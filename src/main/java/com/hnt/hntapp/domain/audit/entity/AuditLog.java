package com.hnt.hntapp.domain.audit.entity;

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

@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    // 검수 유형 1차/2차
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditType auditType;

    // 대상 가맹점
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    // 검수 대상 일련번호
    @Column(name = "serial_number", nullable = false, length = 20)
    private String serialNumber;

    // 연관 전표 2차 검수
    @Column(name = "activation_id")
    private UUID activationId;

    // 정산 년월 ( 2차 검수시 - yyyymm )
    private String settlementMonth;

    // 검수 결과
    @Column(nullable = false)
    private Boolean passed;

    // 불일치 내용
    private String mismatchDetail;

    // 소명 내용 ( 가맹점주 작성 )
    private String clarification;

    // 소명 일시
    private LocalDateTime clarifiedAt;

    // 처리 결과 (인정/조치/미처리)
    @Column(length = 20)
    private String resolution;

    // 검수자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id")
    private User auditor;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


    // -- 비즈니스 메서드

    // 소명 등록
    public void submitClarification(String content) {
        this.clarification = content;
        this.clarifiedAt = LocalDateTime.now();
    }

    // 처리 결과
    public void resolve(String resolution) {
        this.resolution = resolution;
    }





}
