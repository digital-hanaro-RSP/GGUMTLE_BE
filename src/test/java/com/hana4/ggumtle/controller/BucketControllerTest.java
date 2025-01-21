package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
import com.hana4.ggumtle.dto.recommendation.RecommendationResponseDto;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.security.filter.JwtAuthFilter;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.service.BucketService;

@WebMvcTest(BucketController.class)
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class BucketControllerTest {

	@MockitoBean
	BucketService bucketService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	JwtAuthFilter jwtAuthFilter;

	@MockitoBean
	JwtProvider jwtProvider;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	void createBucket_WithRecommendations_success() throws Exception {
		// given
		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
			.title("New Bucket")
			.tagType(BucketTagType.BE) // Enum 값 사용
			.dueDate(LocalDateTime.now().plusDays(30))
			.howTo(BucketHowTo.MONEY) // Enum 값 사용
			.isDueSet(true)
			.safeBox(BigDecimal.valueOf(1000))
			.isAutoAllocate(false)
			.allocateAmount(BigDecimal.valueOf(500))
			.followers(10L)
			.cronCycle("0 0 12 * * ?") // 예제 주기 표현식
			.goalAmount(BigDecimal.valueOf(10000))
			.status(BucketStatus.DOING) // Enum 값 사용
			.memo("This is a test bucket.")
			.isRecommended(true)
			.originId(1L) // Optional 필드
			.build();

		BucketResponseDto.Recommendation recommendation = BucketResponseDto.Recommendation.builder()
			.followers(20L)
			.title("Recommended Bucket")
			.build();

		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.userId("user123")
			.title(requestDto.getTitle())
			.tagType(requestDto.getTagType())
			.dueDate(requestDto.getDueDate())
			.howTo(requestDto.getHowTo())
			.isDueSet(requestDto.getIsDueSet())
			.isAutoAllocate(requestDto.getIsAutoAllocate())
			.allocateAmount(requestDto.getAllocateAmount())
			.cronCycle(requestDto.getCronCycle())
			.goalAmount(requestDto.getGoalAmount())
			.memo(requestDto.getMemo())
			.status(requestDto.getStatus())
			.isRecommended(requestDto.getIsRecommended())
			.originId(requestDto.getOriginId())
			.followers(requestDto.getFollowers())
			.safeBox(requestDto.getSafeBox())
			.recommendations(List.of(recommendation))
			.build();

		// mock 설정
		CustomUserDetails mockUser = mock(CustomUserDetails.class);
		when(mockUser.getUser()).thenReturn(new User());
		when(bucketService.createBucket(any(BucketRequestDto.Create.class), any(), any())).thenReturn(responseDto);

		String reqBody = objectMapper.writeValueAsString(requestDto);
		// when
		mockMvc.perform(MockMvcRequestBuilders.post("/api/buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(responseDto.getId()))
			.andExpect(jsonPath("$.data.userId").value(responseDto.getUserId()))
			.andExpect(jsonPath("$.data.title").value(responseDto.getTitle()))
			.andExpect(jsonPath("$.data.tagType").value(responseDto.getTagType().name()))
			.andExpect(jsonPath("$.data.dueDate").exists())
			.andExpect(jsonPath("$.data.howTo").value(responseDto.getHowTo().name()))
			.andExpect(jsonPath("$.data.isDueSet").value(responseDto.getIsDueSet()))
			.andExpect(jsonPath("$.data.isAutoAllocate").value(responseDto.getIsAutoAllocate()))
			.andExpect(jsonPath("$.data.allocateAmount").value(responseDto.getAllocateAmount()))
			.andExpect(jsonPath("$.data.cronCycle").value(responseDto.getCronCycle()))
			.andExpect(jsonPath("$.data.goalAmount").value(responseDto.getGoalAmount()))
			.andExpect(jsonPath("$.data.memo").value(responseDto.getMemo()))
			.andExpect(jsonPath("$.data.status").value(responseDto.getStatus().name()))
			.andExpect(jsonPath("$.data.isRecommended").value(responseDto.getIsRecommended()))
			.andExpect(jsonPath("$.data.originId").value(responseDto.getOriginId()))
			.andExpect(jsonPath("$.data.safeBox").value(responseDto.getSafeBox()))
			.andExpect(jsonPath("$.data.followers").value(responseDto.getFollowers()))
			.andExpect(jsonPath("$.data.recommendations[0].title").value(recommendation.getTitle()))
			.andExpect(jsonPath("$.data.recommendations[0].followers").value(recommendation.getFollowers()))
			.andDo(print());

		// then
		verify(bucketService).createBucket(any(BucketRequestDto.Create.class), any(), any());
	}

	@Test
	void updateBucket_success() throws Exception {
		Long bucketId = 1L;
		BucketRequestDto.Create requestDto = BucketRequestDto.Create.builder()
			.title("updated Bucket")
			.tagType(BucketTagType.BE) // Enum 값 사용
			.dueDate(LocalDateTime.now().plusDays(30))
			.howTo(BucketHowTo.MONEY) // Enum 값 사용
			.isDueSet(true)
			.safeBox(BigDecimal.valueOf(1000))
			.isAutoAllocate(false)
			.allocateAmount(BigDecimal.valueOf(500))
			.followers(10L)
			.cronCycle("0 0 12 * * ?") // 예제 주기 표현식
			.goalAmount(BigDecimal.valueOf(10000))
			.status(BucketStatus.DOING) // Enum 값 사용
			.memo("This is a updated bucket.")
			.isRecommended(true)
			.originId(1L) // Optional 필드
			.build();

		BucketResponseDto.Recommendation recommendation = BucketResponseDto.Recommendation.builder()
			.followers(20L)
			.title("Recommended Bucket")
			.build();
		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.userId("user123")
			.title(requestDto.getTitle())
			.tagType(requestDto.getTagType())
			.dueDate(requestDto.getDueDate())
			.howTo(requestDto.getHowTo())
			.isDueSet(requestDto.getIsDueSet())
			.isAutoAllocate(requestDto.getIsAutoAllocate())
			.allocateAmount(requestDto.getAllocateAmount())
			.cronCycle(requestDto.getCronCycle())
			.goalAmount(requestDto.getGoalAmount())
			.memo(requestDto.getMemo())
			.status(requestDto.getStatus())
			.isRecommended(requestDto.getIsRecommended())
			.originId(requestDto.getOriginId())
			.followers(requestDto.getFollowers())
			.safeBox(requestDto.getSafeBox())
			.recommendations(List.of(recommendation))
			.build();

		CustomUserDetails mockUser = mock(CustomUserDetails.class);
		when(mockUser.getUser()).thenReturn(new User());
		given(bucketService.updateBucket(eq(bucketId), any(BucketRequestDto.Create.class)))
			.willReturn(responseDto);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/buckets/{bucketId}", bucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.id").value(responseDto.getId()))
			.andExpect(jsonPath("$.data.title").value(responseDto.getTitle()))
			.andExpect(jsonPath("$.data.memo").value(responseDto.getMemo()))
			.andDo(print());

		verify(bucketService).updateBucket(bucketId, requestDto);
	}

	@Test
	void updateBucketStatus_success() throws Exception {
		Long bucketId = 1L;

		Map<String, String> updates = new HashMap<>();
		updates.put("status", BucketStatus.DONE.name()); // 상태만 업데이트

		BucketResponseDto.Recommendation recommendation = BucketResponseDto.Recommendation.builder()
			.followers(20L)
			.title("Recommended Bucket")
			.build();
		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.userId("user123")
			.title("Bucket")
			.tagType(BucketTagType.BE)
			.dueDate(LocalDateTime.now().plusDays(30))
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.safeBox(BigDecimal.valueOf(1000))
			.isAutoAllocate(false)
			.allocateAmount(BigDecimal.valueOf(500))
			.cronCycle("0 0 12 * * ?")
			.goalAmount(BigDecimal.valueOf(10000))
			.status(BucketStatus.DONE) // 수정된 상태
			.memo("This is a bucket.")
			.isRecommended(true)
			.originId(1L)
			.followers(10L)
			.recommendations(List.of(recommendation))
			.build();

		CustomUserDetails mockUser = mock(CustomUserDetails.class);
		when(mockUser.getUser()).thenReturn(new User());

		given(bucketService.updateBucketStatus(eq(bucketId), anyMap()))
			.willReturn(responseDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/buckets/{bucketId}", bucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updates)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.id").value(responseDto.getId()))
			.andExpect(jsonPath("$.data.status").value(responseDto.getStatus().name()))
			.andDo(print());

		verify(bucketService).updateBucketStatus(bucketId, updates);
	}

	@Test
	void deleteBucket_success() throws Exception {
		Long bucketId = 1L;

		willDoNothing().given(bucketService).deleteBucket(bucketId);

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/buckets/{bucketId}", bucketId))
			.andExpect(status().isOk())
			.andDo(print());

		verify(bucketService).deleteBucket(bucketId);
	}

	@Test
	void getAllBuckets_ShouldReturnBucketList() throws Exception {
		// BucketResponseDto.BucketInfo 객체를 생성
		List<BucketResponseDto.BucketInfo> buckets = Arrays.asList(
			BucketResponseDto.BucketInfo.builder()
				.id(1L)
				.title("Bucket1")
				.tagType(BucketTagType.BE) // Enum 값 사용
				.dueDate(LocalDateTime.now().plusDays(30))
				.howTo(BucketHowTo.MONEY)
				.isDueSet(true)
				.isAutoAllocate(false)
				.allocateAmount(BigDecimal.valueOf(500))
				.cronCycle("0 0 12 * * ?")
				.goalAmount(BigDecimal.valueOf(10000))
				.status(BucketStatus.DOING)
				.isRecommended(true)
				.originId(1L)
				.followers(10L)
				.safeBox(BigDecimal.valueOf(1000))
				.build(),

			BucketResponseDto.BucketInfo.builder()
				.id(2L)
				.title("Bucket2")
				.tagType(BucketTagType.BE)
				.dueDate(LocalDateTime.now().plusDays(30))
				.howTo(BucketHowTo.MONEY)
				.isDueSet(true)
				.isAutoAllocate(true)
				.allocateAmount(BigDecimal.valueOf(1000))
				.cronCycle("0 0 12 * * ?")
				.goalAmount(BigDecimal.valueOf(15000))
				.status(BucketStatus.DONE)
				.isRecommended(false)
				.originId(2L)
				.followers(20L)
				.safeBox(BigDecimal.valueOf(2000))
				.build()
		);

		given(bucketService.getAllBuckets()).willReturn(buckets);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/buckets"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.length()").value(buckets.size()))
			.andDo(print());

		verify(bucketService).getAllBuckets();
	}

	@Test
	void getBucketById_ShouldReturnBucket() throws Exception {
		Long bucketId = 1L;
		BucketResponseDto.Recommendation recommendation = BucketResponseDto.Recommendation.builder()
			.followers(20L)
			.title("Recommended Bucket")
			.build();
		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.userId("user123")
			.title("Bucket")
			.tagType(BucketTagType.BE)
			.dueDate(LocalDateTime.now().plusDays(30))
			.howTo(BucketHowTo.MONEY)
			.isDueSet(true)
			.safeBox(BigDecimal.valueOf(1000))
			.isAutoAllocate(false)
			.allocateAmount(BigDecimal.valueOf(500))
			.cronCycle("0 0 12 * * ?")
			.goalAmount(BigDecimal.valueOf(10000))
			.status(BucketStatus.DONE) // 수정된 상태
			.memo("This is a bucket.")
			.isRecommended(true)
			.originId(1L)
			.followers(10L)
			.recommendations(List.of(recommendation))
			.build();

		given(bucketService.getBucketById(bucketId)).willReturn(responseDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/buckets/{bucketId}", bucketId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.id").value(responseDto.getId()))
			.andDo(print());

		verify(bucketService).getBucketById(bucketId);
	}

	@Test
	void getTop3RecommendedBuckets_ShouldReturnTop3Recommendations() throws Exception {
		// 5개의 BucketInfo 객체를 생성 (followers 수치를 기준으로 상위 3개를 선택)
		List<RecommendationResponseDto.RecommendedBucketInfo> recommendations = Arrays.asList(
			RecommendationResponseDto.RecommendedBucketInfo.builder()
				.tagType(BucketTagType.BE) // 적절한 BucketTagType 설정
				.recommendations(Arrays.asList(
					RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
						.id(1L)
						.title("Bucket 1")
						.followers(500L)
						.build(),
					RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
						.id(2L)
						.title("Bucket 2")
						.followers(300L)
						.build(),
					RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
						.id(3L)
						.title("Bucket 3")
						.followers(200L)
						.build()
				))
				.build()
		);

		// bucketService에서 추천 버킷을 반환하도록 설정
		given(bucketService.getRecommendedBuckets()).willReturn(recommendations);

		// mockMvc로 GET 요청을 보내고 응답을 검증
		mockMvc.perform(MockMvcRequestBuilders.get("/api/buckets/recommendation"))
			.andExpect(status().isOk())  // 응답 상태 코드 확인
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))  // 응답 Content-Type 확인
			.andExpect(jsonPath("$.data[0].recommendations.length()").value(3)) // 추천된 버킷의 개수가 3개인지 확인
			.andExpect(jsonPath("$.data[0].recommendations[0].id").value(1L)) // followers가 가장 많은 버킷 ID 확인
			.andExpect(jsonPath("$.data[0].recommendations[1].id").value(2L)) // followers 두번째로 많은 버킷 ID 확인
			.andExpect(jsonPath("$.data[0].recommendations[2].id").value(3L)) // followers 세번째로 많은 버킷 ID 확인
			.andDo(print());  // 출력 확인

		// bucketService의 getRecommendedBuckets 메소드 호출 검증
		verify(bucketService).getRecommendedBuckets();
	}

}
