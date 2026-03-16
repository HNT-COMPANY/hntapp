package com.hnt.hntapp.domain.policy.entity;


import com.hnt.hntapp.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 정책 엔티티
 * - 가맹점별 정책 정보 관리
 * - 정책 유형 / 금액 / 적용 기간 포함
 * - 수정 이력은 change_logs 테이블에서 관리
 */
@Entity
@Table(name = "policies")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    /** 정책 고유 ID ( UUID ) */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 소속 가맹점 (FK)
     * - 어느 가맹점에 적용되는 정책인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="franchise_id", nullable = false)
    private Franchise franchise;

    /** 정책 이름 (예: 로열티 정책, 광고비 정책) */
    @Column(nullable = false)
    private String policyName;

    /** 정책 유형 (예: 고정금액 / 비율) */
    @Column(nullable = false)
    private String policyType;

    /** 정책 금액 또는 비율 */
    private BigDecimal policyValue;

    /** 정책 적용 시작일 */
    private LocalDate startDate;

    /** 정책 적용 종료일*/
    private LocalDate endDate;

    /** 정책 상태 ( 적용중 / 만료 / 대기 ) */
    private String status;

    /** 등록일 ( 자동 생성 ) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 ( 자동 갱신 ) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
