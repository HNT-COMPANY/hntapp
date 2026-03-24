package com.hnt.hntapp.domain.user.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 엔티티
 * - 본사 관리자 / 가맹점주 / 직원 / 고객 모두 관리
 * - role 필드로 권한 구분
 * - 가맹점주/직원은 franchise_id로 소속 가맹점 연결
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** 사용자 고유 ID */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 이메일 ( 로그인 ID로 사용 될 예정, 중복 불가 ) */
    @Column(nullable = false, unique = true)
    private String email;

    /** 비밀번호 (암호화된 해시값 저장 ) */
    @Column(nullable = false)
    private String passwordHash;

    /** 사용자 이름 */
    private String name;

    /** 사용자 번호 */
    private String phone;

    /** 권한 ( ADMIN, FRANCHISEE, STAFF, CUSTOMER) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * 소속 가맹점 (FK)
     * - ADMIN: null (본사 소속)
     * - FRANCHISEE / STAFF: 소속 가맹점 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    /** 활성화 여부 (탈퇴/정지 시 false) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /** 등록일 (자동 생성 ) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 ( 자동 수정 ) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** 가입 심사 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    /** 가맹점주 권한 부여 */
    public void assignFranchisee(Franchise franchise) {
        this.franchise = franchise;
        this.role = Role.FRANCHISEE;
    }

    /** 가맹점주 권한 회수 → STAFF로 강등 */
    public void revokeFranchisee() {
        this.franchise = null;
        this.role = Role.STAFF;
    }

    /** 회원 정보 수정 */
    public void updateInfo(String name, String phone) {
        if (name  != null) this.name  = name;
        if (phone != null) this.phone = phone;
    }

    /** 소속 가맹점 변경 (STAFF 이동 시) */
    public void changeFranchise(Franchise franchise) {
        this.franchise = franchise;
    }

    /** 승인 */
    public void approve() {
        this.status = UserStatus.APPROVED;
    }

    /** 반려 */
    public void reject() {
        this.status = UserStatus.REJECTED;
    }

    /** 비활성화 */
    public void deactivate() {
        this.isActive = false;
        this.status   = UserStatus.INACTIVE;
    }

}
