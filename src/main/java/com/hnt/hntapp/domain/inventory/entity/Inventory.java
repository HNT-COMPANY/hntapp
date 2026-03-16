package com.hnt.hntapp.domain.inventory.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 엔티티
 * - 가맹점별 상품 재고 현황 관리
 * - 안전재고 미달 시 긴급/부족 상태로 변경
 * - 수정 이력은 change_logs 테이블에서 관리
 */
@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    /** 재고 고유 ID (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 소속 가맹점 (FK)
     * - 어느 가맹점의 재고인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /**
     * 상품 (FK)
     * - 어떤 상품의 재고인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** 현재 재고 수량 */
    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;

    /** 안전 재고 수량 (이 수량 이하면 부족 상태) */
    @Column(nullable = false)
    @Builder.Default
    private Integer safeStock = 0;

    /**
     * 재고 상태
     * - 정상: currentStock > safeStock
     * - 부족: currentStock <= safeStock
     * - 긴급: currentStock == 0
     */
    private String status;

    /** 메모 (특이사항 기록) */
    private String memo;

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 (자동 갱신) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}