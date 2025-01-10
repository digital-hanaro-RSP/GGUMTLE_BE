package com.hana4.ggumtle.model.entity.goalPortfolio;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GoalPortfolio extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

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
