package com.hana4.ggumtle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
	List<Advertisement> findAllByRiskRatingInOrderByIdDesc(List<String> riskRatings);

	List<Advertisement> findAllByAdType(AdvertisementAdType adType);
}
