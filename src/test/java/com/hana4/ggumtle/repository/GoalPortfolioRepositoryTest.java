package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.model.entity.goalPortfolio.GoalPortfolio;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GoalPortfolioRepositoryTest {

	@Autowired
	private GoalPortfolioRepository goalPortfolioRepository;

	@Autowired
	private PortfolioTemplateRepository portfolioTemplateRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		portfolioTemplateRepository.deleteAll();
	}

	@Test
	void testFindByUser() {
		// Arrange
		User user = User.builder()
			.tel("010-1234-5678")
			.password("defaultPassword")
			.name("Test User")
			.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
			.gender("m")
			.nickname("TestNickname")
			.role(UserRole.USER)
			.build();
		user = userRepository.save(user);

		PortfolioTemplate template = PortfolioTemplate.builder()
			.name("BALANCED")
			.depositWithdrawalRatio(new BigDecimal("0.30"))
			.savingTimeDepositRatio(new BigDecimal("0.40"))
			.investmentRatio(new BigDecimal("0.40"))
			.foreignCurrencyRatio(new BigDecimal("0.10"))
			.pensionRatio(new BigDecimal("0.15"))
			.etcRatio(new BigDecimal("0.05"))
			.build();
		template = portfolioTemplateRepository.save(template);

		GoalPortfolio goalPortfolio = GoalPortfolio.builder()
			.user(user)
			.depositWithdrawalRatio(BigDecimal.valueOf(0.30))
			.savingTimeDepositRatio(BigDecimal.valueOf(0.20))
			.investmentRatio(BigDecimal.valueOf(0.20))
			.foreignCurrencyRatio(BigDecimal.valueOf(0.10))
			.pensionRatio(BigDecimal.valueOf(0.15))
			.etcRatio(BigDecimal.valueOf(0.05))
			.template(template)
			.build();
		goalPortfolioRepository.save(goalPortfolio);

		// Act
		Optional<GoalPortfolio> foundGoalPortfolio = goalPortfolioRepository.findByUserId(user.getId());

		// Assert
		assertThat(foundGoalPortfolio).isPresent();
		assertThat(foundGoalPortfolio.get().getUser().getTel()).isEqualTo("010-1234-5678");
		assertThat(foundGoalPortfolio.get().getDepositWithdrawalRatio()).isEqualTo(BigDecimal.valueOf(0.30));
	}
}
