package com.hnt.hntapp.domain.warehouse.dto;

import com.hnt.hntapp.domain.warehouse.entity.PhoneColor;
import com.hnt.hntapp.domain.warehouse.entity.PhoneModel;
import com.hnt.hntapp.domain.warehouse.entity.PhoneStorage;
import com.hnt.hntapp.domain.warehouse.entity.WarehouseStock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarehouseDto {

    // ──────────────────────────────────────────
    // 요청 DTO
    // ──────────────────────────────────────────

    /** 모델 등록 요청 */
    public record CreateModelRequest(
            @NotBlank String maker,
            @NotBlank String name,
            List<CreateStorageRequest> storages
    ) {}

    /** 용량 등록 요청 — maker/name 제거, capacity/releasePrice/colors 로 수정 */
    public record CreateStorageRequest(
            @NotBlank String capacity,
            Long releasePrice,
            List<CreateColorRequest> colors
    ) {}

    /** 컬러 등록 요청 */
    public record CreateColorRequest(
            @NotBlank String colorName,
            String hexCode
    ) {}

    /** 가맹점별 배분 요청 */
    public record DistributeRequest(
            @NotNull UUID franchiseId,
            @NotNull UUID phoneColorId,
            @NotNull @PositiveOrZero Integer qty
    ) {}

    /** 출고가 수정 요청 */
    public record UpdatePriceRequest(
            @NotNull Long releasePrice
    ) {}

    // ──────────────────────────────────────────
    // 응답 DTO
    // ──────────────────────────────────────────

    @Getter
    @Builder
    public static class ModelResponse {
        private UUID   id;
        private String maker;
        private String name;
        private Boolean isActive;
        private List<StorageResponse> storages;
        private LocalDateTime createdAt;

        public static ModelResponse from(PhoneModel m) {
            return ModelResponse.builder()
                    .id(m.getId())
                    .maker(m.getMaker())
                    .name(m.getName())
                    .isActive(m.getIsActive())
                    .storages(m.getStorages().stream()
                            .map(StorageResponse::from)
                            .collect(Collectors.toList()))
                    .createdAt(m.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StorageResponse {
        private UUID   id;
        private String capacity;
        private Long   releasePrice;
        private List<ColorResponse> colors;

        public static StorageResponse from(PhoneStorage s) {
            return StorageResponse.builder()
                    .id(s.getId())
                    .capacity(s.getCapacity())
                    .releasePrice(s.getReleasePrice())
                    .colors(s.getColors().stream()
                            .map(ColorResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ColorResponse {
        private UUID   id;
        private String colorName;
        private String hexCode;

        public static ColorResponse from(PhoneColor c) {
            return ColorResponse.builder()
                    .id(c.getId())
                    .colorName(c.getColorName())
                    .hexCode(c.getHexCode())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StockResponse {
        private UUID    id;
        private UUID    franchiseId;
        private String  franchiseName;
        private String  maker;
        private String  modelName;
        private String  capacity;
        private Long    releasePrice;
        private String  colorName;
        private String  hexCode;
        private Integer officialQty;
        private Integer currentQty;
        private String  status;
        private String  statusLabel;

        public static StockResponse from(WarehouseStock ws) {
            PhoneColor   color   = ws.getPhoneColor();
            PhoneStorage storage = color.getPhoneStorage();
            PhoneModel   model   = storage.getPhoneModel();

            return StockResponse.builder()
                    .id(ws.getId())
                    .franchiseId(ws.getFranchise().getId())
                    .franchiseName(ws.getFranchise().getName())
                    .maker(model.getMaker())
                    .modelName(model.getName())
                    .capacity(storage.getCapacity())
                    .releasePrice(storage.getReleasePrice())
                    .colorName(color.getColorName())
                    .hexCode(color.getHexCode())
                    .officialQty(ws.getOfficialQty())
                    .currentQty(ws.getCurrentQty())
                    .status(ws.getStatus().name())
                    .statusLabel(ws.getStatus().getLabel())
                    .build();
        }
    }
}
