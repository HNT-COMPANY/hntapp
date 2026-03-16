package com.hnt.hntapp.domain.settlement.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** 정산 엔티티
 * - 가맹점별 월간 정산 보고 관리
 * - 총액 / 지급액 /미지급액 포함
 * - 수정 이력은 change_logs 테이블에서 관리
 */
@Entity
@Table(name = "settlements")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {

    /** 정산 고유 ID ( UUID ) */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 소속 가맹점 (FK)
     * - 어느 가맹점의 정산인지 체크!
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /** 정산 월 (예: 2026-03-01) */
    @Column(nullable = false)
    private LocalDateTime settlementMonth;

    /** 총 정산 금액 */
    @Column(nullable = false)
    private BigDecimal totalAmount;

    /** 지급 완료 금액 */
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /** 미지급 금액 */
    @Builder.Default
    private BigDecimal unpaidAmount = BigDecimal.ZERO;

    /**
     * 정산 상태
     * - 대기 : 정산 미처리
     * - 완료 : 지급 완료
     * - 미납 : 미지급 존재
     */
    private String status;

    /** 메모 ( 특이사항 기록 ) */
    private String memo;

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 (자동 갱신) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
