package com.hana4.ggumtle;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationRunner {
	private final PortfolioTemplateRepository portfolioTemplateRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (portfolioTemplateRepository.count() == 0) {
			PortfolioTemplate conservative = PortfolioTemplate.builder()
				.name("CONSERVATIVE")
				.depositWithdrawalRatio(new BigDecimal("0.70"))
				.savingTimeDepositRatio(new BigDecimal("0.20"))
				.investmentRatio(new BigDecimal("0.20"))
				.foreignCurrencyRatio(BigDecimal.ZERO)
				.pensionRatio(new BigDecimal("0.10"))
				.etcRatio(BigDecimal.ZERO)
				.build();

			PortfolioTemplate moderatelyConservative = PortfolioTemplate.builder()
				.name("MODERATELY_CONSERVATIVE")
				.depositWithdrawalRatio(new BigDecimal("0.50"))
				.savingTimeDepositRatio(new BigDecimal("0.30"))
				.investmentRatio(new BigDecimal("0.30"))
				.foreignCurrencyRatio(new BigDecimal("0.05"))
				.pensionRatio(new BigDecimal("0.15"))
				.etcRatio(BigDecimal.ZERO)
				.build();

			PortfolioTemplate balanced = PortfolioTemplate.builder()
				.name("BALANCED")
				.depositWithdrawalRatio(new BigDecimal("0.30"))
				.savingTimeDepositRatio(new BigDecimal("0.40"))
				.investmentRatio(new BigDecimal("0.40"))
				.foreignCurrencyRatio(new BigDecimal("0.10"))
				.pensionRatio(new BigDecimal("0.15"))
				.etcRatio(new BigDecimal("0.05"))
				.build();

			PortfolioTemplate growthOriented = PortfolioTemplate.builder()
				.name("MODERATELY_AGGRESSIVE")
				.depositWithdrawalRatio(new BigDecimal("0.20"))
				.savingTimeDepositRatio(new BigDecimal("0.50"))
				.investmentRatio(new BigDecimal("0.50"))
				.foreignCurrencyRatio(new BigDecimal("0.15"))
				.pensionRatio(new BigDecimal("0.10"))
				.etcRatio(new BigDecimal("0.05"))
				.build();

			PortfolioTemplate aggressive = PortfolioTemplate.builder()
				.name("AGGRESSIVE")
				.depositWithdrawalRatio(new BigDecimal("0.10"))
				.savingTimeDepositRatio(new BigDecimal("0.60"))
				.investmentRatio(new BigDecimal("0.60"))
				.foreignCurrencyRatio(new BigDecimal("0.15"))
				.pensionRatio(new BigDecimal("0.05"))
				.etcRatio(new BigDecimal("0.10"))
				.build();

			portfolioTemplateRepository.saveAll(
				Arrays.asList(conservative, moderatelyConservative, balanced, growthOriented, aggressive));
		}
	}
}
