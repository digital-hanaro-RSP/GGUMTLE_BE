package com.hana4.ggumtle.model.entity.portfolioTemplate;

import java.math.BigDecimal;

import com.hana4.ggumtle.model.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class PortfolioTemplate extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private BigDecimal depositWithdrawalRatio;

	@Column(nullable = false)
	private BigDecimal savingTimeDepositRatio;

	@Column(nullable = false)
	private BigDecimal investmentRatio;

	@Column(nullable = false)
	private BigDecimal foreignCurrencyRatio;

	@Column(nullable = false)
	private BigDecimal pensionRatio;

	@Column(nullable = false)
	private BigDecimal etcRatio;
}
