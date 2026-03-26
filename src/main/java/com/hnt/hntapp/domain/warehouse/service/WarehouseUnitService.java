package com.hnt.hntapp.domain.warehouse.service;

import com.hnt.hntapp.domain.audit.entity.AuditLog;
import com.hnt.hntapp.domain.audit.entity.AuditType;
import com.hnt.hntapp.domain.audit.repository.AuditLogRepository;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseUnitService {

    private final WarehouseUnitRepository unitRepository;
    private final PhoneColorRepository    phoneColorRepository;
    private final FranchiseRepository     franchiseRepository;
    private final AuditLogRepository      auditLogRepository;

    // ──────────────────────────────────────────
    // 기기 등록 (본사/담당자)
    // ──────────────────────────────────────────

    @Transactional
    public List<WarehouseUnitDto.UnitResponse> registerUnits(
            WarehouseUnitDto.RegisterRequest req) {

        var franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "가맹점을 찾을 수 없습니다. id=" + req.franchiseId()));

        var phoneColor = phoneColorRepository.findByIdWithStorageAndModel(req.phoneColorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "컬러 정보를 찾을 수 없습니다. id=" + req.phoneColorId()));

        // 중복 일련번호 체크
        req.serialNumbers().forEach(sn -> {
            if (unitRepository.existsBySerialNumber(sn)) {
                throw new IllegalArgumentException("이미 등록된 일련번호입니다: " + sn);
            }
        });

        String carrier = resolveCarrier(req.dealer());

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

        return unitRepository.saveAll(units).stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────
    // 1차 검수 (가맹점주 실물 확인)
    // ──────────────────────────────────────────

    @Transactional
    public void passFirstAudit(String serialNumber) {
        var unit = findBySerial(serialNumber);
        unit.passFirstAudit();

        auditLogRepository.save(AuditLog.builder()
                .auditType(AuditType.FIRST)
                .franchise(unit.getFranchise())
                .serialNumber(serialNumber)
                .passed(true)
                .build());
    }

    @Transactional
    public void failFirstAudit(String serialNumber, String mismatchDetail) {
        var unit = findBySerial(serialNumber);

        auditLogRepository.save(AuditLog.builder()
                .auditType(AuditType.FIRST)
                .franchise(unit.getFranchise())
                .serialNumber(serialNumber)
                .passed(false)
                .mismatchDetail(mismatchDetail)
                .build());
    }

    // ──────────────────────────────────────────
    // 판매 처리 (전표 연결)
    // ──────────────────────────────────────────

    /** 판매 처리 — ActivationService 에서 호출 */
    @Transactional
    public void sellUnit(String serialNumber, UUID activationId) {
        var unit = findBySerial(serialNumber);
        unit.sell(activationId);
    }

    /** 판매 취소 — 전표 보류 시 호출 */
    @Transactional
    public void cancelSale(String serialNumber) {
        var unit = findBySerial(serialNumber);
        if (unit.getStatus() != UnitStatus.SOLD) {
            throw new IllegalStateException("판매 상태의 기기만 취소할 수 있습니다.");
        }
        unit.markAsLost("판매 취소");
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    public WarehouseUnitDto.UnitResponse getBySerial(String serialNumber) {
        return WarehouseUnitDto.UnitResponse.from(findBySerial(serialNumber));
    }

    public List<WarehouseUnitDto.UnitResponse> getByFranchise(UUID franchiseId) {
        return unitRepository.findByFranchiseId(franchiseId).stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());
    }

    /** 입고 기기만 조회 — 전표 작성 시 사용 */
    public List<WarehouseUnitDto.UnitResponse> getStockByFranchise(UUID franchiseId) {
        return unitRepository.findByFranchiseIdAndStatus(franchiseId, UnitStatus.STOCK).stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());
    }

    public List<WarehouseUnitDto.UnitResponse> getAllUnits() {
        return unitRepository.findAllWithDetails().stream()
                .map(WarehouseUnitDto.UnitResponse::from)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    public WarehouseUnit findBySerial(String serialNumber) {
        return unitRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "일련번호를 찾을 수 없습니다: " + serialNumber));
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
        throw new IllegalArgumentException("알 수 없는 거래처입니다: " + dealer);
    }
}
