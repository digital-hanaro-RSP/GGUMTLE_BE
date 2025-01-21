package com.hana4.ggumtle.dto.bucketList;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.user.User;

@ExtendWith(MockitoExtension.class)
class BucketResponseDtoTest {

	// BucketInfo 테스트: 빌더와 생성자
	@Test
	void bucketInfo_whenBuiltWithBuilder_thenAllFieldsAreSet() {
		// Given
		BucketResponseDto.BucketInfo bucketInfo = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.title("Test Bucket")
			.tagType(BucketTagType.DO)
			.dueDate(LocalDateTime.now().plusDays(10))
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(false)
			.allocateAmount(new BigDecimal("5000"))
			.cronCycle("0 0 12 * * ?")
			.goalAmount(new BigDecimal("100000"))
			.memo("Sample Memo")
			.status(BucketStatus.DOING)
			.isRecommended(false)
			.originId(2L)
			.safeBox(new BigDecimal("2000"))
			.followers(150L)
			.userId("123")
			.build();

		// Then
		assertNotNull(bucketInfo);
		assertEquals(1L, bucketInfo.getId());
		assertEquals("Test Bucket", bucketInfo.getTitle());
		assertEquals(BucketTagType.DO, bucketInfo.getTagType());
		assertNotNull(bucketInfo.getDueDate());
		assertEquals(BucketHowTo.MONEY, bucketInfo.getHowTo());
	}

	// Recommendation 테스트
	@Test
	void recommendation_whenBuiltWithBuilder_thenAllFieldsAreSet() {
		// Given
		BucketResponseDto.Recommendation recommendation = BucketResponseDto.Recommendation.builder()
			.followers(100L)
			.title("Recommended Bucket")
			.build();

		// Then
		assertNotNull(recommendation);
		assertEquals(100L, recommendation.getFollowers());
		assertEquals("Recommended Bucket", recommendation.getTitle());
	}

	// BucketInfo.form 테스트: 모든 필드가 설정된 경우
	@Test
	void bucketInfoForm_whenBucketIsValid_thenAllFieldsAreMapped() {
		// Given
		Bucket bucket = Bucket.builder()
			.id(1L)
			.title("Test Bucket")
			.tagType(BucketTagType.DO)
			.dueDate(LocalDateTime.now().plusDays(30))
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.isAutoAllocate(false)
			.allocateAmount(new BigDecimal("5000"))
			.cronCycle("0 0 12 * * ?")
			.goalAmount(new BigDecimal("100000"))
			.memo("Test Memo")
			.status(BucketStatus.DOING)
			.isRecommended(false)
			.originId(2L)
			.safeBox(new BigDecimal("2000"))
			.followers(150L)
			.user(User.builder().id("123").build()) // 유효한 User 설정
			.build();

		// When
		BucketResponseDto.BucketInfo result = BucketResponseDto.BucketInfo.form(bucket);

		// Then
		assertNotNull(result);
		assertEquals(bucket.getId(), result.getId());
		assertEquals(bucket.getTitle(), result.getTitle());
		assertEquals(bucket.getTagType(), result.getTagType());
		assertEquals(bucket.getDueDate(), result.getDueDate());
		assertEquals(bucket.getHowTo(), result.getHowTo());
		assertEquals(bucket.getIsDueSet(), result.getIsDueSet());
		assertEquals(bucket.getIsAutoAllocate(), result.getIsAutoAllocate());
		assertEquals(bucket.getAllocateAmount(), result.getAllocateAmount());
		assertEquals(bucket.getCronCycle(), result.getCronCycle());
		assertEquals(bucket.getGoalAmount(), result.getGoalAmount());
		assertEquals(bucket.getMemo(), result.getMemo());
		assertEquals(bucket.getStatus(), result.getStatus());
		assertEquals(bucket.getSafeBox(), result.getSafeBox());
		assertEquals(bucket.getIsRecommended(), result.getIsRecommended());
		assertEquals(bucket.getFollowers(), result.getFollowers());
		assertEquals(bucket.getUser().getId().toString(), result.getUserId());
	}

	// BucketInfo.form 테스트: 일부 필드가 null인 경우
	@Test
	void bucketInfoForm_whenOptionalFieldsAreNull_thenHandlesGracefully() {
		// Given
		Bucket bucket = Bucket.builder()
			.id(1L)
			.title("Test Bucket")
			.tagType(null)
			.dueDate(null)
			.howTo(null)
			.user(User.builder().id("123").build()) // 유효한 User 설정
			.build();

		// When
		BucketResponseDto.BucketInfo result = BucketResponseDto.BucketInfo.form(bucket);

		// Then
		assertNotNull(result);
		assertEquals(bucket.getId(), result.getId());
		assertEquals(bucket.getTitle(), result.getTitle());
		assertNull(result.getTagType());
		assertNull(result.getDueDate());
		assertNull(result.getHowTo());
	}
}
