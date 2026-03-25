package com.hnt.hntapp.domain.warehouse.repository;

import com.hnt.hntapp.domain.warehouse.entity.PhoneModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneModelRepository extends JpaRepository<PhoneModel, UUID> {

    /** 활성화된 모델만 조회 */
    List<PhoneModel> findByIsActiveTrue();

    /** 제조사별 조회 */
    List<PhoneModel> findByMakerAndIsActiveTrue(String maker);

    /** 모델명 중복 체크 */
    boolean existsByMakerAndName(String maker, String name);

    /** 모델 + 용량 + 컬러 한번에 fetch */
    @Query("SELECT DISTINCT m FROM PhoneModel m " +
            "LEFT JOIN FETCH m.storages s " +
            "LEFT JOIN FETCH s.colors " +
            "WHERE m.isActive = true " +
            "ORDER BY m.maker, m.name")
    List<PhoneModel> findAllWithStoragesAndColors();
}
