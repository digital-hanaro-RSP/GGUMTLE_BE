package com.hana4.ggumtle.dto.dreamAccount;

import java.math.BigDecimal;

import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class DreamAccountResponseDto {

	@Getter
	@Builder
	@AllArgsConstructor
	public static class DreamAccountInfo {

		@Schema(description = "DreamAccount ID")
		private Long id;

		@Schema(description = "사용자 ID")
		private String userId;

		@Schema(description = "DreamAccount 잔액")
		private BigDecimal balance;

		@Schema(description = "DreamAccount 총액")
		private BigDecimal total;

		@Schema
		private BigDecimal totalSafeBox;

		// @Schema(description = "DreamAccount 생성일")
		// private String createdDate; // 예: "2025-01-20"
		//
		// @Schema(description = "DreamAccount 업데이트일")
		// private String updatedDate; // 예: "2025-01-21"

		// DreamAccount 정보를 DreamAccount 엔티티에서 변환
		public static DreamAccountInfo fromEntity(DreamAccount dreamAccount, BigDecimal totalSafeBox) {
			return DreamAccountInfo.builder()
				.id(dreamAccount.getId())
				.userId(dreamAccount.getUser().getId())
				.balance(dreamAccount.getBalance())
				.total(dreamAccount.getTotal())
				.totalSafeBox(totalSafeBox)
				// .createdDate(dreamAccount.getCreatedDate().toString())  // 생성일을 String으로 변환
				// .updatedDate(dreamAccount.getUpdatedDate().toString())  // 업데이트일을 String으로 변환
				.build();
		}
	}
}