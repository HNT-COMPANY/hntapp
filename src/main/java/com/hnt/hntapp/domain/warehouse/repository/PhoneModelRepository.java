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

    /**
     * 모델 + 용량 fetch (1단계)
     * MultipleBagFetchException 방지:
     * storages 와 colors 를 동시에 JOIN FETCH 하면 안 됨
     * → 1단계: 모델 + 용량만 fetch
     * → 2단계: 용량 + 컬러 fetch (WarehouseService 에서 별도 호출)
     */
    @Query("SELECT DISTINCT m FROM PhoneModel m " +
            "LEFT JOIN FETCH m.storages s " +
            "WHERE m.isActive = true " +
            "ORDER BY m.maker, m.name")
    List<PhoneModel> findAllWithStorages();

    /**
     * 모델 + 용량 + 컬러 한번에 fetch
     * JPQL 대신 EntityGraph 방식으로 변경
     */
    @Query("SELECT DISTINCT m FROM PhoneModel m " +
            "LEFT JOIN FETCH m.storages " +
            "WHERE m.isActive = true")
    List<PhoneModel> findAllWithStoragesAndColors();
}
