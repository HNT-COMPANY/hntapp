package com.hnt.hntapp.domain.warehouse.dto;

import com.hnt.hntapp.domain.warehouse.entity.UnitStatus;
import com.hnt.hntapp.domain.warehouse.entity.WarehouseUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WarehouseUnitDto {

    // ── 요청 DTO ──────────────────────────────

    /** 기기 일괄 등록 요청 */
    public record RegisterRequest(          // RegosterRequest → RegisterRequest 오타 수정
            @NotNull  UUID         franchiseId,
            @NotNull  UUID         phoneColorId,
            @NotBlank String       dealer,
            @NotNull  LocalDate    stockedAt,
            @NotEmpty List<String> serialNumbers
    ) {}

    /** 1차 검수 실패 요청 */
    public record FirstAuditFailRequest(
            @NotBlank String serialNumber,
            @NotBlank String mismatchDetail
    ) {}

    /** 상태 변경 요청 */
    public record StatusChangeRequest(
            @NotBlank String     serialNumber,
            @NotNull  UnitStatus status,
            String memo
    ) {}

    // ── 응답 DTO ──────────────────────────────

    @Getter
    @Builder
    public static class UnitResponse {
        private UUID          id;
        private String        serialNumber;
        private String        maker;
        private String        modelName;
        private String        capacity;
        private String        colorName;
        private String        hexCode;
        private String        franchiseName;
        private String        dealer;
        private String        carrier;
        private LocalDate     stockedAt;
        private String        status;
        private String        statusLabel;
        private Boolean       firstAuditPassed;
        private LocalDateTime firstAuditAt;
        private UUID          activationId;
        private LocalDate     soldAt;

        public static UnitResponse from(WarehouseUnit u) {
            var color   = u.getPhoneColor();
            var storage = color.getPhoneStorage();
            var model   = storage.getPhoneModel();

            return UnitResponse.builder()
                    .id(u.getId())
                    .serialNumber(u.getSerialNumber())
                    .maker(model.getMaker())
                    .modelName(model.getName())
                    .capacity(storage.getCapacity())
                    .colorName(color.getColorName())
                    .hexCode(color.getHexCode())
                    .franchiseName(u.getFranchise().getName())
                    .dealer(u.getDealer())
                    .carrier(u.getCarrier())
                    .stockedAt(u.getStockedAt())
                    .status(u.getStatus().name())
                    .statusLabel(u.getStatus().getLabel())
                    .firstAuditPassed(u.getFirstAuditPassed())
                    .firstAuditAt(u.getFirstAuditAt())
                    .activationId(u.getActivationId())
                    .soldAt(u.getSoldAt())
                    .build();
        }
    }

    /** 가맹점 재고 요약 */
    @Getter
    @Builder
    public static class StockSummary {
        private UUID   franchiseId;
        private String franchiseName;
        private long   stockCount;
        private long   soldCount;
        private long   lostCount;
        private long   totalCount;
    }
}
