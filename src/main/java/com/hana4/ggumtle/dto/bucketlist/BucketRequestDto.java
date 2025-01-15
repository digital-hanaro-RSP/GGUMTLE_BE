package com.hana4.ggumtle.dto.bucketlist;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 추가
public class BucketRequestDto {

	@NotNull
	private String title;

	@NotNull
	private BucketTagType tagType;

	@NotNull
	private LocalDateTime dueDate;

	@NotNull
	private BucketHowTo howTo;

	private Boolean isDueSet;

	private BigDecimal safeBox;
	private Boolean isAutoAllocate;

	private BigDecimal allocateAmount;

	@NotNull
	private String cronCycle;

	@NotNull
	private BigDecimal goalAmount;

	@NotNull
	private BucketStatus status;

	private String memo;

	// 추가 필드 (추천 플로우에서만 사용)
	private Boolean isRecommended; // 선택적 필드
	private BigDecimal originId;   // 선택적 필드

	@Builder // Builder 패턴 사용
	@JsonCreator // Jackson이 생성자를 인식하도록 추가
	public BucketRequestDto(
		@JsonProperty("title") @NotNull String title,
		@JsonProperty("tagType") @NotNull BucketTagType tagType,
		@JsonProperty("dueDate") @NotNull LocalDateTime dueDate,
		@JsonProperty("howTo") @NotNull BucketHowTo howTo,
		@JsonProperty("isDueSet") Boolean isDueSet,
		@JsonProperty("safeBox") BigDecimal safeBox,
		@JsonProperty("isAutoAllocate") Boolean isAutoAllocate,
		@JsonProperty("allocateAmount") BigDecimal allocateAmount,
		@JsonProperty("cronCycle") @NotNull String cronCycle,
		@JsonProperty("goalAmount") @NotNull BigDecimal goalAmount,
		@JsonProperty("status") @NotNull BucketStatus status,
		@JsonProperty("memo") String memo,
		@JsonProperty("isRecommended") Boolean isRecommended,
		@JsonProperty("originId") BigDecimal originId
	) {
		this.title = title;
		this.tagType = tagType;
		this.dueDate = dueDate;
		this.howTo = howTo;
		this.isDueSet = isDueSet;
		this.safeBox = safeBox;
		this.isAutoAllocate = isAutoAllocate;
		this.allocateAmount = allocateAmount;
		this.cronCycle = cronCycle;
		this.goalAmount = goalAmount;
		this.status = status;
		this.memo = memo;
		this.isRecommended = isRecommended;
		this.originId = originId;
	}

	public Bucket toEntity() {
		return new Bucket().toBuilder()
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
			.build();
	}
}
