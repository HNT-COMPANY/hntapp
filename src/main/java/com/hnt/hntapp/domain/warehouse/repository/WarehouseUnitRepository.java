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

    /** 가맹점 입고 기기 수량 */
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

    // ── [추가] 일마감 전표용 재고 조회 ──────────────────────────
    // 이유: 일마감 다이얼로그에서 컬러 선택 시
    //       해당 컬러 + 해당 가맹점의 STOCK 상태 단말만 드롭다운으로 보여줘야 하기 때문
    // colorId  = PhoneColor PK (모델 › 용량 › 컬러 선택 후 넘어오는 값)
    // franchiseId = 현재 로그인한 가맹점 ID (세션에서 자동 주입)
    @Query("SELECT u FROM WarehouseUnit u " +
            "WHERE u.phoneColor.id = :colorId " +
            "AND u.franchise.id = :franchiseId " +
            "AND u.status = 'STOCK' " +
            "ORDER BY u.stockedAt ASC")
    List<WarehouseUnit> findAvailableByColorAndFranchise(
            @Param("colorId") UUID colorId,
            @Param("franchiseId") UUID franchiseId);
}
