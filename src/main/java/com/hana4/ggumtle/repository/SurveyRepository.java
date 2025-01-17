package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.survey.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {
	Optional<Survey> findByUserId(String userId);
}
