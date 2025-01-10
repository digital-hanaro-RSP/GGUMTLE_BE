package com.hana4.ggumtle.model.entity.advertisement;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Advertisement")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AdvertisementProductType productType;

    private String productName;

    private String bannerImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementLocationType locationType;

    private String security;

    private String riskRating;

    private String yield;

    @Column(nullable = false)
    private String link;
}
