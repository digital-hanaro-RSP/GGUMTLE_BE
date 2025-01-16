package com.hana4.ggumtle.model.entity.goalPortfolio;

import java.math.BigDecimal;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GoalPortfolio extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_GoalPortfolio_userId_User"))
	private User user;

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

	@ManyToOne
	@JoinColumn(name = "templateId", nullable = false, foreignKey = @ForeignKey(name = "fk_GoalPortfolio_templateId_PortfolioTemplate"))
	private PortfolioTemplate template;
}
