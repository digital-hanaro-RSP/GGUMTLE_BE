package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;

@ExtendWith(MockitoExtension.class)
class PortfolioTemplateServiceTest {

	@Mock
	private PortfolioTemplateRepository portfolioTemplateRepository;

	@InjectMocks
	private PortfolioTemplateService portfolioTemplateService;

	private PortfolioTemplate portfolioTemplate;

	@BeforeEach
	void setUp() {
		portfolioTemplate = new PortfolioTemplate();
		portfolioTemplate.setName("CONSERVATIVE");
	}

	@Test
	void findByName_ExistingName_ReturnsPortfolioTemplate() {
		when(portfolioTemplateRepository.findByName("CONSERVATIVE")).thenReturn(Optional.of(portfolioTemplate));

		PortfolioTemplate result = portfolioTemplateService.findByName("CONSERVATIVE");

		assertNotNull(result);
		assertEquals("CONSERVATIVE", result.getName());
	}

	@Test
	void findByName_NonExistingName_ThrowsCustomException() {
		when(portfolioTemplateRepository.findByName("NONEXISTENT")).thenReturn(Optional.empty());

		assertThrows(CustomException.class, () -> portfolioTemplateService.findByName("NONEXISTENT"));
	}
}
