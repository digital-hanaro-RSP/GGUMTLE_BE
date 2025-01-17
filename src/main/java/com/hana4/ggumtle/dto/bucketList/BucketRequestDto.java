package com.hana4.ggumtle.dto.bucketList;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BucketRequestDto {
	@Getter
	@Builder(toBuilder = true) // 기존 필드 값은 유지하고 변경하려는 필드만 수정된 값으로 교체
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode
	public static class Create {
		@NotEmpty(message = "제목을 입력하세요.")
		private String title;

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
		private BigDecimal originId;   // 선택적 필드

		public Bucket toEntity(User user) {
			return new Bucket().toBuilder()
				.user(user)
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
