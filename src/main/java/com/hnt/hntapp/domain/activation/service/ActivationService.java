package com.hnt.hntapp.domain.activation.service;

import com.hnt.hntapp.domain.activation.dto.ActivationDto;
import com.hnt.hntapp.domain.activation.entity.*;
import com.hnt.hntapp.domain.activation.repository.ActivationRepository;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import com.hnt.hntapp.domain.warehouse.service.WarehouseService;
import com.hnt.hntapp.domain.warehouse.service.WarehouseUnitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivationService {

    private final ActivationRepository  activationRepository;
    private final FranchiseRepository   franchiseRepository;
    private final UserRepository        userRepository;
    private final WarehouseService      warehouseService;
    private final WarehouseUnitService  unitService;    // 신규 추가

    // ──────────────────────────────────────────
    // 전표 저장 (가맹점주)
    // ──────────────────────────────────────────

    /**
     * 전표 저장 (임시저장 or 즉시 제출)
     *
     * 변경사항:
     * - serialNumber 로 WarehouseUnit 조회 + 판매 처리
     * - 일 마감은 그냥 저장 (별도 검토 단계 없음)
     * - 예상 실마진 자동 계산
     */
    @Transactional
    public ActivationDto.Response create(ActivationDto.CreateRequest req, UUID writerId) {
        var franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "가맹점을 찾을 수 없습니다. id=" + req.franchiseId()));
        var writer = userRepository.findById(writerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "사용자를 찾을 수 없습니다. id=" + writerId));

        Carrier carrier = Carrier.fromDealer(req.dealer());

        var phoneColor = req.phoneColorId() != null
                ? warehouseService.findColor(req.phoneColorId())
                : null;

        Activation activation = Activation.builder()
                .franchise(franchise)
                .writer(writer)
                .activationDate(req.activationDate())
                .carrier(carrier)
                .dealer(req.dealer())
                .inflowPath(req.inflowPath())
                .customerName(req.customerName())
                .birthDate(req.birthDate())
                .phoneNumber(req.phoneNumber())
                .activationType(req.activationType())
                .phoneColor(phoneColor)
                .serialNumber(req.serialNumber())   // 일련번호 저장
                .usim(req.usim())
                .plan(req.plan())
                .additionalService(req.additionalService())
                .insurance(req.insurance())
                .contract(req.contract())
                .releasePrice(req.releasePrice())
                .publicSupport(req.publicSupport())
                .distSupport(req.distSupport())
                .prepayment(req.prepayment())
                .margin(req.margin())
                .marginDetail(req.marginDetail())
                .netPrice(req.netPrice())
                .sellPrice(req.sellPrice())
                .installmentMonths(req.installmentMonths())
                .deduction(req.deduction())
                .deductionDetail(req.deductionDetail())
                .hasReview(req.hasReview())
                .hasAdditionalAuth(req.hasAdditionalAuth())
                .receptionFee(req.receptionFee())
                .commission(req.commission())
                .build();

        activation.calcRealMargin();

        // 즉시 제출 시 일련번호 기기 판매 처리
        if (req.submitNow()) {
            activation.submit();
            // 일련번호가 있으면 기기 상태 STOCK → SOLD
            if (req.serialNumber() != null && !req.serialNumber().isBlank()) {
                var saved = activationRepository.save(activation);
                unitService.sellUnit(req.serialNumber(), saved.getId());
                return ActivationDto.Response.from(saved);
            }
        }

        return ActivationDto.Response.from(activationRepository.save(activation));
    }

    /** 임시저장 → 제출 */
    @Transactional
    public ActivationDto.Response submit(UUID activationId, UUID writerId) {
        Activation a = findActivation(activationId);
        if (!a.getWriter().getId().equals(writerId)) {
            throw new IllegalStateException("본인이 작성한 전표만 제출할 수 있습니다.");
        }
        if (a.getStatus() != ActivationStatus.DRAFT) {
            throw new IllegalStateException("임시저장 상태의 전표만 제출할 수 있습니다.");
        }
        a.submit();

        // 일련번호 기기 판매 처리
        if (a.getSerialNumber() != null && !a.getSerialNumber().isBlank()) {
            unitService.sellUnit(a.getSerialNumber(), activationId);
        }
        return ActivationDto.Response.from(a);
    }

    /** 보류 후 재제출 */
    @Transactional
    public ActivationDto.Response resubmit(UUID activationId, UUID writerId) {
        Activation a = findActivation(activationId);
        if (!a.getWriter().getId().equals(writerId)) {
            throw new IllegalStateException("본인이 작성한 전표만 재제출할 수 있습니다.");
        }
        a.resubmit();
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 본사 검토 (월 정산 단계에서 2차 검수)
    // ──────────────────────────────────────────

    public List<ActivationDto.Response> getPendingList() {
        return activationRepository
                .findByStatusOrderByCreatedAtDesc(ActivationStatus.SUBMITTED)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivationDto.Response approve(UUID activationId, UUID reviewerId) {
        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "사용자를 찾을 수 없습니다. id=" + reviewerId));
        a.approve(reviewer);
        return ActivationDto.Response.from(a);
    }

    @Transactional
    public ActivationDto.Response reject(UUID activationId, UUID reviewerId,
                                         ActivationDto.RejectRequest req) {
        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "사용자를 찾을 수 없습니다. id=" + reviewerId));
        a.reject(reviewer, req.rejectReason());
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    public List<ActivationDto.Response> getByFranchiseAndDate(
            UUID franchiseId, LocalDate date) {
        return activationRepository
                .findByFranchiseIdAndActivationDate(franchiseId, date)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    public List<ActivationDto.Response> getByFranchiseAndMonth(
            UUID franchiseId, int year, int month) {
        return activationRepository
                .findByFranchiseAndMonth(franchiseId, year, month)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    public ActivationDto.DailySummary getDailySummary(UUID franchiseId, LocalDate date) {
        var franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "가맹점을 찾을 수 없습니다. id=" + franchiseId));
        return ActivationDto.DailySummary.builder()
                .date(date)
                .franchiseName(franchise.getName())
                .totalCount(activationRepository
                        .countByFranchiseIdAndActivationDate(franchiseId, date))
                .totalRealMargin(activationRepository
                        .sumRealMarginByFranchiseAndDate(franchiseId, date))
                .approvedCount(activationRepository
                        .countByFranchiseIdAndActivationDateAndStatus(
                                franchiseId, date, ActivationStatus.APPROVED))
                .pendingCount(activationRepository
                        .countByFranchiseIdAndActivationDateAndStatus(
                                franchiseId, date, ActivationStatus.SUBMITTED))
                .rejectedCount(activationRepository
                        .countByFranchiseIdAndActivationDateAndStatus(
                                franchiseId, date, ActivationStatus.REJECTED))
                .build();
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    private Activation findActivation(UUID id) {
        return activationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "전표를 찾을 수 없습니다. id=" + id));
    }
}
