package com.hnt.hntapp.domain.user.repository;

import com.hnt.hntapp.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    /**
     * 이메일로 사용자 조회
     * - 로그인 시 이메일로 사용자 찾을 때 사용
     * - Optional: 사용자가 없을 수도 있어서 null 안전하게 처리
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 중복 체크
     * - 회원가입 시 이미 사용중인 이메일인지 확인
     * - true: 이미 존재 / false: 사용 가능
     */
    boolean existsByEmail(String email);
}
