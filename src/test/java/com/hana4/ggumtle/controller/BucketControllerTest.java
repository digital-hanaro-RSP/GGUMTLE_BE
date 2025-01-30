package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

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
import com.hana4.ggumtle.service.BucketService;
import com.hana4.ggumtle.service.DreamAccountService;

@WebMvcTest(controllers = BucketController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	})
@Import(TestSecurityConfig.class)
class BucketControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext webApplicationContext;

	@MockitoBean
	BucketService bucketService;

	@MockitoBean
	DreamAccountService dreamAccountService;

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	@WithMockCustomUser
	void createBucket() throws Exception {

		// given
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("Test Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(String.valueOf(LocalDateTime.now()))
			.howTo(BucketHowTo.MONEY)
			.isAutoAllocate(true)
			.allocateAmount(BigDecimal.valueOf(1000))
			.goalAmount(BigDecimal.valueOf(5000))
			.memo("Test Memo")
			.isRecommended(false)
			.originId(null)
			.build();

		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.title("Test Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDateTime.now())
			.howTo(BucketHowTo.MONEY)
			.isAutoAllocate(true)
			.allocateAmount(BigDecimal.valueOf(1000))
			.goalAmount(BigDecimal.valueOf(5000))
			.isRecommended(false)
			.memo("Test Memo")
			.build();

		// mock behavior
		when(bucketService.createBucket(any(BucketRequestDto.CreateBucket.class), any(User.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(post("/buckets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.title").value("Test Bucket"))
			.andExpect(jsonPath("$.data.tagType").value("GO"))
			.andExpect(jsonPath("$.data.howTo").value("MONEY"))
			.andExpect(jsonPath("$.data.isAutoAllocate").value(true))
			.andExpect(jsonPath("$.data.allocateAmount").value(1000))
			.andExpect(jsonPath("$.data.goalAmount").value(5000))
			.andExpect(jsonPath("$.data.memo").value("Test Memo"))
			.andExpect(jsonPath("$.data.isRecommended").value(false));
	}

	@Test
	@WithMockCustomUser
	void updateBucket() throws Exception {
		Long bucketId = 1L;
		BucketRequestDto.CreateBucket requestDto = BucketRequestDto.CreateBucket.builder()
			.title("Updated Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(String.valueOf(LocalDateTime.now().plusDays(1)))
			.howTo(BucketHowTo.MONEY)
			.isAutoAllocate(true)
			.allocateAmount(BigDecimal.valueOf(2000))
			.goalAmount(BigDecimal.valueOf(10000))
			.memo("Updated Memo")
			.isRecommended(true)
			.originId(null)
			.build();

		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(bucketId)
			.title("Updated Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDateTime.now().plusDays(1))
			.howTo(BucketHowTo.MONEY)
			.isAutoAllocate(true)
			.allocateAmount(BigDecimal.valueOf(2000))
			.goalAmount(BigDecimal.valueOf(10000))
			.isRecommended(true)
			.memo("Updated Memo")
			.build();

		// mock behavior
		when(bucketService.updateBucket(any(User.class), eq(bucketId),
			any(BucketRequestDto.CreateBucket.class))).thenReturn(responseDto);

		// when & then
		mockMvc.perform(put("/buckets/{bucketId}", bucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(bucketId))
			.andExpect(jsonPath("$.data.title").value("Updated Bucket"))
			.andExpect(jsonPath("$.data.tagType").value("GO"))
			.andExpect(jsonPath("$.data.howTo").value("MONEY"))
			.andExpect(jsonPath("$.data.isAutoAllocate").value(true))
			.andExpect(jsonPath("$.data.allocateAmount").value(BigDecimal.valueOf(2000)))
			.andExpect(jsonPath("$.data.goalAmount").value(10000))
			.andExpect(jsonPath("$.data.memo").value("Updated Memo"))
			.andExpect(jsonPath("$.data.isRecommended").value(true));

	}

	@Test
	@WithMockCustomUser
	void updateBucketStatus() throws Exception {
		Long bucketId = 1L;
		BucketRequestDto.UpdateBucketStatus updateStatus = BucketRequestDto.UpdateBucketStatus.builder()
			.status(BucketStatus.DONE)
			.build();

		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(bucketId)
			.status(BucketStatus.DONE)
			.build();

		// mock behavior
		when(bucketService.updateBucketStatus(any(User.class), eq(bucketId),
			any(BucketRequestDto.UpdateBucketStatus.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(patch("/buckets/{bucketId}", bucketId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateStatus)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(bucketId))
			.andExpect(jsonPath("$.data.status").value("DONE"));
	}

	@Test
	@WithMockCustomUser
	void deleteBucket() throws Exception {
		Long bucketId = 1L;
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		mockMvc.perform(MockMvcRequestBuilders.delete("/buckets/{bucketId}", bucketId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());
		verify(bucketService).deleteBucket(bucketId, customUserDetails.getUser());

	}

	@Test
	@WithMockCustomUser
	void getAllBuckets() throws Exception {

		// given
		List<BucketResponseDto.BucketInfo> responseDtos = List.of(
			BucketResponseDto.BucketInfo.builder()
				.id(1L)
				.title("Test Bucket 1")
				.tagType(BucketTagType.GO)
				.dueDate(LocalDateTime.now())
				.howTo(BucketHowTo.MONEY)
				.isAutoAllocate(true)
				.allocateAmount(BigDecimal.valueOf(1000))
				.goalAmount(BigDecimal.valueOf(5000))
				.isRecommended(false)
				.memo("Test Memo 1")
				.build(),
			BucketResponseDto.BucketInfo.builder()
				.id(2L)
				.title("Test Bucket 2")
				.tagType(BucketTagType.LEARN)
				.dueDate(LocalDateTime.now())
				.howTo(BucketHowTo.MONEY)
				.isAutoAllocate(false)
				.allocateAmount(BigDecimal.valueOf(2000))
				.goalAmount(BigDecimal.valueOf(8000))
				.isRecommended(true)
				.memo("Test Memo 2")
				.build()
		);

		// mock behavior
		when(bucketService.getAllBuckets(any(User.class))).thenReturn(responseDtos);

		// when & then
		mockMvc.perform(get("/buckets")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].id").value(1L))
			.andExpect(jsonPath("$.data[0].title").value("Test Bucket 1"))
			.andExpect(jsonPath("$.data[1].id").value(2L))
			.andExpect(jsonPath("$.data[1].title").value("Test Bucket 2"));
	}

	@Test
	@WithMockCustomUser
	void getBucketById() throws Exception {

		// given
		BucketResponseDto.BucketInfo responseDto = BucketResponseDto.BucketInfo.builder()
			.id(1L)
			.title("Test Bucket")
			.tagType(BucketTagType.GO)
			.dueDate(LocalDateTime.now())
			.howTo(BucketHowTo.MONEY)
			.isAutoAllocate(true)
			.allocateAmount(BigDecimal.valueOf(1000))
			.goalAmount(BigDecimal.valueOf(5000))
			.isRecommended(false)
			.memo("Test Memo")
			.build();

		// mock behavior
		when(bucketService.getBucketById(anyLong())).thenReturn(responseDto);

		// when & then
		mockMvc.perform(get("/buckets/{bucketId}", 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.title").value("Test Bucket"))
			.andExpect(jsonPath("$.data.tagType").value("GO"))
			.andExpect(jsonPath("$.data.howTo").value("MONEY"))
			.andExpect(jsonPath("$.data.isAutoAllocate").value(true))
			.andExpect(jsonPath("$.data.allocateAmount").value(1000))
			.andExpect(jsonPath("$.data.goalAmount").value(5000))
			.andExpect(jsonPath("$.data.memo").value("Test Memo"));
	}

	@Test
	@WithMockCustomUser
	void getRecommendedBuckets() throws Exception {

		// given
		List<RecommendationResponseDto.RecommendedBucketInfo.Recommendation> recommendations1 = List.of(
			RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
				.id(1L)
				.title("Recommended Bucket 1")
				.followers(100L)
				.build()
		);
		List<RecommendationResponseDto.RecommendedBucketInfo.Recommendation> recommendations2 = List.of(
			RecommendationResponseDto.RecommendedBucketInfo.Recommendation.builder()
				.id(2L)
				.title("Recommended Bucket 2")
				.followers(200L)
				.build()
		);

		List<RecommendationResponseDto.RecommendedBucketInfo> responseDtos = List.of(
			RecommendationResponseDto.RecommendedBucketInfo.builder()
				.tagType(BucketTagType.GO)
				.recommendations(recommendations1)
				.build(),
			RecommendationResponseDto.RecommendedBucketInfo.builder()
				.tagType(BucketTagType.DO)
				.recommendations(recommendations2)
				.build()
		);

		// mock behavior
		when(bucketService.getRecommendedBuckets(any(BucketTagType.class))).thenReturn(responseDtos);

		// when & then
		mockMvc.perform(get("/buckets/recommendation")
				.param("tagType", "GO")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].tagType").value("GO"))
			.andExpect(jsonPath("$.data[0].recommendations[0].id").value(1L))
			.andExpect(jsonPath("$.data[0].recommendations[0].title").value("Recommended Bucket 1"))
			.andExpect(jsonPath("$.data[0].recommendations[0].followers").value(100L))
			.andExpect(jsonPath("$.data[1].tagType").value("DO"))
			.andExpect(jsonPath("$.data[1].recommendations[0].id").value(2L))
			.andExpect(jsonPath("$.data[1].recommendations[0].title").value("Recommended Bucket 2"))
			.andExpect(jsonPath("$.data[1].recommendations[0].followers").value(200L));
	}

}