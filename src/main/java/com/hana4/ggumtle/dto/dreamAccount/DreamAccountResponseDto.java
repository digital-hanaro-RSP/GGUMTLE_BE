package com.hana4.ggumtle.dto.dreamAccount;

import java.math.BigDecimal;

import com.hana4.ggumtle.dto.BaseDto;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Generated
public class DreamAccountResponseDto {

	@Schema(description = "꿈통장 정보 응답")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@ToString
	@SuperBuilder
	public static class DreamAccountInfo extends BaseDto {

		@Schema(description = "DreamAccount ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
		private Long id;

		@Schema(description = "사용자 ID", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
		private String userId;

		private User user;

		@Schema(description = "DreamAccount 잔액", example = "1000.01", requiredMode = Schema.RequiredMode.REQUIRED)
		private BigDecimal balance;

		@Schema(description = "DreamAccount 총액", example = "1000.01", requiredMode = Schema.RequiredMode.REQUIRED)
		private BigDecimal total;

		@Schema(description = "safebox 총액", example = "500.01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private BigDecimal totalSafeBox;

		// @Schema(description = "DreamAccount 생성일")
		// private String createdDate; // 예: "2025-01-20"
		//
		// @Schema(description = "DreamAccount 업데이트일")
		// private String updatedDate; // 예: "2025-01-21"

		// DreamAccount 정보를 DreamAccount 엔티티에서 변환
		public static DreamAccountInfo from(DreamAccount dreamAccount, BigDecimal totalSafeBox) {
			return DreamAccountInfo.builder()
				.id(dreamAccount.getId())
				.user(dreamAccount.getUser())
				.userId(dreamAccount.getUser().getId())
				.balance(dreamAccount.getBalance())
				.total(dreamAccount.getTotal())
				.totalSafeBox(totalSafeBox)
				.createdAt(dreamAccount.getCreatedAt())  // 생성일을 String으로 변환
				.updatedAt(dreamAccount.getUpdatedAt())  // 업데이트일을 String으로 변환
				.build();
		}
	}
}
