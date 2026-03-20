package com.hnt.hntapp.domain.franchise.dto;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.franchise.entity.FranchiseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 가맹점 응답 DTO
 * - Entity → DTO 변환
 */
@Getter
@Builder
public class FranchiseResponseDto {

    private UUID id;
    private String name;
    private String ownerName;
    private String region;
    private String address;
    private String phoneNumber;
    private Double lat;
    private Double lng;
    private Integer gpsRadius;
    private FranchiseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Franchise Entity -> ResponseDto 변환 */
    public static FranchiseResponseDto from(Franchise franchise) {
        return FranchiseResponseDto.builder()
                .id(franchise.getId())
            .name(franchise.getName())
            .ownerName(franchise.getOwnerName())
            .region(franchise.getRegion())
            .address(franchise.getAddress())
            .phoneNumber(franchise.getPhoneNumber())
            .lat(franchise.getLat())
            .lng(franchise.getLng())
            .gpsRadius(franchise.getGpsRadius())
            .status(franchise.getStatus())
            .createdAt(franchise.getCreatedAt())
            .updatedAt(franchise.getUpdatedAt())
            .build();
    }
}