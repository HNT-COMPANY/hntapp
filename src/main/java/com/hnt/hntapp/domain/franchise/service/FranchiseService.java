package com.hnt.hntapp.domain.franchise.service;

import com.hnt.hntapp.domain.franchise.dto.FranchiseRequestDto;
import com.hnt.hntapp.domain.franchise.dto.FranchiseResponseDto;
import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.franchise.entity.FranchiseStatus;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    /** 가맹점 등록 */
    @Transactional
    public FranchiseResponseDto create(FranchiseRequestDto request) {
        if (franchiseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 가맹점명입니다.");
        }
        Franchise franchise = Franchise.builder()
                .name(request.getName())
                .ownerName(request.getOwnerName())
                .region(request.getRegion())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .lat(request.getLat())
                .lng(request.getLng())
                .gpsRadius(request.getGpsRadius() != null ? request.getGpsRadius() : 50)
                .status(FranchiseStatus.OPERATING)
                .build();

        Franchise saved = franchiseRepository.save(franchise);
        franchiseRepository.flush();
        return FranchiseResponseDto.from(
                franchiseRepository.findById(saved.getId()).orElseThrow());
    }

    /** 가맹점 전체 목록 조회 */
    public List<FranchiseResponseDto> getAll() {
        return franchiseRepository.findAll()
                .stream()
                .map(FranchiseResponseDto::from)
                .collect(Collectors.toList());
    }

    /** 가맹점 상세 조회 */
    public FranchiseResponseDto getById(UUID id) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "가맹점을 찾을 수 없습니다."));
        return FranchiseResponseDto.from(franchise);
    }

    /** 가맹점 수정 */
    @Transactional
    public FranchiseResponseDto update(UUID id, FranchiseRequestDto request) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "가맹점을 찾을 수 없습니다."));
        franchise.update(request);
        return FranchiseResponseDto.from(franchise);
    }

    /** 가맹점 상태 변경 */
    @Transactional
    public FranchiseResponseDto updateStatus(UUID id, String status) {
        FranchiseStatus franchiseStatus = FranchiseStatus.from(status);

        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "가맹점을 찾을 수 없습니다. id=" + id));

        if (franchise.getStatus() == franchiseStatus) {
            throw new IllegalStateException(
                    "이미 해당 상태입니다: " + franchiseStatus.getLabel());
        }

        franchise.updateStatus(franchiseStatus);
        return FranchiseResponseDto.from(franchise);
    }

}
