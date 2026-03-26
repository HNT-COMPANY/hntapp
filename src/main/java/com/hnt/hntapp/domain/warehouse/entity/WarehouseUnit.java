package com.hnt.hntapp.domain.warehouse.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "warehouse_units", uniqueConstraints = @UniqueConstraint(
        columnNames = "serial_number",
        name = "uq_serial_number"
))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    // 일련번호 (6~10자리 자체 번호) - 전체 유니크
    @Column(name = "serial_number", nullable = false, length = 20)
    private String serialNumber;

    // 모델 정보 (PhoneColor FK → 모델/용량/컬러 계층 접근)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_color_id", nullable = false)
    private PhoneColor phoneColor;

    // 보관 가맹점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    // 입고 거래처 (라온, 한빛, 에이딘 등)
    @Column(nullable = false)
    private String dealer;

    // 통신사 (거래처로 자동 구분)
    @Column(nullable = false, length = 10)
    private String carrier;

    // 입고일
    @Column(nullable = false)
    private LocalDate stockedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UnitStatus status = UnitStatus.STOCK;

    // 판매된 전표 ID (판매 시 연결)
    @Column(name = "activation_id")
    private UUID activationId;

    // 판매일
    private LocalDate soldAt;

    // 이관된 가맹점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferred_to_franchise_id")
    private Franchise transferredToFranchise;

    // 이관일
    private LocalDate transferredAt;

    // 메모 (분실 사유 등)
    private String memo;

    // 1차 검수 완료 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean firstAuditPassed = false;

    // 1차 검수 일시
    private LocalDateTime firstAuditAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ──────────────────────────────────────────
    // 비즈니스 메서드
    // ──────────────────────────────────────────

    /** 1차 검수 통과 */
    public void passFirstAudit() {
        this.firstAuditPassed = true;
        this.firstAuditAt = LocalDateTime.now();
    }

    /** 판매 처리 — 전표 연결 + STOCK → SOLD */
    public void sell(UUID activationId) {
        if (this.status != UnitStatus.STOCK) {
            throw new IllegalStateException(
                    "입고 상태의 기기만 판매할 수 있습니다. 현재 상태: " + this.status.getLabel());
        }
        this.status = UnitStatus.SOLD;           // ← SOLD 로 수정
        this.activationId = activationId;
        this.soldAt = LocalDate.now();
    }

    /** 분실 처리 */
    public void markAsLost(String reason) {
        this.status = UnitStatus.LOST;
        this.memo = reason;
    }

    /** 이관 처리 */
    public void transfer(Franchise toFranchise) {
        if (this.status != UnitStatus.STOCK) {
            throw new IllegalStateException(
                    "입고 상태의 기기만 이관할 수 있습니다.");
        }
        this.status = UnitStatus.TRANSFERRED;
        this.transferredToFranchise = toFranchise;
        this.transferredAt = LocalDate.now();
    }

    /** 가맹점 변경 (이관 후 새 가맹점 소속) */
    public void changeFranchise(Franchise newFranchise) {
        this.franchise = newFranchise;
        this.status = UnitStatus.STOCK;
    }
}
