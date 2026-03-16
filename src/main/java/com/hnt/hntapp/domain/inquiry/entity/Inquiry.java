package com.hnt.hntapp.domain.inquiry.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "inquiries")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Inquiry {

    /** 문의 고유 ID (UUID) */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 소속 가맹점 (FK)
     * - 어느 개맹점의 문의 인지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /** 문의 작성사 (FK)
     * - 가맹점주 또는 직원
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 문의 제목 (필수)
     */
    @Column(nullable = false)
    private String title;

    /** 문의 내용 (필수) */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 관리자 답변 (답변 전 null) */
    @Column(columnDefinition = "TEXT")
    private String answer;

    /** 문의 상태
     * - 접수: 문의 등록 직후
     * - 처리중: 관리자 확인
     * - 완료 : 답변 완료
     */
    @Builder.Default
    private String status = "접수";

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;

}
