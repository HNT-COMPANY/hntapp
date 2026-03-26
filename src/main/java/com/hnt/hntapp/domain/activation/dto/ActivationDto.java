package com.hnt.hntapp.domain.activation.dto;

import com.hnt.hntapp.domain.activation.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActivationDto {

    // ──────────────────────────────────────────
    // 요청 DTO
    // ──────────────────────────────────────────

    public record CreateRequest(
            // 기본 정보
            @NotNull  UUID   franchiseId,
            @NotNull  LocalDate activationDate,
            @NotBlank String dealer,          // 거래처 → 통신사 자동
            String inflowPath,                // 유입처

            // 고객 정보
            @NotBlank String customerName,
            String birthDate,
            @NotBlank String phoneNumber,

            // 개통 정보
            @NotNull  ActivationType activationType,
            UUID   phoneColorId,              // 단말기 (nullable — 기타 기기)
            String serialNumber,
            String usim,
            String plan,
            String additionalService,
            String insurance,
            String contract,

            // 금액
            Long releasePrice,
            Long publicSupport,
            Long distSupport,
            Long prepayment,
            Long margin,
            String marginDetail,
            Long netPrice,
            Long sellPrice,
            Integer installmentMonths,
            Long deduction,
            String deductionDetail,

            // 추가
            Boolean hasReview,
            Boolean hasAdditionalAuth,
            Long receptionFee,
            Long commission,

            // DRAFT or SUBMITTED
            boolean submitNow
    ) {}

    public record RejectRequest(
            @NotBlank String rejectReason
    ) {}

    // ──────────────────────────────────────────
    // 응답 DTO
    // ──────────────────────────────────────────

    @Getter
    @Builder
    public static class Response {
        private UUID   id;
        private LocalDate activationDate;

        // 기본
        private String franchiseName;
        private String writerName;
        private String carrier;
        private String carrierLabel;
        private String dealer;
        private String inflowPath;

        // 고객
        private String customerName;
        private String birthDate;
        private String phoneNumber;

        // 개통
        private String activationType;
        private String activationTypeLabel;
        private String serialNumber;
        private String modelName;
        private String capacity;
        private String colorName;
        private String hexCode;
        private String usim;
        private String plan;
        private String additionalService;
        private String insurance;
        private String contract;

        // 금액
        private Long releasePrice;
        private Long publicSupport;
        private Long distSupport;
        private Long prepayment;
        private Long margin;
        private String marginDetail;
        private Long netPrice;
        private Long sellPrice;
        private Integer installmentMonths;
        private Long deduction;
        private String deductionDetail;

        // 추가
        private Boolean hasReview;
        private Boolean hasAdditionalAuth;
        private Long receptionFee;
        private Long commission;
        private Long realMargin;

        // 상태
        private String status;
        private String statusLabel;
        private String rejectReason;
        private String reviewerName;
        private LocalDateTime reviewedAt;
        private LocalDateTime createdAt;

        public static Response from(Activation a) {
            var b = Response.builder()
                    .id(a.getId())
                    .activationDate(a.getActivationDate())
                    .franchiseName(a.getFranchise().getName())
                    .writerName(a.getWriter().getName())
                    .carrier(a.getCarrier().name())
                    .carrierLabel(a.getCarrier().getLabel())
                    .dealer(a.getDealer())
                    .inflowPath(a.getInflowPath())
                    .customerName(a.getCustomerName())
                    .birthDate(a.getBirthDate())
                    .phoneNumber(a.getPhoneNumber())
                    .activationType(a.getActivationType().name())
                    .activationTypeLabel(a.getActivationType().getLabel())
                    .serialNumber(a.getSerialNumber())
                    .usim(a.getUsim())
                    .plan(a.getPlan())
                    .additionalService(a.getAdditionalService())
                    .insurance(a.getInsurance())
                    .contract(a.getContract())
                    .releasePrice(a.getReleasePrice())
                    .publicSupport(a.getPublicSupport())
                    .distSupport(a.getDistSupport())
                    .prepayment(a.getPrepayment())
                    .margin(a.getMargin())
                    .marginDetail(a.getMarginDetail())
                    .netPrice(a.getNetPrice())
                    .sellPrice(a.getSellPrice())
                    .installmentMonths(a.getInstallmentMonths())
                    .deduction(a.getDeduction())
                    .deductionDetail(a.getDeductionDetail())
                    .hasReview(a.getHasReview())
                    .hasAdditionalAuth(a.getHasAdditionalAuth())
                    .receptionFee(a.getReceptionFee())
                    .commission(a.getCommission())
                    .realMargin(a.getRealMargin())
                    .status(a.getStatus().name())
                    .statusLabel(a.getStatus().getLabel())
                    .rejectReason(a.getRejectReason())
                    .reviewedAt(a.getReviewedAt())
                    .createdAt(a.getCreatedAt());

            // 단말기 정보
            if (a.getPhoneColor() != null) {
                var color   = a.getPhoneColor();
                var storage = color.getPhoneStorage();
                var model   = storage.getPhoneModel();
                b.modelName(model.getName())
                        .capacity(storage.getCapacity())
                        .colorName(color.getColorName())
                        .hexCode(color.getHexCode());
            }

            // 검토자
            if (a.getReviewer() != null) {
                b.reviewerName(a.getReviewer().getName());
            }

            return b.build();
        }
    }

    /** 일 마감 요약 */
    @Getter
    @Builder
    public static class DailySummary {
        private LocalDate date;
        private String    franchiseName;
        private int       totalCount;      // 총 전표 건수
        private Long      totalRealMargin; // 총 실마진
        private int       approvedCount;
        private int       pendingCount;
        private int       rejectedCount;
    }
}
