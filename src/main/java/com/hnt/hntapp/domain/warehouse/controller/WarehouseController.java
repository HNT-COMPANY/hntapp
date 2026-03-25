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

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // 모델 등록 ( 용량 / 컬러 동시 )
    @PostMapping("/models")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDto.ModelResponse>> createModel (
            @RequestBody @Valid WarehouseDto.CreateModelRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("모델 등록 성공", warehouseService.createModel(request)));
    }

    // 전체 모델 목록 조회 매서드
    @GetMapping("/models")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<List<WarehouseDto.ModelResponse>>> getModels() {
        return ResponseEntity.ok(
                ApiResponse.success("모델 목록 조회 성공", warehouseService.getAllModels()));

    }

    // 창고 재고 ADMIN 전용
    // 전체 가맹점 재고 현황
    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseDto.StockResponse>>> getAllStock(){
        return ResponseEntity.ok(
                ApiResponse.success("전체 재고 조회 성공", warehouseService.getAllStocks()));
    }

    // 가맹점별 재고 조회
    @GetMapping("/stock/franchise/{franchiseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<WarehouseDto.StockResponse>>> getStocksByFranchise(
            @PathVariable UUID franchiseId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("가맹점 재고 조회 성공", warehouseService.getStocksByFranchise(franchiseId)));
    }

    // 가맹점 배분 확정
    @PostMapping("/stock/distribute")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseDto.StockResponse>> distribute(
            @RequestBody @Valid WarehouseDto.DistributeRequest request
    ) {
        return ResponseEntity.ok (
                ApiResponse.success("배분 확정 성공", warehouseService.distribute(request)));
    }

}
