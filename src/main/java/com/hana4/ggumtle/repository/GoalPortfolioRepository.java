package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;

public interface GoalPortfolioRepository extends JpaRepository<GoalPortfolio, Long> {
	Optional<GoalPortfolio> findByUserId(String userId);
}
