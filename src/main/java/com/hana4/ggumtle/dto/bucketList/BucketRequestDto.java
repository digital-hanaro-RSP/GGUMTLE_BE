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
	public static class CreateBucket {
		@Schema(description = "버킷리스트 제목", example = "유럽 여행", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "제목을 입력하세요.")
		private String title;

		@Schema(description = "버킷리스트 태그타입", example = "GO", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "태그타입을 입력하세요.")
		private BucketTagType tagType;

		@Schema(description = "버킷리스트 완료날짜", example = "2025-01-21", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "완료 날짜를 입력하세요.")
		private LocalDateTime dueDate;

		@Schema(description = "버킷리스트 종류", example = "MONEY", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "버킷 종류를 입력하세요.")
		private BucketHowTo howTo;

		@Schema(description = "완료일 설정 유무", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
		private Boolean isDueSet;

		// safeBox 액수가 request에서 왜 필요한가용?
		@Schema(description = "safeBox 액수", example = "", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private BigDecimal safeBox;

		@Schema(description = "자동분배 설정 유무", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
		private Boolean isAutoAllocate;

		@Schema(description = "자동분배 할당 액수", example = "500.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private BigDecimal allocateAmount;

		@Schema(description = "팔로워 수", example = "3000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private Long followers;

		@Schema(description = "자동분배 주기", example = "0 0 1 * *", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String cronCycle;

		@Schema(description = "버킷리스트 목표 금액", example = "10000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private BigDecimal goalAmount;

		// 상태도 처음에 만들때는 DOING으로
		// @Schema(description = "버킷리스트 상태", example = "DOING", requiredMode = Schema.RequiredMode.REQUIRED)
		// @NotNull(message = "버킷 상태를 입력하세요.")
		// private BucketStatus status;

		@Schema(description = "버킷리스트 메모", example = "여행 꼭 가고싶다", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private String memo;

		// 추가 필드 (추천 플로우에서만 사용)
		@Schema(description = "추천 버킷리스트인지 여부", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private Boolean isRecommended; // 선택적 필드

		@Schema(description = "추천 버킷이라면 오리지널 아이디", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
		private Long originId;   // 선택적 필드

		public Bucket from(User user, DreamAccount dreamAccount) {

			if (this.howTo == BucketHowTo.MONEY) {
				safeBox = BigDecimal.ZERO;
			} else {
				safeBox = null;
			}
			return new Bucket().toBuilder()
				.user(user)
				.dreamAccount(dreamAccount)
				.safeBox(safeBox)
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
				// .status(this.status)
				.isRecommended(this.isRecommended)
				.originId(this.originId)
				.followers(this.followers)
				.build();
		}
	}

	@Schema(description = "수정된 버킷리스트 상태 정보 DTO")
	@Getter
	@Builder(toBuilder = true) // 기존 필드 값은 유지하고 변경하려는 필드만 수정된 값으로 교체
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode
	public static class UpdateBucketStatus {
		@Schema(description = "수정된 버킷리스트 상태", example = "DONE", requiredMode = Schema.RequiredMode.REQUIRED)
		private BucketStatus status;

		public BucketStatus getStatus() {
			return status;
		}
	}
}
