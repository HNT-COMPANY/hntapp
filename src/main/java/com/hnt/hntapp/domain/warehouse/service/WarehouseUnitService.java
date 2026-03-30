package com.hnt.hntapp.domain.warehouse.service;

import com.hnt.hntapp.domain.audit.entity.AuditLog;
import com.hnt.hntapp.domain.audit.entity.AuditType;
import com.hnt.hntapp.domain.audit.repository.AuditLogRepository;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseDto;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseUnitDto;
import com.hnt.hntapp.domain.warehouse.entity.*;
import com.hnt.hntapp.domain.warehouse.repository.PhoneColorRepository;
import com.hnt.hntapp.domain.warehouse.repository.WarehouseUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * WarehouseUnitService
 * - WarehouseUnit 비즈니스 로직
 *
 * [변경 내역]
 * - System.out.println() 로그 전 메서드에 추가 (흐름 추적용)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseUnitService {

    private final WarehouseUnitRepository unitRepository;
    private final PhoneColorRepository    phoneColorRepository;
    private final FranchiseRepository     franchiseRepository;
    private final AuditLogRepository      auditLogRepository;

    public List<WarehouseDto.UnitSimpleResponse> getAvailableUnits(
            UUID colorId, UUID franchiseId
    ) {
        System.out.println("[WarehouseUnitSerice] 재고 조회 요청 - colorId=" + colorId + ", franchiseId="+ franchiseId);

        List<WarehouseDto.UnitSimpleResponse> result =
                    unitRepository
                        .findAvailableByColorAndFranchise(colorId, franchiseId)
                        .stream()
                        .map(WarehouseDto.UnitSimpleResponse::from)
                        .collect(Collectors.toList());

        System.out.println("[WarehouseUnitService] 조회된 재고 수 =" + result.size());
        return result;
    }

    // ──────────────────────────────────────────
    // 기기 등록 (본사/담당자)
    // ──────────────────────────────────────────

    @Transactional
    public List<WarehouseUnitDto.UnitResponse> registerUnits(
            WarehouseUnitDto.RegisterRequest req) {
        System.out.println("[WarehouseUnitService] registerUnits() 호출");
        System.out.println("[WarehouseUnitService] franchiseId=" + req.franchiseId()
                + ", phoneColorId=" + req.phoneColorId()
                + ", dealer=" + req.dealer()
                + ", 등록 수량=" + req.serialNumbers().size());

        var franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> {
                    System.out.println("[WarehouseUnitService] 가맹점 없음 - id=" + req.franchiseId());
                    return new EntityNotFoundException("가맹점을 찾을 수 없습니다. id=" + req.franchiseId());
                });

        var phoneColor = phoneColorRepository.findByIdWithStorageAndModel(req.phoneColorId())
                .orElseThrow(() -> {
                    System.out.println("[WarehouseUnitService] 컬러 정보 없음 - id=" + req.phoneColorId());
                    return new EntityNotFoundException("컬러 정보를 찾을 수 없습니다. id=" + req.phoneColorId());
                });

        // 중복 일련번호 체크
        req.serialNumbers().forEach(sn -> {
            if (unitRepository.existsBySerialNumber(sn)) {
                System.out.println("[WarehouseUnitService] 중복 일련번호 감지: " + sn);
                throw new IllegalArgumentException("이미 등록된 일련번호입니다: " + sn);
            }
        });

        String carrier = resolveCarrier(req.dealer());
        System.out.println("[WarehouseUnitService] 통신사 결정: dealer=" + req.dealer() + " → carrier=" + carrier);

        var units = req.serialNumbers().stream()
                .map(sn -> WarehouseUnit.builder()
                        .serialNumber(sn)
                        .phoneColor(phoneColor)
                        .franchise(franchise)
                        .dealer(req.dealer())
                        .carrier(carrier)
                        .stockedAt(req.stockedAt())
                        .build())
                .collect(Collectors.toList());

        List<WarehouseUnitDto.UnitResponse> result = unitRepository.saveAll(units).stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());

        System.out.println("[WarehouseUnitService] 기기 등록 완료 - 저장된 수량=" + result.size());
        return result;
    }

    // ──────────────────────────────────────────
    // 1차 검수 (가맹점주 실물 확인)
    // ──────────────────────────────────────────

    @Transactional
    public void passFirstAudit(String serialNumber) {
        System.out.println("[WarehouseUnitService] passFirstAudit() 호출 - serialNumber=" + serialNumber);

        var unit = findBySerial(serialNumber);
        unit.passFirstAudit();
        System.out.println("[WarehouseUnitService] 기기 상태 업데이트 완료 - franchiseId=" + unit.getFranchise().getId());

        auditLogRepository.save(AuditLog.builder()
                .auditType(AuditType.FIRST)
                .franchise(unit.getFranchise())
                .serialNumber(serialNumber)
                .passed(true)
                .build());

        System.out.println("[WarehouseUnitService] AuditLog 저장 완료 - type=FIRST, passed=true");
    }

    @Transactional
    public void failFirstAudit(String serialNumber, String mismatchDetail) {
        System.out.println("[WarehouseUnitService] failFirstAudit() 호출 - serialNumber=" + serialNumber);
        System.out.println("[WarehouseUnitService] 불일치 사유=" + mismatchDetail);

        var unit = findBySerial(serialNumber);

        auditLogRepository.save(AuditLog.builder()
                .auditType(AuditType.FIRST)
                .franchise(unit.getFranchise())
                .serialNumber(serialNumber)
                .passed(false)
                .mismatchDetail(mismatchDetail)
                .build());

        System.out.println("[WarehouseUnitService] AuditLog 저장 완료 - type=FIRST, passed=false");
    }

    // ──────────────────────────────────────────
    // 판매 처리 (전표 연결)
    // ──────────────────────────────────────────

    /** 판매 처리 — ActivationService 에서 호출 */
    @Transactional
    public void sellUnit(String serialNumber, UUID activationId) {
        System.out.println("[WarehouseUnitService] sellUnit() 호출 - serialNumber=" + serialNumber
                + ", activationId=" + activationId);

        var unit = findBySerial(serialNumber);
        unit.sell(activationId);

        System.out.println("[WarehouseUnitService] 판매 처리 완료 - serialNumber=" + serialNumber);
    }

    /** 판매 취소 — 전표 보류 시 호출 */
    @Transactional
    public void cancelSale(String serialNumber) {
        System.out.println("[WarehouseUnitService] cancelSale() 호출 - serialNumber=" + serialNumber);

        var unit = findBySerial(serialNumber);
        if (unit.getStatus() != UnitStatus.SOLD) {
            System.out.println("[WarehouseUnitService] 판매 취소 불가 - 현재 상태=" + unit.getStatus());
            throw new IllegalStateException("판매 상태의 기기만 취소할 수 있습니다.");
        }
        unit.markAsLost("판매 취소");

        System.out.println("[WarehouseUnitService] 판매 취소 완료 - serialNumber=" + serialNumber);
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    public WarehouseUnitDto.UnitResponse getBySerial(String serialNumber) {
        System.out.println("[WarehouseUnitService] getBySerial() 호출 - serialNumber=" + serialNumber);
        WarehouseUnitDto.UnitResponse result = WarehouseUnitDto.UnitResponse.from(findBySerial(serialNumber));
        System.out.println("[WarehouseUnitService] 단건 조회 완료 - serialNumber=" + serialNumber);
        return result;
    }

    public List<WarehouseUnitDto.UnitResponse> getByFranchise(UUID franchiseId) {
        System.out.println("[WarehouseUnitService] getByFranchise() 호출 - franchiseId=" + franchiseId);

        List<WarehouseUnitDto.UnitResponse> result = unitRepository.findByFranchiseId(franchiseId).stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());

        System.out.println("[WarehouseUnitService] 가맹점 기기 조회 완료 - 수량=" + result.size());
        return result;
    }

    /** 입고 기기만 조회 — 전표 작성 시 사용 */
    public List<WarehouseUnitDto.UnitResponse> getStockByFranchise(UUID franchiseId) {
        System.out.println("[WarehouseUnitService] getStockByFranchise() 호출 - franchiseId=" + franchiseId);

        List<WarehouseUnitDto.UnitResponse> result =
                unitRepository.findByFranchiseIdAndStatus(franchiseId, UnitStatus.STOCK).stream()
                        .map(WarehouseUnitDto.UnitResponse::from)
                        .collect(Collectors.toList());

        System.out.println("[WarehouseUnitService] 입고 기기 조회 완료 - 수량=" + result.size());
        return result;
    }

    public List<WarehouseUnitDto.UnitResponse> getAllUnits() {
        System.out.println("[WarehouseUnitService] getAllUnits() 호출 (관리자 전체 조회)");

        List<WarehouseUnitDto.UnitResponse> result = unitRepository.findAllWithDetails().stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());

        System.out.println("[WarehouseUnitService] 전체 기기 조회 완료 - 수량=" + result.size());
        return result;
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    public WarehouseUnit findBySerial(String serialNumber) {
        System.out.println("[WarehouseUnitService] findBySerial() - serialNumber=" + serialNumber);
        return unitRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> {
                    System.out.println("[WarehouseUnitService] 일련번호 없음: " + serialNumber);
                    return new EntityNotFoundException("일련번호를 찾을 수 없습니다: " + serialNumber);
                });
    }

    private String resolveCarrier(String dealer) {
        if (dealer == null) throw new IllegalArgumentException("거래처 정보가 없습니다.");
        String d = dealer.trim();
        if (d.equals("라온") || d.equals("한빛") || d.equals("예스") || d.equals("PS"))
            return "SK";
        if (d.equals("에이딘") || d.equals("M&S") || d.equals("오텔"))
            return "KT";
        if (d.equals("메타") || d.equals("수석") || d.equals("빅뱅") ||
                d.equals("골드스타") || d.equals("모스트") || d.equals("수인") || d.equals("가본"))
            return "LG";
        System.out.println("[WarehouseUnitService] 알 수 없는 거래처: " + dealer);
        throw new IllegalArgumentException("알 수 없는 거래처입니다: " + dealer);
    }
}
