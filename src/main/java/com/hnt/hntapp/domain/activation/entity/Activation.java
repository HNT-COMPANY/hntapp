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

    /** 개통처 (소속 가맹점) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /** 작성자 (가맹점주/직원) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    /** 전표 날짜 */
    @Column(nullable = false)
    private LocalDate activationDate;

    /** 통신사 (거래처 선택 시 자동 구분) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(10)")
    private Carrier carrier;

    /** 거래처 (라온, 한빛, 에이딘 등) */
    @Column(nullable = false)
    private String dealer;

    /** 유입처 (내방, 온라인 등) */
    private String inflowPath;

    // ── 고객 정보 ──────────────────────────────

    @Column(nullable = false)
    private String customerName;

    private String birthDate;       // 생년월일 6자리 (011127)

    @Column(nullable = false)
    private String phoneNumber;

    // ── 개통 정보 ──────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private ActivationType activationType;  // 신규/기기변경/번호이동

    /** 단말기 (PhoneColor FK — 모델→용량→컬러 계층) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_color_id")
    private PhoneColor phoneColor;

    private String usim;              // 유심
    private String plan;              // 요금제
    private String additionalService; // 부가서비스
    private String insurance;         // 보험
    private String contract;          // 약정

    // ── 금액 정보 ──────────────────────────────

    private Long    releasePrice;       // 출고가
    private Long    publicSupport;      // 공시지원금
    private Long    distSupport;        // 유통망지원금
    private Long    prepayment;         // 선납금
    private Long    margin;             // 마진
    private String  marginDetail;       // 마진 상세
    private Long    netPrice;           // 넷가
    private Long    sellPrice;          // 판매가
    private Integer installmentMonths;  // 할부 개월 (0 = 일시불)
    private Long    deduction;          // 차감
    private String  deductionDetail;    // 차감 상세

    // ── 추가 정보 ──────────────────────────────

    private Boolean hasReview;          // 후기작성 여부
    private Boolean hasAdditionalAuth;  // 부가인증 유무
    private Long    receptionFee;       // 접수비
    private Long    commission;         // 수수료 (음수 가능)
    private Long    realMargin;         // 실마진 (자동계산)

    // ── 전표 상태 ──────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Builder.Default
    private ActivationStatus status = ActivationStatus.DRAFT;

    private String rejectReason;  // 보류 사유

    // 검토자 (본사 관리자)
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

    /** 실마진 자동 계산: 마진 + 수수료 - 차감 */
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
