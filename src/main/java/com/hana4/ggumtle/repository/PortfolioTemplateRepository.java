package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;

public interface PortfolioTemplateRepository extends JpaRepository<PortfolioTemplate, Long> {
	Optional<PortfolioTemplate> findByName(String name);
}
