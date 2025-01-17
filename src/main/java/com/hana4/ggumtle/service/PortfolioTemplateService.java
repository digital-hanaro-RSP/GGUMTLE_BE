package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioTemplateService {
	private final PortfolioTemplateRepository portfolioTemplateRepository;

	public PortfolioTemplate findByName(String investmentTypeName) {
		return portfolioTemplateRepository.findByName(
				investmentTypeName)
			.orElseThrow(() -> new CustomException(
				ErrorCode.NOT_FOUND, "investmentType을 찾을 수 없습니다."));
	}
}
