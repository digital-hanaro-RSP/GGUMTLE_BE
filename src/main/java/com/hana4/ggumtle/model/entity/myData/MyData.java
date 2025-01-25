package com.hana4.ggumtle.model.entity.myData;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class MyData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
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
