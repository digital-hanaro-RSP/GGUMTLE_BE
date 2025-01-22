package com.hana4.ggumtle.model.entity.advertisement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private AdvertisementAdType adType = AdvertisementAdType.HANA;
}
