package com.hnt.hntapp.domain.user.service;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.user.dto.UserResponseDto;
import com.hnt.hntapp.domain.user.dto.UserUpdateRequestDto;
import com.hnt.hntapp.domain.user.entity.Role;
import com.hnt.hntapp.domain.user.entity.User;
import com.hnt.hntapp.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;

    // 전체 회원 목록 조회
    public List<UserResponseDto> getAllusers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    // 권한별 회원 목록 조회 하기
    public List<UserResponseDto> getUserByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role))
                .stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    // 회원 상세 조회
    public UserResponseDto getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return UserResponseDto.from(user);
    }

    // 회원 정보 수정
    // null인 필드는 기존 값 유지 ( 부분 업데이트 하기 )
    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용합니다."));

        // 소속 가맹점 조회 (변경 요청 시 )
        Franchise franchise = null;
        if (request.getFranchiseId() != null) {
            franchise = franchiseRepository.findById(request.getFranchiseId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        }

        // 새 User 엔티티 빌드 (null 이면 기존 값 유지)
        User updated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .name(request.getName()     != null ? request.getName()             : user.getName())
                .phone(request.getPhone()   != null ? request.getPhone()            : user.getPhone())
                .role(request.getRole()     != null ? Role.valueOf(request.getRole()) : user.getRole())
                .isActive(request.getIsActive() != null ? request.getIsActive()     : user.getIsActive())
                .franchise(franchise        != null ? franchise                      : user.getFranchise())
                .build();

        userRepository.save(updated);
        return UserResponseDto.from(updated);
    }

    // 회원을 비활성화 ( 실제 삭제 대신 isActive - false ) 사용
    // 데이터 보존을 위해 물리적 삭제 대신 sofe delete 사용
    @Transactional
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        User deactivated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(false) //--> 비활성화 해두기
                .franchise(user.getFranchise())
                .build();

        userRepository.save(deactivated);
    }

    // 회원 완전 삭제 ( 관리자 전용 - 신중하게 )
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteById(id);
    }

}
