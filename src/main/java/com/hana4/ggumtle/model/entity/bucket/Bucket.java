package com.hana4.ggumtle.model.entity.bucket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "Bucket")
public class Bucket extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "dreamAccountId", nullable = false, foreignKey = @ForeignKey(name = "fk_Bucket_dreamAccountId_DreamAccount"))
	private DreamAccount dreamAccount;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_Bucket_userId_User"))
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BucketTagType tagType;

	private LocalDateTime dueDate;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isDueSet;

	private String memo;

	@Enumerated(EnumType.STRING)
	private BucketHowTo howTo;

	private BigDecimal goalAmount;

	private Long followers;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BucketStatus status = BucketStatus.DOING;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isAutoAllocate;

	private BigDecimal allocateAmount;

	private String cronCycle;

	private BigDecimal safeBox;



	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isRecommended;
	private Long originId;

	public void updateFromDto(BucketRequestDto.CreateBucket dto) {
		this.title = dto.getTitle();
		this.tagType = dto.getTagType();
		this.dueDate = dto.getDueDate();
		this.howTo = dto.getHowTo();
		this.isDueSet = dto.getIsDueSet();
		this.isAutoAllocate = dto.getIsAutoAllocate();
		this.allocateAmount = dto.getAllocateAmount();
		this.cronCycle = dto.getCronCycle();
		this.goalAmount = dto.getGoalAmount();
		this.memo = dto.getMemo();
		this.status = BucketStatus.DOING;
		this.isRecommended = dto.getIsRecommended();
		this.originId = dto.getOriginId();
		this.followers = dto.getFollowers();
	}
}
