package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PortfolioTemplateRepositoryTest {

	@Autowired
	private PortfolioTemplateRepository portfolioTemplateRepository;

	@BeforeEach
	void setUp() {
		portfolioTemplateRepository.deleteAll();
	}

	@Test
	void testFindByName() {
		// Arrange
		PortfolioTemplate template = PortfolioTemplate.builder()
			.name("CONSERVATIVE")
			.depositWithdrawalRatio(BigDecimal.valueOf(0.40))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.30))
			.investmentRatio(BigDecimal.valueOf(0.10))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.05))
			.etcRatio(BigDecimal.valueOf(0.05))
			.build();
		portfolioTemplateRepository.save(template);

		// Act
		Optional<PortfolioTemplate> foundTemplate = portfolioTemplateRepository.findByName("CONSERVATIVE");

		// Assert
		assertThat(foundTemplate).isPresent();
		assertThat(foundTemplate.get().getName()).isEqualTo("CONSERVATIVE");
		assertThat(foundTemplate.get().getDepositWithdrawalRatio()).isEqualTo(BigDecimal.valueOf(0.40));
	}
}
