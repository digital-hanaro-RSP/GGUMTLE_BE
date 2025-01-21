package com.hana4.ggumtle.dto.bucketList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class BucketRequestDto {
	@Schema(description = "버킷리스트 DTO")
	@Getter
	@Builder(toBuilder = true) // 기존 필드 값은 유지하고 변경하려는 필드만 수정된 값으로 교체
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode
	public static class Create {
		@Schema(description = "버킷리스트 제목", example = "유럽 여행", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "제목을 입력하세요.")
		private String title;

		@Schema(description = "버킷리스트 태그타입", example = "GO", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "태그타입을 입력하세요.")
		private BucketTagType tagType;

		@NotNull(message = "완료 날짜를 입력하세요.")
		private LocalDateTime dueDate;

		@NotNull(message = "버킷 종류를 입력하세요.")
		private BucketHowTo howTo;

		private Boolean isDueSet;

		private BigDecimal safeBox;
		private Boolean isAutoAllocate;

		private BigDecimal allocateAmount;
		private Long followers;

		@NotEmpty(message = "주기를 입력하세요.")
		private String cronCycle;

		@NotNull(message = "목표 금액을 입력하세요.")
		private BigDecimal goalAmount;

		@NotNull(message = "버킷 상태를 입력하세요.")
		private BucketStatus status;

		private String memo;

		// 추가 필드 (추천 플로우에서만 사용)
		private Boolean isRecommended; // 선택적 필드
		private Long originId;   // 선택적 필드

		public Bucket toEntity(User user, DreamAccount dreamAccount) {
			return new Bucket().toBuilder()
				.user(user)
				.dreamAccount(dreamAccount)
				.safeBox(this.safeBox)
				.title(this.title)
				.tagType(this.tagType)
				.dueDate(this.dueDate)
				.howTo(this.howTo)
				.isDueSet(this.isDueSet)
				.isAutoAllocate(this.isAutoAllocate)
				.allocateAmount(this.allocateAmount)
				.cronCycle(this.cronCycle)
				.goalAmount(this.goalAmount)
				.memo(this.memo)
				.status(this.status)
				.isRecommended(this.isRecommended)
				.originId(this.originId)
				.followers(this.followers)
				.build();
		}
	}
}
