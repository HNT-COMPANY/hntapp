package com.hnt.hntapp.domain.attendance.entity;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 출퇴근 엔티티
 * - 가맹점 직원 / 가맹점주 / 본사 직원 출퇴근 관리
 * - GPS 좌표로 위치 검증 후 출퇴근 처리
 * - 지각 / 조퇴 / 결근 / 반차 상태 관리
 */
@Entity
@Table(name = "attendances")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    /** 출퇴근 고유 ID (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 출퇴근 직원 (FK)
     * - 누가 출퇴근 했는지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 소속 가맹점 (FK)
     * - 어느 가맹점에서 출퇴근 했는지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id", nullable = false)
    private Franchise franchise;

    /** 출퇴근 날짜 */
    @Column(nullable = false)
    private LocalDate workDate;

    /** 출근 시간 */
    private LocalDateTime checkInTime;

    /** 출근 시 GPS 위도 */
    private Double checkInLat;

    /** 출근 시 GPS 경도 */
    private Double checkInLng;

    /** 퇴근 시간 */
    private LocalDateTime checkOutTime;

    /** 퇴근 시 GPS 위도 */
    private Double checkOutLat;

    /** 퇴근 시 GPS 경도 */
    private Double checkOutLng;

    /**
     * 출퇴근 상태
     * - 정상: 정시 출퇴근
     * - 지각: 출근 시간 초과
     * - 조퇴: 퇴근 시간 미달
     * - 결근: 출근 기록 없음
     * - 반차: 반일 근무
     * - 위치불일치: GPS 범위 벗어남
     */
    @Builder.Default
    private String status = "정상";

    /** 메모 (특이사항 기록) */
    private String memo;

    /**
     * 승인자 (FK)
     * - 출퇴근 승인한 관리자
     * - 자동 승인 시 null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 (자동 갱신) */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}