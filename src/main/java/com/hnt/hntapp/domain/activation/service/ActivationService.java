package com.hnt.hntapp.domain.activation.service;

import com.hnt.hntapp.domain.activation.dto.ActivationDto;
import com.hnt.hntapp.domain.activation.entity.*;
import com.hnt.hntapp.domain.activation.repository.ActivationRepository;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import com.hnt.hntapp.domain.warehouse.repository.WarehouseStockRepository;
import com.hnt.hntapp.domain.warehouse.service.WarehouseService;
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

    private final ActivationRepository    activationRepository;
    private final FranchiseRepository     franchiseRepository;
    private final UserRepository          userRepository;
    private final WarehouseService        warehouseService;

    // ──────────────────────────────────────────
    // 전표 작성 (가맹점주)
    // ──────────────────────────────────────────

    /**
     * 전표 저장 (임시저장 or 즉시 제출)
     * - 제출 시 재고 자동 차감
     */
    @Transactional
    public ActivationDto.Response create(ActivationDto.CreateRequest req, UUID writerId) {
        var franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> new EntityNotFoundException("가맹점 없음"));
        var writer = userRepository.findById(writerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        // 통신사 자동 구분
        Carrier carrier = Carrier.fromDealer(req.dealer());

        // 단말기 컬러 조회 (선택한 경우)
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

        // 실마진 자동 계산
        activation.calcRealMargin();

        // 즉시 제출 요청 시
        if (req.submitNow()) {
            activation.submit();
            // 재고 자동 차감
            if (phoneColor != null) {
                warehouseService.deductStock(
                        req.franchiseId(), phoneColor.getId(), 1);
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

        // 재고 차감
        if (a.getPhoneColor() != null) {
            warehouseService.deductStock(
                    a.getFranchise().getId(), a.getPhoneColor().getId(), 1);
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
    // 본사 검토
    // ──────────────────────────────────────────

    /** 제출된 전표 목록 조회 (본사) */
    public List<ActivationDto.Response> getPendingList() {
        return activationRepository
                .findByStatusOrderByCreatedAtDesc(ActivationStatus.SUBMITTED)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    /** 승인 */
    @Transactional
    public ActivationDto.Response approve(UUID activationId, UUID reviewerId) {
        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        a.approve(reviewer);
        return ActivationDto.Response.from(a);
    }

    /** 보류 */
    @Transactional
    public ActivationDto.Response reject(UUID activationId, UUID reviewerId,
                                         ActivationDto.RejectRequest req) {
        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        // 보류 시 재고 복구
        if (a.getPhoneColor() != null) {
            warehouseService.addStock(
                    a.getFranchise().getId(), a.getPhoneColor().getId(), 1);
        }
        a.reject(reviewer, req.rejectReason());
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    /** 가맹점 일별 전표 목록 */
    public List<ActivationDto.Response> getByFranchiseAndDate(
            UUID franchiseId, LocalDate date) {
        return activationRepository
                .findByFranchiseIdAndActivationDate(franchiseId, date)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    /** 가맹점 월별 전표 목록 */
    public List<ActivationDto.Response> getByFranchiseAndMonth(
            UUID franchiseId, int year, int month) {
        return activationRepository
                .findByFranchiseAndMonth(franchiseId, year, month)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());
    }

    /** 일 마감 요약 */
    public ActivationDto.DailySummary getDailySummary(UUID franchiseId, LocalDate date) {
        var list = activationRepository
                .findByFranchiseIdAndActivationDate(franchiseId, date);
        var franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("가맹점 없음"));
        long totalMargin = activationRepository
                .sumRealMarginByFranchiseAndDate(franchiseId, date);

        return ActivationDto.DailySummary.builder()
                .date(date)
                .franchiseName(franchise.getName())
                .totalCount(list.size())
                .totalRealMargin(totalMargin)
                .approvedCount((int) list.stream()
                        .filter(a -> a.getStatus() == ActivationStatus.APPROVED).count())
                .pendingCount((int) list.stream()
                        .filter(a -> a.getStatus() == ActivationStatus.SUBMITTED).count())
                .rejectedCount((int) list.stream()
                        .filter(a -> a.getStatus() == ActivationStatus.REJECTED).count())
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
