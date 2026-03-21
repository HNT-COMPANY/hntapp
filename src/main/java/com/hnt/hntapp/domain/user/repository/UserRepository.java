package com.hnt.hntapp.domain.user.repository;

import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 Repository
 * - JpaRepository 상속으로 기본 CRUD 자동 제공
 * - 로그인 시 이메일로 사용자 조회
 * - 이메일 중복 체크
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /** 이메일로 사용자 조회 (로그인 시 사용) */
    Optional<User> findByEmail(String email);

    /** 권한별 사용자 목록 조회 */
    List<User> findByRole(Role role);

    /** 상태별 사용자 목록 */
    List<User> findByStatus(UserStatus status);

    /** 가맹점별 사용자 목록 조회 */
    List<User> findByFranchiseId(UUID franchiseId);

    /** 활성화된 사용자만 조회 */
    List<User> findByIsActive(Boolean isActive);
}
