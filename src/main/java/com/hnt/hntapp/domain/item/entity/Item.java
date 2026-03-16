package com.hnt.hntapp.domain.item.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 상품 마스터 엔티티
 * - 전체 상품 기준 정보 관리
 * - 재고(Inventory)에서 이 테이블을 참조
 * - 상품 추가/수정은 본사 관리자(ADMIN)만 가능
 */
@Entity
@Getter
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    /** 상품 고유 ID (UUID) */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 상품명 (필수) */
    @Column(nullable = false)
    private String name;

    /** 상품 카테고리*/
    private String category;

    /** 단위 */
    private String unit;

    /** 등록일 (자동 생성 ) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일  ( 자동 생성 ) */
    @CreationTimestamp
    private LocalDateTime updatedAt;

}
