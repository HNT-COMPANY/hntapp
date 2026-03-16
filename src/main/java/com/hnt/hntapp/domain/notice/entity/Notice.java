package com.hnt.hntapp.domain.notice.entity;

import com.hnt.hntapp.domain.user.entity.User;
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
 * 공지사항 엔티티
 * - 본사 관리자(ADMIN)가 작성
 * - 전체 가맹점주/직원에게 공지
 * - 발행 여부로 공개/비공개 관리
 */
@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    /** 공지 고유 ID ( UUID )*/
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 공지 제목 (필수) */
    @Column(nullable = false)
    private String title;

    /** 공지 내용 (필수) */
    @Column(nullable = false)
    private String content;

    /** 작성사 (FK)
     * - 본사 관리자 ( ADMIN만 작성 가능
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createBy;

    /**
     * 발행 여부
     * - false: 임시저장 (가맹점주 비공개)
     * - true: 발행 (가맹점주 공개)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 (자동 갱신) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
