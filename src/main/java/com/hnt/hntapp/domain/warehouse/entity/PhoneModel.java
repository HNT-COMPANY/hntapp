package com.hnt.hntapp.domain.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 휴대폰 모델 마스터
 * - 본사 관리자만 등록/수정/삭제 가능
 * - 예: iPhone 16 Pro, Galaxy S25 Ultra
 */
@Entity
@Table(name = "phone_models")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /** 제조사 (Apple, Samsung, LG 등) */
    @Column(nullable = false)
    private String maker;

    /** 모델명 (iPhone 16 Pro, Galaxy S25 Ultra 등) */
    @Column(nullable = false)
    private String name;

    /** 활성화 여부 (단종 시 false) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "phoneModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PhoneStorage> storages = new ArrayList<>();

    public void update(String maker, String name) {
        if (maker != null) this.maker = maker;
        if (name  != null) this.name  = name;
    }

    public void deactivate() { this.isActive = false; }
    public void activate()   { this.isActive = true;  }
}
