package com.hnt.hntapp.domain.warehouse.service;

import com.hnt.hntapp.domain.franchise.entity.Franchise;
import com.hnt.hntapp.domain.franchise.repository.FranchiseRepository;
import com.hnt.hntapp.domain.warehouse.dto.WarehouseDto;
import com.hnt.hntapp.domain.warehouse.entity.*;
import com.hnt.hntapp.domain.warehouse.repository.PhoneColorRepository;
import com.hnt.hntapp.domain.warehouse.repository.PhoneModelRepository;
import com.hnt.hntapp.domain.warehouse.repository.WarehouseStockRepository;
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
public class WarehouseService {

    private final PhoneModelRepository     phoneModelRepository;
    private final PhoneColorRepository     phoneColorRepository;   // 신규 추가
    private final WarehouseStockRepository warehouseStockRepository;
    private final FranchiseRepository      franchiseRepository;

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

    // ──────────────────────────────────────────
    // 창고 재고 관리 (본사 전용)
    // ──────────────────────────────────────────

    public List<WarehouseDto.StockResponse> getAllStocks() {
        return warehouseStockRepository.findAllWithDetails()
                .stream()
                .map(WarehouseDto.StockResponse::from)
                .collect(Collectors.toList());
    }

    public List<WarehouseDto.StockResponse> getStocksByFranchise(UUID franchiseId) {
        return warehouseStockRepository.findByFranchiseId(franchiseId)
                .stream()
                .map(WarehouseDto.StockResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WarehouseDto.StockResponse distribute(WarehouseDto.DistributeRequest req) {
        Franchise franchise = franchiseRepository.findById(req.franchiseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "가맹점을 찾을 수 없습니다. id=" + req.franchiseId()));

        PhoneColor color = findColor(req.phoneColorId());

        WarehouseStock stock = warehouseStockRepository
                .findByFranchiseIdAndPhoneColorId(req.franchiseId(), req.phoneColorId())
                .orElseGet(() -> WarehouseStock.builder()
                        .franchise(franchise)
                        .phoneColor(color)
                        .build());

        stock.confirmStock(req.qty());
        return WarehouseDto.StockResponse.from(warehouseStockRepository.save(stock));
    }

    /** 재고 차감 — 전표 제출 시 호출 */
    @Transactional
    public void deductStock(UUID franchiseId, UUID phoneColorId, int qty) {
        WarehouseStock stock = warehouseStockRepository
                .findByFranchiseIdAndPhoneColorId(franchiseId, phoneColorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "재고 정보를 찾을 수 없습니다. franchiseId=" + franchiseId));
        stock.deductStock(qty);
    }

    /** 재고 복구 — 전표 보류 시 호출 */
    @Transactional
    public void addStock(UUID franchiseId, UUID phoneColorId, int qty) {
        WarehouseStock stock = warehouseStockRepository
                .findByFranchiseIdAndPhoneColorId(franchiseId, phoneColorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "재고 정보를 찾을 수 없습니다. franchiseId=" + franchiseId));
        stock.addStock(qty);
    }

    /**
     * PhoneColor 단건 조회
     * 기존: 전체 모델 stream 필터링 → O(N) 비효율
     * 개선: PhoneColorRepository 직접 조회 → O(1)
     */
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
