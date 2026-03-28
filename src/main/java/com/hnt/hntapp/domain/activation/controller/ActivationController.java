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

/**
 * ActivationController
 * - /api/activations 엔드포인트 관리
 *
 * [변경 내역]
 * - System.out.println() 로그 전 메서드에 추가 (흐름 추적용)
 */
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
        System.out.println("[ActivationController] POST /api/activations - 전표 저장 요청");
        System.out.println("[ActivationController] franchiseId=" + request.franchiseId()
                + ", dealer=" + request.dealer()
                + ", customerName=" + request.customerName()
                + ", submitNow=" + request.submitNow());

        UUID writerId = extractUserId(token);
        System.out.println("[ActivationController] writerId=" + writerId);

        ActivationDto.Response response = activationService.create(request, writerId);
        System.out.println("[ActivationController] 전표 저장 완료 - id=" + response.getId()
                + ", status=" + response.getStatus());

        return ResponseEntity.ok(ApiResponse.success("전표 저장 성공", response));
    }

    /** 임시저장 → 제출 */
    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> submit(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] PATCH /api/activations/" + id + "/submit - 전표 제출 요청");

        UUID writerId = extractUserId(token);
        ActivationDto.Response response = activationService.submit(id, writerId);

        System.out.println("[ActivationController] 전표 제출 완료 - id=" + id + ", status=" + response.getStatus());
        return ResponseEntity.ok(ApiResponse.success("전표 제출 성공", response));
    }

    /** 전표 수정 */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> update(
            @PathVariable UUID id,
            @RequestBody @Valid ActivationDto.CreateRequest request,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] PUT /api/activations/" + id + " - 전표 수정 요청");

        UUID writerId = extractUserId(token);
        ActivationDto.Response response = activationService.update(id, request, writerId);

        System.out.println("[ActivationController] 전표 수정 완료 - id=" + id);
        return ResponseEntity.ok(ApiResponse.success("전표 수정 성공", response));
    }

    /** 전표 삭제 (DRAFT 상태만 가능) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] DELETE /api/activations/" + id + " - 전표 삭제 요청");

        UUID writerId = extractUserId(token);
        activationService.delete(id, writerId);

        System.out.println("[ActivationController] 전표 삭제 완료 - id=" + id);
        return ResponseEntity.ok(ApiResponse.success("전표 삭제 완료", null));
    }

    /** 보류 후 재제출 */
    @PatchMapping("/{id}/resubmit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE', 'STAFF')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> resubmit(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] PATCH /api/activations/" + id + "/resubmit - 재제출 요청");

        UUID writerId = extractUserId(token);
        ActivationDto.Response response = activationService.resubmit(id, writerId);

        System.out.println("[ActivationController] 재제출 완료 - id=" + id);
        return ResponseEntity.ok(ApiResponse.success("재제출 성공", response));
    }

    // ──────────────────────────────────────────
    // 본사 — 검토
    // ──────────────────────────────────────────

    /** 검토 대기 목록 */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivationDto.Response>>> getPending() {
        System.out.println("[ActivationController] GET /api/activations/pending - 검토 대기 목록 조회");

        List<ActivationDto.Response> result = activationService.getPendingList();

        System.out.println("[ActivationController] 검토 대기 목록 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("검토 대기 목록 조회 성공", result));
    }

    /** 승인 */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> approve(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] PATCH /api/activations/" + id + "/approve - 승인 요청");

        UUID reviewerId = extractUserId(token);
        ActivationDto.Response response = activationService.approve(id, reviewerId);

        System.out.println("[ActivationController] 승인 완료 - id=" + id);
        return ResponseEntity.ok(ApiResponse.success("승인 완료", response));
    }

    /** 보류 */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ActivationDto.Response>> reject(
            @PathVariable UUID id,
            @RequestBody @Valid ActivationDto.RejectRequest request,
            @RequestHeader("Authorization") String token) {
        System.out.println("[ActivationController] PATCH /api/activations/" + id + "/reject - 보류 요청");
        System.out.println("[ActivationController] rejectReason=" + request.rejectReason());

        UUID reviewerId = extractUserId(token);
        ActivationDto.Response response = activationService.reject(id, reviewerId, request);

        System.out.println("[ActivationController] 보류 처리 완료 - id=" + id);
        return ResponseEntity.ok(ApiResponse.success("보류 처리 완료", response));
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
        System.out.println("[ActivationController] GET 일별 전표 조회 - franchiseId=" + franchiseId + ", date=" + d);

        List<ActivationDto.Response> result = activationService.getByFranchiseAndDate(franchiseId, d);

        System.out.println("[ActivationController] 일별 전표 조회 완료 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("일별 전표 조회 성공", result));
    }

    /** 가맹점 월별 전표 목록 */
    @GetMapping("/franchise/{franchiseId}/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<List<ActivationDto.Response>>> getMonthly(
            @PathVariable UUID franchiseId,
            @RequestParam int year,
            @RequestParam int month) {
        System.out.println("[ActivationController] GET 월별 전표 조회 - franchiseId=" + franchiseId
                + ", year=" + year + ", month=" + month);

        List<ActivationDto.Response> result = activationService.getByFranchiseAndMonth(franchiseId, year, month);

        System.out.println("[ActivationController] 월별 전표 조회 완료 - 수량=" + result.size());
        return ResponseEntity.ok(ApiResponse.success("월별 전표 조회 성공", result));
    }

    /** 일 마감 요약 */
    @GetMapping("/franchise/{franchiseId}/daily-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<ApiResponse<ActivationDto.DailySummary>> getDailySummary(
            @PathVariable UUID franchiseId,
            @RequestParam(defaultValue = "") String date) {
        LocalDate d = date.isEmpty() ? LocalDate.now() : LocalDate.parse(date);
        System.out.println("[ActivationController] GET 일마감 요약 - franchiseId=" + franchiseId + ", date=" + d);

        ActivationDto.DailySummary summary = activationService.getDailySummary(franchiseId, d);

        System.out.println("[ActivationController] 일마감 요약 완료 - totalCount=" + summary.getTotalCount()
                + ", totalRealMargin=" + summary.getTotalRealMargin());
        return ResponseEntity.ok(ApiResponse.success("일 마감 요약 조회 성공", summary));
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    private UUID extractUserId(String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        String email = jwtUtil.getEmail(token);
        System.out.println("[ActivationController] extractUserId - email=" + email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."))
                .getId();
    }
}
