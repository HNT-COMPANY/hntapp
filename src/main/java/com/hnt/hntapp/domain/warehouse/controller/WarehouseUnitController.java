package com.hnt.hntapp.domain.warehouse.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseDto;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseUnitDto;
import com.hnt.hntapp.domain.warehouse.service.WarehouseUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * WarehouseUnitController
 * - /api/units 엔드포인트 관리
 *
 * [변경 내역]
 * - POST /api/units: hasRole('ADMIN') → hasAnyRole('ADMIN','FRANCHISEE') 변경
 *   가맹점주도 본인 재고 직접 등록 가능
 * - System.out.println() 로그 유지
 */
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class WarehouseUnitController {

    private final WarehouseUnitService unitService;

    // ──────────────────────────────────────────
    // 기기 등록 (본사 + 가맹점주 모두 가능)
    // ──────────────────────────────────────────

    /**
     * 기기 일괄 등록
     * - ADMIN    : 어느 가맹점이든 선택 가능
     * - FRANCHISEE: 세션 franchiseId 자동 주입 (프론트에서 처리)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> register(
            @RequestBody @Valid WarehouseUnitDto.RegisterRequest request) {
        System.out.println("[WarehouseUnitController] POST /api/units - 기기 일괄 등록 요청");
        System.out.println("[WarehouseUnitController] franchiseId=" + request.franchiseId()
                + ", 등록 수량=" + request.serialNumbers().size());

        List<WarehouseUnitDto.UnitResponse> result = unitService.registerUnits(request);

        System.out.println("[WarehouseUnitController] 기기 등록 완료 - 등록된 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("기기 등록 완료", result));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<List<WarehouseDto.UnitSimpleResponse>>> getAvailableUnits(
            @RequestParam UUID colorId,
            @RequestParam UUID franchiseId
    ) {
        System.out.println("[WarehouseUnitController] GET / api/units/available" + "colorId=" + colorId + " franchiseId=" + franchiseId);
        return ResponseEntity.ok(ApiResponse.success("재고 단말 목록 조회 성공",unitService.getAvailableUnits(colorId, franchiseId)));
    }

    // ──────────────────────────────────────────
    // 1차 검수 (가맹점주)
    // ──────────────────────────────────────────

    /** 1차 검수 통과 */
    @PatchMapping("/{serialNumber}/audit/pass")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<Void>> passFirstAudit(
            @PathVariable String serialNumber,
            @RequestHeader("Authorization") String token) {
        System.out.println("[WarehouseUnitController] PATCH /api/units/" + serialNumber + "/audit/pass - 1차 검수 통과 요청");

        unitService.passFirstAudit(serialNumber);

        System.out.println("[WarehouseUnitController] 1차 검수 통과 처리 완료 - serialNumber=" + serialNumber);
        return ResponseEntity.ok(ApiResponse.success("1차 검수 완료", null));
    }

    /** 1차 검수 실패 */
    @PostMapping("/audit/fail")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<Void>> failFirstAudit(
            @RequestBody @Valid WarehouseUnitDto.FirstAuditFailRequest request) {
        System.out.println("[WarehouseUnitController] POST /api/units/audit/fail - 1차 검수 실패 요청");
        System.out.println("[WarehouseUnitController] serialNumber=" + request.serialNumber()
                + ", 불일치사유=" + request.mismatchDetail());

        unitService.failFirstAudit(request.serialNumber(), request.mismatchDetail());

        System.out.println("[WarehouseUnitController] 1차 검수 실패 기록 완료 - serialNumber=" + request.serialNumber());
        return ResponseEntity.ok(ApiResponse.success("1차 검수 불일치 기록 완료", null));
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    /** 일련번호 단건 조회 */
    @GetMapping("/{serialNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<WarehouseUnitDto.UnitResponse>> getBySerial(
            @PathVariable String serialNumber) {
        System.out.println("[WarehouseUnitController] GET /api/units/" + serialNumber + " - 단건 조회 요청");

        WarehouseUnitDto.UnitResponse result = unitService.getBySerial(serialNumber);

        System.out.println("[WarehouseUnitController] 단건 조회 완료 - serialNumber=" + serialNumber);
        return ResponseEntity.ok(ApiResponse.success("기기 조회 성공", result));
    }

    /** 가맹점 전체 기기 조회 */
    @GetMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getByFranchise(
            @PathVariable UUID franchiseId) {
        System.out.println("[WarehouseUnitController] GET /api/units/franchise/" + franchiseId + " - 가맹점 전체 기기 조회 요청");

        List<WarehouseUnitDto.UnitResponse> result = unitService.getByFranchise(franchiseId);

        System.out.println("[WarehouseUnitController] 가맹점 기기 조회 완료 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("가맹점 기기 조회 성공", result));
    }

    /** 가맹점 입고(판매 가능) 기기만 조회 — 전표 작성 시 사용 */
    @GetMapping("/franchise/{franchiseId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getStock(
            @PathVariable UUID franchiseId) {
        System.out.println("[WarehouseUnitController] GET /api/units/franchise/" + franchiseId + "/stock - 입고 기기 조회 요청");

        List<WarehouseUnitDto.UnitResponse> result = unitService.getStockByFranchise(franchiseId);

        System.out.println("[WarehouseUnitController] 입고 기기 조회 완료 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("입고 기기 조회 성공", result));
    }

    /** 전체 기기 현황 (관리자 전용) */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getAll() {
        System.out.println("[WarehouseUnitController] GET /api/units - 전체 기기 조회 요청 (관리자)");

        List<WarehouseUnitDto.UnitResponse> result = unitService.getAllUnits();

        System.out.println("[WarehouseUnitController] 전체 기기 조회 완료 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("전체 기기 조회 성공", result));
    }
}
