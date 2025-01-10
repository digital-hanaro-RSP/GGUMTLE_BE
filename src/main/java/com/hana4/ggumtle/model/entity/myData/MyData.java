package com.hana4.ggumtle.model.entity.myData;

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
public class MyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_MyData_userId_User"))
    private User user;

    @Column(nullable = false)
    private BigDecimal depositWithdrawal;

    @Column(nullable = false)
    private BigDecimal savingTimeDeposit;

    @Column(nullable = false)
    private BigDecimal investment;

    @Column(nullable = false)
    private BigDecimal foreignCurrency;

    @Column(nullable = false)
    private BigDecimal pension;

    @Column(nullable = false)
    private BigDecimal etc;
}
