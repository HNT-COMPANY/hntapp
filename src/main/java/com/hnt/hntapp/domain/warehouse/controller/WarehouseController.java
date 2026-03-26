package com.hnt.hntapp.domain.warehouse.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseDto;
import com.hnt.hntapp.domain.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 모델 마스터 API
 * - Stock 관련 엔드포인트 전부 제거
 *   (distribute, getAllStock, getStocksByFranchise)
 * - 재고 조회는 /api/units 로 이전됨
 */
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    /** 모델 등록 (용량·컬러 동시) */
    @PostMapping("/models")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDto.ModelResponse>> createModel(
            @RequestBody @Valid WarehouseDto.CreateModelRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("모델 등록 성공", warehouseService.createModel(request)));
    }

    /** 전체 모델 목록 조회 */
    @GetMapping("/models")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<List<WarehouseDto.ModelResponse>>> getModels() {
        return ResponseEntity.ok(
                ApiResponse.success("모델 목록 조회 성공", warehouseService.getAllModels()));
    }

    /** 모델 비활성화 (단종) */
    @PatchMapping("/models/{modelId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateModel(
            @PathVariable UUID modelId) {
        warehouseService.deactivateModel(modelId);
        return ResponseEntity.ok(ApiResponse.success("모델 비활성화 완료", null));
    }
}
