package com.hana4.ggumtle.dto.dreamAccount;

import java.math.BigDecimal;

import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class DreamAccountRequestDto {

	@Schema(description = "DreamAccount DTO")
	@Getter
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode
	public static class Create {

		// @Schema(description = "DreamAccount과 연결된 사용자", requiredMode = Schema.RequiredMode.REQUIRED)
		// @NotNull(message = "사용자 ID는 필수입니다.")
		// private String userId;

		@Schema(description = "DreamAccount의 잔액", example = "1000.00", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "잔액은 필수입니다.")
		private BigDecimal balance;

		@Schema(description = "DreamAccount의 총액", example = "5000.00", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "총액은 필수입니다.")
		private BigDecimal total;

		public DreamAccount toEntity(User user) {
			if (this.balance == null) {
				this.balance = BigDecimal.ZERO;  // 기본값 설정
			}
			if (this.total == null) {
				this.total = BigDecimal.ZERO;  // 기본값 설정
			}
			return DreamAccount.builder()
				.user(user)
				.balance(this.balance)
				.total(this.total)
				.build();
		}
	}

	@Schema(description = "Amount 추가 DTO")
	@Getter
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode
	public static class AddAmount {

		@Schema(description = "추가할 금액", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "추가할 금액은 필수입니다.")
		private BigDecimal amount;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class DistributeAmount {
		@NotNull(message = "분배 금액은 필수입니다.")
		@Schema(description = "분배할 금액", example = "500.01", requiredMode = Schema.RequiredMode.REQUIRED)
		@DecimalMin(value = "0.0", inclusive = false, message = "분배 금액은 0보다 커야 합니다.")
		private BigDecimal amount;
	}
}