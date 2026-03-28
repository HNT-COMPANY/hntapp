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

/**
 * ActivationService
 * - 개통 전표 비즈니스 로직
 *
 * [변경 내역]
 * - System.out.println() 로그 전 메서드에 추가 (흐름 추적용)
 * - update(): 전표 수정 메서드 추가 (DRAFT 상태만 가능)
 * - delete(): 전표 삭제 메서드 추가 (DRAFT 상태만 가능)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivationService {

    private final ActivationRepository  activationRepository;
    private final FranchiseRepository   franchiseRepository;
    private final UserRepository        userRepository;
    private final WarehouseService      warehouseService;
    private final WarehouseUnitService  unitService;

    // ──────────────────────────────────────────
    // 전표 저장 (가맹점주)
    // ──────────────────────────────────────────

    @Transactional
    public ActivationDto.Response create(ActivationDto.CreateRequest req, UUID writerId) {
        System.out.println("[ActivationService] create() 호출 - writerId=" + writerId
                + ", franchiseId=" + req.franchiseId()
                + ", submitNow=" + req.submitNow());

        var franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> {
                    System.out.println("[ActivationService] 가맹점 없음 - id=" + req.franchiseId());
                    return new EntityNotFoundException("가맹점을 찾을 수 없습니다. id=" + req.franchiseId());
                });

        var writer = userRepository.findById(writerId)
                .orElseThrow(() -> {
                    System.out.println("[ActivationService] 사용자 없음 - id=" + writerId);
                    return new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + writerId);
                });

        Carrier carrier = Carrier.fromDealer(req.dealer());
        System.out.println("[ActivationService] 통신사 결정 - dealer=" + req.dealer() + " → carrier=" + carrier);

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
                .serialNumber(req.serialNumber())
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
        System.out.println("[ActivationService] 실마진 계산 완료 - realMargin=" + activation.getRealMargin());

        if (req.submitNow()) {
            activation.submit();
            System.out.println("[ActivationService] 즉시 제출 처리");

            if (req.serialNumber() != null && !req.serialNumber().isBlank()) {
                var saved = activationRepository.save(activation);
                unitService.sellUnit(req.serialNumber(), saved.getId());
                System.out.println("[ActivationService] 기기 판매 처리 완료 - serialNumber=" + req.serialNumber());
                return ActivationDto.Response.from(saved);
            }
        }

        Activation saved = activationRepository.save(activation);
        System.out.println("[ActivationService] 전표 저장 완료 - id=" + saved.getId()
                + ", status=" + saved.getStatus());
        return ActivationDto.Response.from(saved);
    }

    /** 임시저장 → 제출 */
    @Transactional
    public ActivationDto.Response submit(UUID activationId, UUID writerId) {
        System.out.println("[ActivationService] submit() 호출 - activationId=" + activationId);

        Activation a = findActivation(activationId);

        if (!a.getWriter().getId().equals(writerId)) {
            System.out.println("[ActivationService] 작성자 불일치 - writerId=" + writerId);
            throw new IllegalStateException("본인이 작성한 전표만 제출할 수 있습니다.");
        }
        if (a.getStatus() != ActivationStatus.DRAFT) {
            System.out.println("[ActivationService] 제출 불가 상태 - status=" + a.getStatus());
            throw new IllegalStateException("임시저장 상태의 전표만 제출할 수 있습니다.");
        }

        a.submit();

        if (a.getSerialNumber() != null && !a.getSerialNumber().isBlank()) {
            unitService.sellUnit(a.getSerialNumber(), activationId);
            System.out.println("[ActivationService] 기기 판매 처리 완료 - serialNumber=" + a.getSerialNumber());
        }

        System.out.println("[ActivationService] 전표 제출 완료 - id=" + activationId);
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 전표 수정 (DRAFT 상태만 가능)
    // ──────────────────────────────────────────

    @Transactional
    public ActivationDto.Response update(UUID activationId, ActivationDto.CreateRequest req, UUID writerId) {
        System.out.println("[ActivationService] update() 호출 - activationId=" + activationId);

        Activation a = findActivation(activationId);

        if (!a.getWriter().getId().equals(writerId)) {
            System.out.println("[ActivationService] 수정 권한 없음 - writerId=" + writerId);
            throw new IllegalStateException("본인이 작성한 전표만 수정할 수 있습니다.");
        }
        if (a.getStatus() != ActivationStatus.DRAFT) {
            System.out.println("[ActivationService] 수정 불가 상태 - status=" + a.getStatus());
            throw new IllegalStateException("임시저장 상태의 전표만 수정할 수 있습니다.");
        }

        // 기존 삭제 후 재생성 (엔티티 필드가 많아 빌더 재사용)
        activationRepository.delete(a);
        System.out.println("[ActivationService] 기존 전표 삭제 후 재생성");

        return create(req, writerId);
    }

    // ──────────────────────────────────────────
    // 전표 삭제 (DRAFT 상태만 가능)
    // ──────────────────────────────────────────

    @Transactional
    public void delete(UUID activationId, UUID writerId) {
        System.out.println("[ActivationService] delete() 호출 - activationId=" + activationId);

        Activation a = findActivation(activationId);

        if (!a.getWriter().getId().equals(writerId)) {
            System.out.println("[ActivationService] 삭제 권한 없음 - writerId=" + writerId);
            throw new IllegalStateException("본인이 작성한 전표만 삭제할 수 있습니다.");
        }
        if (a.getStatus() != ActivationStatus.DRAFT) {
            System.out.println("[ActivationService] 삭제 불가 상태 - status=" + a.getStatus());
            throw new IllegalStateException("임시저장 상태의 전표만 삭제할 수 있습니다.");
        }

        activationRepository.delete(a);
        System.out.println("[ActivationService] 전표 삭제 완료 - id=" + activationId);
    }

    /** 보류 후 재제출 */
    @Transactional
    public ActivationDto.Response resubmit(UUID activationId, UUID writerId) {
        System.out.println("[ActivationService] resubmit() 호출 - activationId=" + activationId);

        Activation a = findActivation(activationId);
        if (!a.getWriter().getId().equals(writerId)) {
            throw new IllegalStateException("본인이 작성한 전표만 재제출할 수 있습니다.");
        }
        a.resubmit();

        System.out.println("[ActivationService] 재제출 완료 - id=" + activationId);
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 본사 검토
    // ──────────────────────────────────────────

    public List<ActivationDto.Response> getPendingList() {
        System.out.println("[ActivationService] getPendingList() 호출");

        List<ActivationDto.Response> result = activationRepository
                .findByStatusOrderByCreatedAtDesc(ActivationStatus.SUBMITTED)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());

        System.out.println("[ActivationService] 검토 대기 목록 - 수량=" + result.size());
        return result;
    }

    @Transactional
    public ActivationDto.Response approve(UUID activationId, UUID reviewerId) {
        System.out.println("[ActivationService] approve() 호출 - activationId=" + activationId);

        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + reviewerId));
        a.approve(reviewer);

        System.out.println("[ActivationService] 승인 완료 - id=" + activationId);
        return ActivationDto.Response.from(a);
    }

    @Transactional
    public ActivationDto.Response reject(UUID activationId, UUID reviewerId,
                                         ActivationDto.RejectRequest req) {
        System.out.println("[ActivationService] reject() 호출 - activationId=" + activationId
                + ", reason=" + req.rejectReason());

        Activation a = findActivation(activationId);
        var reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. id=" + reviewerId));
        a.reject(reviewer, req.rejectReason());

        System.out.println("[ActivationService] 보류 처리 완료 - id=" + activationId);
        return ActivationDto.Response.from(a);
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    public List<ActivationDto.Response> getByFranchiseAndDate(UUID franchiseId, LocalDate date) {
        System.out.println("[ActivationService] getByFranchiseAndDate() - franchiseId=" + franchiseId + ", date=" + date);

        List<ActivationDto.Response> result = activationRepository
                .findByFranchiseIdAndActivationDate(franchiseId, date)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());

        System.out.println("[ActivationService] 일별 전표 조회 완료 - 수량=" + result.size());
        return result;
    }

    public List<ActivationDto.Response> getByFranchiseAndMonth(UUID franchiseId, int year, int month) {
        System.out.println("[ActivationService] getByFranchiseAndMonth() - franchiseId=" + franchiseId
                + ", year=" + year + ", month=" + month);

        List<ActivationDto.Response> result = activationRepository
                .findByFranchiseAndMonth(franchiseId, year, month)
                .stream()
                .map(ActivationDto.Response::from)
                .collect(Collectors.toList());

        System.out.println("[ActivationService] 월별 전표 조회 완료 - 수량=" + result.size());
        return result;
    }

    public ActivationDto.DailySummary getDailySummary(UUID franchiseId, LocalDate date) {
        System.out.println("[ActivationService] getDailySummary() - franchiseId=" + franchiseId + ", date=" + date);

        var franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new EntityNotFoundException("가맹점을 찾을 수 없습니다. id=" + franchiseId));

        ActivationDto.DailySummary summary = ActivationDto.DailySummary.builder()
                .date(date)
                .franchiseName(franchise.getName())
                .totalCount(activationRepository.countByFranchiseIdAndActivationDate(franchiseId, date))
                .totalRealMargin(activationRepository.sumRealMarginByFranchiseAndDate(franchiseId, date))
                .approvedCount(activationRepository.countByFranchiseIdAndActivationDateAndStatus(
                        franchiseId, date, ActivationStatus.APPROVED))
                .pendingCount(activationRepository.countByFranchiseIdAndActivationDateAndStatus(
                        franchiseId, date, ActivationStatus.SUBMITTED))
                .rejectedCount(activationRepository.countByFranchiseIdAndActivationDateAndStatus(
                        franchiseId, date, ActivationStatus.REJECTED))
                .build();

        System.out.println("[ActivationService] 일마감 요약 완료 - totalCount=" + summary.getTotalCount()
                + ", totalRealMargin=" + summary.getTotalRealMargin());
        return summary;
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    private Activation findActivation(UUID id) {
        System.out.println("[ActivationService] findActivation() - id=" + id);
        return activationRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("[ActivationService] 전표 없음 - id=" + id);
                    return new EntityNotFoundException("전표를 찾을 수 없습니다. id=" + id);
                });
    }
}
