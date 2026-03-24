package com.hnt.hntapp.domain.franchise.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.franchise.dto.FranchiseRequestDto;
import com.hnt.hntapp.domain.franchise.dto.FranchiseResponseDto;
import com.hnt.hntapp.domain.franchise.service.FranchiseService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/franchises")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FranchiseController {

    private final FranchiseService franchiseService;

    /** 가맹점 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<FranchiseResponseDto>> create(
            @RequestBody FranchiseRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.success("가맹점 등록 성공", franchiseService.create(request)));
    }

    /** 가맹점 전체 목록 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FranchiseResponseDto>>> getAll() {
        return ResponseEntity.ok (
                ApiResponse.success("가맹점 목록 조회 성공", franchiseService.getAll())
        );

    }

    /** 가맹점 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FranchiseResponseDto>> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok (
                ApiResponse.success("가맹점 조회 성공", franchiseService.getById(id)));
    }

    /** 가맹점 상태 변경 */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FranchiseResponseDto>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String value) {
        return ResponseEntity.ok(
                ApiResponse.success("상태 변경 성공",
                        franchiseService.updateStatus(id, value)));
    }

    /** 가맹점 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FranchiseResponseDto>> update(
            @PathVariable UUID id,
            @RequestBody FranchiseRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.success("가맹점 수정 성공", franchiseService.update(id, request)));
    }

    /** 가맹점 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id) {
        franchiseService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("가맹점 삭제 성공", null));
    }


}

