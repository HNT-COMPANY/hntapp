package com.hnt.hntapp.domain.franchise.entity;


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
 * 가맹점 엔티티
 * - 각 가맹점의 기본 정보를 관리
 * - users, policies, settlements, inventories, inquiries 테이블과 연관
 */
@Entity
@Table(name = "franchises")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Franchise {

    /** 가맹점 고유  ID ( UUID ) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 가맹점 이름 */
    @Column(nullable = false)
    private String name;

    /** 가맹점주 이름 */
    private String ownerName;

    /** 지역 */
    private String region;

    /** 상세 주소 */
    private String address;

    /** 연락처 */
    private String phone;

    /** GPS 위도 (출퇴근 체크용) */
    private Double lat;

    /** GPS 경도 (출퇴근 체크용 */
    private Double lng;

    /** GPS 허용 반경 ( 단위 : M, 기본값 50M )*/
    private Integer gpsRadius;

    /** 가맹점 상태 (운영중 / 대기 / 중지 등 )*/
    private String status;

    /** 등록일 (자동 생성) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 수정일 (자동 갱신 )*/
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
