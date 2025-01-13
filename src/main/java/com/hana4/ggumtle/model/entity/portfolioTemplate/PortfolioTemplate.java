package com.hana4.ggumtle.model.entity.portfolioTemplate;

import com.hana4.ggumtle.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
