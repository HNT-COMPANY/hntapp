package com.hnt.hntapp.domain.warehouse.entity;

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

@Entity
@Table(
        name = "warehouse_stocks",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"franchise_id", "phone_color_id"},
                name = "uq_warehouse_stock"
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_color_id", nullable = false)
    private PhoneColor phoneColor;

    /** 공식 재고 수량 (본사 배분 확정값) */
    @Column(nullable = false)
    @Builder.Default
    private Integer officialQty = 0;

    /** 현재 재고 수량 (실시간) */
    @Column(nullable = false)
    @Builder.Default
    private Integer currentQty = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StockStatus status = StockStatus.NORMAL;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ──────────────────────────────────────────
    // 비즈니스 메서드
    // ──────────────────────────────────────────

    /** 본사 배분 확정 — officialQty + currentQty 동시 설정 */
    public void confirmStock(int qty) {
        this.officialQty = qty;
        this.currentQty  = qty;
        updateStatus();
    }

    /** 재고 추가 — 입고 또는 보류 시 복구 */
    public void addStock(int qty) {
        this.currentQty += qty;
        updateStatus();
    }

    /** 재고 차감 — 전표 제출 시 호출 */
    public void deductStock(int qty) {
        if (this.currentQty < qty) {
            throw new IllegalStateException(
                    "재고가 부족합니다. 현재: " + this.currentQty + " / 요청: " + qty);
        }
        this.currentQty -= qty;
        updateStatus();
    }

    private void updateStatus() {
        if (this.currentQty == 0) {
            this.status = StockStatus.OUT_OF_STOCK;
        } else if (this.currentQty <= 2) {
            this.status = StockStatus.LOW;
        } else {
            this.status = StockStatus.NORMAL;
        }
    }
}
