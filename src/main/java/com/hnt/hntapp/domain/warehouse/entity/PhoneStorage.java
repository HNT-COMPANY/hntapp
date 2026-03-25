package com.hnt.hntapp.domain.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phone_storages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_model_id", nullable = false)
    private PhoneModel phoneModel;

    /** 용량명 (128GB, 256GB, 512GB, 1TB) */
    @Column(nullable = false)
    private String capacity;

    /** 출고가 (원) */
    private Long releasePrice;

    /** color → colors 복수형으로 수정 */
    @OneToMany(mappedBy = "phoneStorage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PhoneColor> colors = new ArrayList<>();

    public void updatePrice(Long price) {
        this.releasePrice = price;
    }
}
