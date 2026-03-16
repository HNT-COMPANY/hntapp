package com.hnt.hntapp.domain.changelog.entity;

import com.hnt.hntapp.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 변경 로그 엔티티
 * - 정책 / 정산 / 재고 수정 이력 전담 관리
 * - updated_by FK 대신 이 테이블 하나로 모든 수정 이력 통합
 * - 누가 / 언제 / 무엇을 / 어떻게 바꿨는지 추적
 */
@Entity
@Table(name = "change_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLog {

    /** 로그 고유 ID (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 변경된 테이블명
     * - 예: policies / settlements / inventories
     */
    @Column(nullable = false)
    private String targetTable;

    /**
     * 변경된 레코드 ID (UUID)
     * - 어떤 레코드가 변경됐는지
     */
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID targetId;

    /** 변경된 필드명 (예: status / policyValue) */
    @Column(nullable = false)
    private String fieldName;

    /** 변경 전 값 */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /** 변경 후 값 */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * 변경한 사용자 (FK)
     * - 누가 수정했는지 추적
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    /** 등록일 (자동 생성, 수정일 없음 - 로그는 불변) */
    @CreationTimestamp
    private LocalDateTime createdAt;
}