package com.hnt.hntapp.domain.activation.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.config.JwtUtil;
import com.hnt.hntapp.domain.activation.dto.ActivationDto;
import com.hnt.hntapp.domain.activation.service.ActivationService;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activations")
@RequiredArgsConstructor
public class ActivationController {

    private final ActivationService activationService;
    private final JwtUtil           jwtUtil;
    private final UserRepository    userRepository;

    // ──────────────────────────────────────────
    // 가맹점주 — 전표 작성
    // ──────────────────────────────────────────

    /** 전표 저장 (submitNow=false: 임시저장 / true: 즉시 제출 + 재고 차감) */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> create(
            @RequestBody @Valid ActivationDto.CreateRequest request,
            @RequestHeader("Authorization") String token) {
        UUID writerId = extractUserId(token);
        return ResponseEntity.ok(
                ApiResponse.success("전표 저장 성공",
                        activationService.create(request, writerId)));
    }

    /** 임시저장 → 제출 */
    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> submit(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        UUID writerId = extractUserId(token);
        return ResponseEntity.ok(
                ApiResponse.success("전표 제출 성공",
                        activationService.submit(id, writerId)));
    }

    /** 보류 후 재제출 */
    @PatchMapping("/{id}/resubmit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> resubmit(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        UUID writerId = extractUserId(token);
        return ResponseEntity.ok(
                ApiResponse.success("재제출 성공",
                        activationService.resubmit(id, writerId)));
    }

    // ──────────────────────────────────────────
    // 본사 — 검토
    // ──────────────────────────────────────────

    /** 검토 대기 목록 */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivationDto.Response>>> getPending() {
        return ResponseEntity.ok(
                ApiResponse.success("검토 대기 목록 조회 성공",
                        activationService.getPendingList()));
    }

    /** 승인 */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> approve(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        UUID reviewerId = extractUserId(token);
        return ResponseEntity.ok(
                ApiResponse.success("승인 완료",
                        activationService.approve(id, reviewerId)));
    }

    /** 보류 */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> reject(
            @PathVariable UUID id,
            @RequestBody @Valid ActivationDto.RejectRequest request,
            @RequestHeader("Authorization") String token) {
        UUID reviewerId = extractUserId(token);
        return ResponseEntity.ok(
                ApiResponse.success("보류 처리 완료",
                        activationService.reject(id, reviewerId, request)));
    }

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    /** 가맹점 일별 전표 목록 */
    @GetMapping("/franchise/{franchiseId}/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<ActivationDto.Response>>> getDaily(
            @PathVariable UUID franchiseId,
            @RequestParam(defaultValue = "") String date) {
        LocalDate d = date.isEmpty() ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(
                ApiResponse.success("일별 전표 조회 성공",
                        activationService.getByFranchiseAndDate(franchiseId, d)));
    }

    /** 가맹점 월별 전표 목록 */
    @GetMapping("/franchise/{franchiseId}/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<ActivationDto.Response>>> getMonthly(
            @PathVariable UUID franchiseId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(
                ApiResponse.success("월별 전표 조회 성공",
                        activationService.getByFranchiseAndMonth(franchiseId, year, month)));
    }

    /** 일 마감 요약 */
    @GetMapping("/franchise/{franchiseId}/daily-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<ActivationDto.DailySummary>> getDailySummary(
            @PathVariable UUID franchiseId,
            @RequestParam(defaultValue = "") String date) {
        LocalDate d = date.isEmpty() ? LocalDate.now() : LocalDate.parse(date);
        return ResponseEntity.ok(
                ApiResponse.success("일 마감 요약 조회 성공",
                        activationService.getDailySummary(franchiseId, d)));
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    private UUID extractUserId(String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        String email = jwtUtil.getEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."))
                .getId();
    }
}
