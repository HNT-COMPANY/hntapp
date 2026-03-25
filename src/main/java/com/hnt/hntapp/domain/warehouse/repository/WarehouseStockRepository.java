package com.hnt.hntapp.domain.warehouse.repository;

import com.hnt.hntapp.domain.warehouse.entity.StockStatus;
import com.hnt.hntapp.domain.warehouse.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, UUID> {

    /** 가맹점 전체 재고 조회 */
    @Query("SELECT ws FROM WarehouseStock ws " +
            "JOIN FETCH ws.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "WHERE ws.franchise.id = :franchiseId " +
            "ORDER BY m.maker, m.name, s.capacity, c.colorName")
    List<WarehouseStock> findByFranchiseId(@Param("franchiseId") UUID franchiseId);

    /** 특정 가맹점 × 컬러 재고 조회 */
    Optional<WarehouseStock> findByFranchiseIdAndPhoneColorId(
            UUID franchiseId, UUID phoneColorId);

    /** 가맹점 품절/부족 재고 조회 */
    @Query("SELECT ws FROM WarehouseStock ws " +
            "JOIN FETCH ws.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "WHERE ws.franchise.id = :franchiseId " +
            "AND ws.status != 'NORMAL'")
    List<WarehouseStock> findLowOrOutOfStock(@Param("franchiseId") UUID franchiseId);

    /** 전체 가맹점 재고 현황 (관리자용) */
    @Query("SELECT ws FROM WarehouseStock ws " +
            "JOIN FETCH ws.franchise f " +
            "JOIN FETCH ws.phoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "ORDER BY f.name, m.maker, m.name")
    List<WarehouseStock> findAllWithDetails();

    /** 특정 상태 재고 조회 */
    List<WarehouseStock> findByStatus(StockStatus status);
}
