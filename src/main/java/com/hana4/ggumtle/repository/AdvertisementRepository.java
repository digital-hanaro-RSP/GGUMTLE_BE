package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.advertisement.Advertisement;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
	Optional<Advertisement> findFirstByRiskRating(String risk);
}
