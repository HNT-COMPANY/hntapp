package com.hnt.hntapp.domain.warehouse.repository;

import com.hnt.hntapp.domain.warehouse.entity.PhoneColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhoneColorRepository extends JpaRepository<PhoneColor, UUID> {

    /**
     * 컬러 단건 조회 — storage + model 함께 fetch
     * WarehouseService.findColor() 에서 사용
     * 기존: 전체 모델 로드 후 stream 필터링 → O(N)
     * 개선: 직접 id 조회 → O(1)
     */
    @Query("SELECT c FROM PhoneColor c " +
            "JOIN FETCH c.phoneStorage s " +
            "JOIN FETCH s.phoneModel m " +
            "WHERE c.id = :colorId")
    Optional<PhoneColor> findByIdWithStorageAndModel(@Param("colorId") UUID colorId);
}
