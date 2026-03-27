package com.hnt.hntapp.domain.warehouse.service;

import com.hnt.hntapp.domain.audit.repository.AuditLogRepository;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseDto;
import com.hnt.hntapp.domain.warehouse.entity.*;
import com.hnt.hntapp.domain.warehouse.repository.PhoneColorRepository;
import com.hnt.hntapp.domain.warehouse.repository.PhoneModelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 모델 마스터 관리 전담 서비스
 * - PhoneModel / PhoneStorage / PhoneColor 등록/조회
 *
 * 제거된 것:
 * - WarehouseStockRepository 의존성 제거
 * - distribute(), getAllStocks(), getStocksByFranchise() 제거
 * - deductStock(), addStock() 제거
 * → 전부 WarehouseUnitService 로 이전됨
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final PhoneModelRepository phoneModelRepository;
    private final PhoneColorRepository phoneColorRepository;

    // ──────────────────────────────────────────
    // 모델 마스터 관리 (본사 전용)
    // ──────────────────────────────────────────

    public List<WarehouseDto.ModelResponse> getAllModels() {
        return phoneModelRepository.findAllWithStoragesAndColors()
                .stream()
                .map(WarehouseDto.ModelResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WarehouseDto.ModelResponse createModel(WarehouseDto.CreateModelRequest req) {
        if (phoneModelRepository.existsByMakerAndName(req.maker(), req.name())) {
            throw new IllegalArgumentException(
                    "이미 존재하는 모델입니다: " + req.maker() + " " + req.name());
        }

        PhoneModel model = PhoneModel.builder()
                .maker(req.maker())
                .name(req.name())
                .build();

        if (req.storages() != null) {
            for (var sr : req.storages()) {
                PhoneStorage storage = PhoneStorage.builder()
                        .phoneModel(model)
                        .capacity(sr.capacity())
                        .releasePrice(sr.releasePrice())
                        .build();

                if (sr.colors() != null) {
                    for (var cr : sr.colors()) {
                        PhoneColor color = PhoneColor.builder()
                                .phoneStorage(storage)
                                .colorName(cr.colorName())
                                .hexCode(cr.hexCode())
                                .build();
                        storage.getColors().add(color);
                    }
                }
                model.getStorages().add(storage);
            }
        }

        return WarehouseDto.ModelResponse.from(phoneModelRepository.save(model));
    }

    @Transactional
    public void deactivateModel(UUID modelId) {
        findModel(modelId).deactivate();
    }

    /** PhoneColor 단건 조회 — WarehouseUnitService 에서 호출 */
    public PhoneColor findColor(UUID colorId) {
        return phoneColorRepository.findByIdWithStorageAndModel(colorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "컬러 정보를 찾을 수 없습니다. id=" + colorId));
    }

    // ──────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────

    private PhoneModel findModel(UUID id) {
        return phoneModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "모델을 찾을 수 없습니다. id=" + id));
    }
}
