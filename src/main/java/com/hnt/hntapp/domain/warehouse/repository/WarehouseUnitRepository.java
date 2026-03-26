package com.hnt.hntapp.domain.warehouse.repository;

import com.hnt.hntapp.domain.warehouse.entity.UnitStatus;
import com.hnt.hntapp.domain.warehouse.entity.WarehouseUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseUnitRepository extends JpaRepository<WarehouseUnit, UUID> {

    /** 일련번호로 단건 조회 */
    Optional<WarehouseUnit> findBySerialNumber(String serialNumber);

    /** 일련번호 중복 체크 */
    boolean existsBySerialNumber(String serialNumber);

    /** 가맹점 전체 기기 조회 */
    @Query("SELECT u FROM WarehouseUnit u " +
            "JOIN FETCH u.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "WHERE u.franchise.id = :franchiseId " +
            "ORDER BY u.stockedAt DESC")
    List<WarehouseUnit> findByFranchiseId(@Param("franchiseId") UUID franchiseId);

    /** 가맹점 + 상태별 조회 */
    @Query("SELECT u FROM WarehouseUnit u " +
            "JOIN FETCH u.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "WHERE u.franchise.id = :franchiseId " +
            "AND u.status = :status " +
            "ORDER BY u.stockedAt DESC")
    List<WarehouseUnit> findByFranchiseIdAndStatus(
            @Param("franchiseId") UUID franchiseId,
            @Param("status") UnitStatus status);

    /** 가맹점 입고 기기 수량 (수량 조회는 COUNT로) */
    @Query("SELECT COUNT(u) FROM WarehouseUnit u " +
            "WHERE u.franchise.id = :franchiseId " +
            "AND u.status = :status")
    long countByFranchiseIdAndStatus(
            @Param("franchiseId") UUID franchiseId,
            @Param("status") UnitStatus status);

    /** 전체 가맹점 재고 현황 (관리자) */
    @Query("SELECT u FROM WarehouseUnit u " +
            "JOIN FETCH u.franchise f " +
            "JOIN FETCH u.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "ORDER BY f.name, m.name")
    List<WarehouseUnit> findAllWithDetails();

    /** 전표 ID로 연결된 기기 조회 */
    Optional<WarehouseUnit> findByActivationId(UUID activationId);

    /** 1차 검수 미완료 기기 조회 (가맹점) */
    List<WarehouseUnit> findByFranchiseIdAndFirstAuditPassedFalse(UUID franchiseId);
}
