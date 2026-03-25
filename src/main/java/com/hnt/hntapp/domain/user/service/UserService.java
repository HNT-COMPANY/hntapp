package com.hnt.hntapp.domain.user.service;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.user.dto.FranchiseeAssignRequestDto;
import com.hnt.hntapp.domain.user.dto.UserResponseDto;
import com.hnt.hntapp.domain.user.dto.UserUpdateRequestDto;
import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.entity.UserStatus;
import com.hnt.hntapp.domain.user.repository.UserRepository;
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
public class UserService {

    private final UserRepository      userRepository;
    private final FranchiseRepository franchiseRepository;

    // ──────────────────────────────────────────
    // 조회
    // ──────────────────────────────────────────

    public List<UserResponseDto> getAllusers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> getUserByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role)).stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUser(UUID id) {
        return UserResponseDto.from(findUserById(id));
    }

    public List<UserResponseDto> getUsersByStatus(String status) {
        return userRepository.findByStatus(UserStatus.valueOf(status)).stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────
    // 가맹점주 권한 부여 / 회수
    // ──────────────────────────────────────────

    /**
     * 가맹점주 권한 부여
     * - PATCH /api/admin/users/{userId}/assign-franchisee
     * - APPROVED 상태 회원만 지정 가능
     * - 가맹점당 가맹점주 1명 제한
     */
    @Transactional
    public UserResponseDto assignFranchisee(UUID userId, FranchiseeAssignRequestDto request) {
        User user = findUserById(userId);

        // 승인된 회원만 가맹점주로 지정 가능
        if (user.getStatus() != UserStatus.APPROVED) {
            throw new IllegalStateException("승인된 회원만 가맹점주로 지정할 수 있습니다.");
        }

        Franchise franchise = franchiseRepository.findById(request.franchiseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "가맹점을 찾을 수 없습니다. id=" + request.franchiseId()));

        // 해당 가맹점에 이미 가맹점주가 있는지 확인
        userRepository.findByFranchiseIdAndRole(franchise.getId(), Role.FRANCHISEE)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(userId)) {
                        throw new IllegalStateException(
                                "[" + franchise.getName() + "]에 이미 가맹점주가 지정되어 있습니다. " +
                                        "(" + existing.getName() + ")");
                    }
                });

        user.assignFranchisee(franchise);
        franchise.updateOwnerName((user.getName()));
        return UserResponseDto.from(user);
    }

    /**
     * 가맹점주 권한 회수
     * - PATCH /api/admin/users/{userId}/revoke-franchisee
     * - Role이 FRANCHISEE인 경우에만 회수 가능
     * - 회수 후 STAFF로 변경
     */
    @Transactional
    public UserResponseDto revokeFranchisee(UUID userId) {
        User user = findUserById(userId);

        if (user.getRole() != Role.FRANCHISEE) {
            throw new IllegalStateException("가맹점주 권한이 없는 회원입니다.");
        }

        if (user.getFranchise() != null) {
            user.getFranchise().updateOwnerName("-");
        }

        user.revokeFranchisee();            // dirty checking → 자동 UPDATE
        return UserResponseDto.from(user);
    }

    // ──────────────────────────────────────────
    // 회원 관리
    // ──────────────────────────────────────────

    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateRequestDto request) {
        User user = findUserById(id);

        // 소속 가맹점 변경 요청 시
        if (request.getFranchiseId() != null) {
            Franchise franchise = franchiseRepository.findById(request.getFranchiseId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 가맹점입니다."));
            user.changeFranchise(franchise);
        }

        // 이름/전화번호 수정
        user.updateInfo(request.getName(), request.getPhone());

        return UserResponseDto.from(user);
    }

    @Transactional
    public void approveUser(UUID id) {
        User user = findUserById(id);
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("대기 중인 회원만 승인할 수 있습니다.");
        }
        user.approve();
    }

    @Transactional
    public void rejectUser(UUID id) {
        User user = findUserById(id);
        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("대기 중인 회원만 반려할 수 있습니다.");
        }
        user.reject();
    }

    @Transactional
    public void deactivateUser(UUID id) {
        User user = findUserById(id);
        user.deactivate();
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(id);
    }

    // ──────────────────────────────────────────
    // 내부 헬퍼
    // ──────────────────────────────────────────

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "존재하지 않는 사용자입니다. id=" + id));
    }
}
