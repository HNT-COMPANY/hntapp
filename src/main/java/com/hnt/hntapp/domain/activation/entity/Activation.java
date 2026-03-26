package com.hnt.hntapp.domain.activation.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.warehouse.entity.PhoneColor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 개통 전표 엔티티
 *
 * 변경사항:
 * - serialNumber 필드 추가 (일련번호 직접 저장)
 * - 전표 제출 시 WarehouseUnit.sell() 호출 → STOCK → SOLD
 * - 전표 보류 시 재고 복구 불필요 (Unit 상태만 되돌리면 됨)
 * - 일 마감은 별도 검토 없이 그냥 저장 (누적)
 * - 월 정산 시 2차 검수에서 일련번호 대조
 */
@Entity
@Table(name = "activations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    // ── 기본 정보 ──────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private LocalDate activationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(10)")
    private Carrier carrier;

    @Column(nullable = false)
    private String dealer;

    private String inflowPath;

    // ── 고객 정보 ──────────────────────────────

    @Column(nullable = false)
    private String customerName;

    private String birthDate;

    @Column(nullable = false)
    private String phoneNumber;

    // ── 개통 정보 ──────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private ActivationType activationType;

    /** 단말기 컬러 (모델/용량/컬러 계층 접근용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_color_id")
    private PhoneColor phoneColor;

    /**
     * 판매 기기 일련번호
     * - WarehouseUnit.serialNumber 참조 (FK 대신 직접 저장 — 추적 용이)
     * - 2차 검수 시 이 값으로 warehouse_units 대조
     */
    @Column(name = "serial_number", length = 20)
    private String serialNumber;

    private String usim;
    private String plan;
    private String additionalService;
    private String insurance;
    private String contract;

    // ── 금액 정보 ──────────────────────────────

    private Long    releasePrice;
    private Long    publicSupport;
    private Long    distSupport;
    private Long    prepayment;
    private Long    margin;
    private String  marginDetail;
    private Long    netPrice;
    private Long    sellPrice;
    private Integer installmentMonths;
    private Long    deduction;
    private String  deductionDetail;

    // ── 추가 정보 ──────────────────────────────

    private Boolean hasReview;
    private Boolean hasAdditionalAuth;
    private Long    receptionFee;
    private Long    commission;
    private Long    realMargin;

    // ── 전표 상태 ──────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Builder.Default
    private ActivationStatus status = ActivationStatus.DRAFT;

    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ──────────────────────────────────────────
    // 비즈니스 메서드
    // ──────────────────────────────────────────

    /** 실마진 자동 계산 */
    public void calcRealMargin() {
        long m = margin     != null ? margin     : 0L;
        long c = commission != null ? commission : 0L;
        long d = deduction  != null ? deduction  : 0L;
        this.realMargin = m + c - d;
    }

    /** 전표 제출 */
    public void submit() {
        this.status = ActivationStatus.SUBMITTED;
    }

    /** 본사 승인 */
    public void approve(User reviewer) {
        this.status     = ActivationStatus.APPROVED;
        this.reviewer   = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }

    /** 본사 보류 */
    public void reject(User reviewer, String reason) {
        this.status       = ActivationStatus.REJECTED;
        this.reviewer     = reviewer;
        this.reviewedAt   = LocalDateTime.now();
        this.rejectReason = reason;
    }

    /** 보류 후 재제출 */
    public void resubmit() {
        if (this.status != ActivationStatus.REJECTED) {
            throw new IllegalStateException("보류 상태의 전표만 재제출할 수 있습니다.");
        }
        this.status       = ActivationStatus.SUBMITTED;
        this.rejectReason = null;
        this.reviewer     = null;
        this.reviewedAt   = null;
    }
}
