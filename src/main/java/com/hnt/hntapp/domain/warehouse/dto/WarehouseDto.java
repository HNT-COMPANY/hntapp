package com.hnt.hntapp.domain.warehouse.dto;

import com.hnt.hntapp.domain.warehouse.entity.PhoneColor;
import com.hnt.hntapp.domain.warehouse.entity.PhoneModel;
import com.hnt.hntapp.domain.warehouse.entity.PhoneStorage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarehouseDto {

    // ── 요청 DTO ──────────────────────────────

    public record CreateModelRequest(
            @NotBlank String maker,
            @NotBlank String name,
            List<CreateStorageRequest> storages
    ) {}

    public record CreateStorageRequest(
            @NotBlank String capacity,
            Long releasePrice,
            List<CreateColorRequest> colors
    ) {}

    public record CreateColorRequest(
            @NotBlank String colorName,
            String hexCode
    ) {}

    // ── 응답 DTO ──────────────────────────────

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
}
