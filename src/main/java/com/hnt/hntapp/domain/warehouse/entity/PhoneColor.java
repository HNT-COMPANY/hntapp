package com.hnt.hntapp.domain.warehouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "phone_colors")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneColor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_storage_id", nullable = false)
    private PhoneStorage phoneStorage;

    // 컬럼명
    @Column(nullable = false)
    private String colorName;

     private String hexCode;

     public void update(String colorName, String hexcode) {
         if (colorName != null) this.colorName = colorName;
         if (hexcode != null) this.hexCode = hexCode;

     }
}
