package com.hnt.hntapp.domain.franchise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class FranchiseRequestDto {

    /** 가맹점 이름 */
    private final String name;

    /** 가맹점주 이름 */
    private final String ownerName;

    /** 지역 */
    private final String region;

    /** 상세 주소 */
    private final String address;

    /** 연락처 */
    private final String phoneNumber;

    /** GPS 위도 */
    private final Double lat;

    /** GPS 경도 */
    private final Double lng;

    /** GPS 허용 반경 (기본 50M -> 변경 가능 */
    private final Integer gpsRadius;

    public FranchiseRequestDto(String name, String ownerName, String region, String address, String phoneNumber, Double lat, Double lng, Integer gpsRadius) {
        this.name = name;
        this.ownerName = ownerName;
        this.region = region;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.lat = lat;
        this.lng = lng;
        this.gpsRadius = gpsRadius;
    }
}
