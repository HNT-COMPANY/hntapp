package com.hnt.hntapp.domain.warehouse.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseUnitDto;
import com.hnt.hntapp.domain.warehouse.service.WarehouseUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class WarehouseUnitController {

    private final WarehouseUnitService unitService;

    // ──────────────────────────────────────────
    // 기기 등록 (본사/담당자)
    // ──────────────────────────────────────────

    /** 기기 일괄 등록 */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> register(
            @RequestBody @Valid WarehouseUnitDto.RegisterRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("기기 등록 완료",
                        unitService.registerUnits(request)));
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
        unitService.passFirstAudit(serialNumber);
        return ResponseEntity.ok(ApiResponse.success("1차 검수 완료", null));
    }

    /** 1차 검수 실패 */
    @PostMapping("/audit/fail")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<Void>> failFirstAudit(
            @RequestBody @Valid WarehouseUnitDto.FirstAuditFailRequest request) {
        unitService.failFirstAudit(request.serialNumber(), request.mismatchDetail());
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
        return ResponseEntity.ok(
                ApiResponse.success("기기 조회 성공",
                        unitService.getBySerial(serialNumber)));
    }

    /** 가맹점 전체 기기 조회 */
    @GetMapping("/franchise/{franchiseId}")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getByFranchise(
            @PathVariable UUID franchiseId) {
        return ResponseEntity.ok(
                ApiResponse.success("가맹점 기기 조회 성공",
                        unitService.getByFranchise(franchiseId)));
    }

    /** 가맹점 입고(판매 가능) 기기만 조회 — 전표 작성 시 사용 */
    @GetMapping("/franchise/{franchiseId}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getStock(
            @PathVariable UUID franchiseId) {
        return ResponseEntity.ok(
                ApiResponse.success("입고 기기 조회 성공",
                        unitService.getStockByFranchise(franchiseId)));
    }

    /** 전체 기기 현황 (관리자) */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseUnitDto.UnitResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("전체 기기 조회 성공",
                        unitService.getAllUnits()));
    }
}
