package com.hnt.hntapp.domain.user.controller;

import com.hnt.hntapp.common.dto.ApiResponse;
import com.hnt.hntapp.domain.user.dto.FranchiseeAssignRequestDto;
import com.hnt.hntapp.domain.user.dto.UserResponseDto;
import com.hnt.hntapp.domain.user.dto.UserUpdateRequestDto;
import com.hnt.hntapp.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 전체 회원 목록 조회
    // - 관리자 전용임
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers(){
        List<UserResponseDto> users = userService.getAllusers();
        return ResponseEntity.ok(ApiResponse.success("회원 목록 조회 성공", users));
    }

    // 권한별 회원 목록 조회 하기
    // - 관리자 전용
    // ex) GET /api/users/role/FRANCHISE
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUserByRole(
            @PathVariable String role
    ) {
        List<UserResponseDto> users = userService.getUserByRole(role);
        return ResponseEntity.ok(ApiResponse.success("권한별 회원 목록 조회 성공", users));
    }

    // 가맹점주 권한 부여
    @PatchMapping("/{userId}/assign-franchisee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> assignFranchisee(
            @PathVariable UUID userId,
            @RequestBody @Valid FranchiseeAssignRequestDto request
    ) {
        return ResponseEntity.ok (
                ApiResponse.success("가맹점주 권한 부여 성공",
                        userService.assignFranchisee(userId, request)));
    }

    // 가맹점주 권한 회수
    @PatchMapping("/{userId}/revoke-franchisee")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> revokeFranchisee(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.success("가맹점주 권한 회수 성공",
                        userService.revokeFranchisee(userId)));
    }


    // 회원 관리 ( ADMIN 전용 )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(
            @PathVariable UUID id
    ) {
        UserResponseDto user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 조회 성공", user));
    }

    // 회원 정보 수정
    // - 관리자 전용
    // - null 필드는 기본 값 유지하기
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequestDto request
    ){
        UserResponseDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", updated));
    }

    //  회원 승인 (PENDING → APPROVED)
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveUser(
            @PathVariable UUID id) {
        userService.approveUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 승인 완료", null));
    }

    //  회원 반려 (PENDING → REJECTED)
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectUser(
            @PathVariable UUID id
    ) {
        userService.rejectUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 반려 완료", null));
    }

    // - 실제 삭제 대신 isActive = false 처리
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<ApiResponse<Void>> deactivateUser (
            @PathVariable UUID id
    ) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 비활성화 성공", null));
    }

    // 데이터 완전 삭제 - 신중 사용
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser (
            @PathVariable UUID id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 삭제 성공", null));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUserByStatus(
            @PathVariable String status
    ) {
        List<UserResponseDto> users = userService

                .getUsersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("상태별 회원 목록 조회 성공", users));
    }


}
