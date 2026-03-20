package com.hnt.hntapp.domain.franchise.repository;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 가맹점 Repository
 * - 기본 CRUD 자동 제공
 * - 모든 코드는 AI와 리팩토링 작업 & 코드 개선 작업을 하였습니다.
 * - 코드 마다 주석이 있는 이유는 AI와 함께 코드 개선 했기 때문입니다.
 */
@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, UUID> {

    /** 지격별 가맹점 조회하기 **/
    List<Franchise> findByRegion(String region);

    /** 상태별 가맹점 조회하기 **/
    List<Franchise> findByStatus(String status);

    /** 가맹점명 중복 체크 */
    boolean existsByName(String name);
}
