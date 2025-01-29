package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountRequestDto;
import com.hana4.ggumtle.dto.dreamAccount.DreamAccountResponseDto;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.service.BucketService;
import com.hana4.ggumtle.service.DreamAccountService;

@WebMvcTest(controllers = DreamAccountController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	})
@Import(TestSecurityConfig.class)
class DreamAccountControllerTest {
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
	void getDreamAccount() throws Exception {

		// given
		LocalDateTime fixedDateTime = LocalDateTime.now();

		DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
			.id(1L)
			.userId("user123")
			.user(User.builder()
				.id("user123")
				.name("홍길동")
				.build())
			.balance(new BigDecimal("1000.01"))
			.total(new BigDecimal("2000.00"))
			.totalSafeBox(new BigDecimal("500.01"))
			.createdAt(fixedDateTime)
			.updatedAt(fixedDateTime)
			.build();

		// Mock 서비스 설정
		when(dreamAccountService.getDreamAccountByUserId(anyString())).thenReturn(responseDto);

		// when & then
		mockMvc.perform(get("/dreamAccount")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.userId").value("user123"))
			.andExpect(jsonPath("$.data.balance").value("1000.01"))
			.andExpect(jsonPath("$.data.total").value("2000.0"))
			.andExpect(jsonPath("$.data.totalSafeBox").value("500.01"));
	}

	@Test
	@WithMockCustomUser
	void addAmount() throws Exception {
		Long dreamAccountId = 1L;
		BigDecimal subtractAmount = new BigDecimal("500.00");

		DreamAccountRequestDto.AddAmount requestDto = DreamAccountRequestDto.AddAmount.builder()
			.amount(subtractAmount)
			.build();

		DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
			.id(dreamAccountId)
			.userId("user123")
			.user(User.builder()
				.id("user123")
				.name("홍길동")
				.build())
			.balance(new BigDecimal("1000.01")) // 꿈통장에서 금액 제거 후의 잔액
			.total(new BigDecimal("1500.02"))
			.totalSafeBox(new BigDecimal("500.01"))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		// Mock 설정: subtractAmountFromDreamAccount 메서드 호출 시 Mock 반환값 지정
		when(dreamAccountService.addAmountToDreamAccount(anyLong(), any(BigDecimal.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(post("/dreamAccount/{dreamAccountId}/amounts", dreamAccountId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))) // 요청 본문으로 JSON 데이터 전송
			.andDo(print())
			.andExpect(status().isOk()) // 응답 상태 코드 검증
			.andExpect(jsonPath("$.data.id").value(dreamAccountId))
			.andExpect(jsonPath("$.data.userId").value("user123"))
			.andExpect(jsonPath("$.data.balance").value("1000.01"))
			.andExpect(jsonPath("$.data.total").value("1500.02"))
			.andExpect(jsonPath("$.data.totalSafeBox").value("500.01"))
			.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
			.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

	}

	@Test
	@WithMockCustomUser
	void subtractAmount() throws Exception {
		Long dreamAccountId = 1L;
		BigDecimal subtractAmount = new BigDecimal("500.00");

		DreamAccountRequestDto.AddAmount requestDto = DreamAccountRequestDto.AddAmount.builder()
			.amount(subtractAmount)
			.build();

		DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
			.id(dreamAccountId)
			.userId("user123")
			.user(User.builder()
				.id("user123")
				.name("홍길동")
				.build())
			.balance(new BigDecimal("1000.01")) // 꿈통장에서 금액 제거 후의 잔액
			.total(new BigDecimal("1500.02"))
			.totalSafeBox(new BigDecimal("500.01"))
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		// Mock 설정: subtractAmountFromDreamAccount 메서드 호출 시 Mock 반환값 지정
		when(dreamAccountService.subtractAmountFromDreamAccount(anyLong(), any(BigDecimal.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(delete("/dreamAccount/{dreamAccountId}/amounts", dreamAccountId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))) // 요청 본문으로 JSON 데이터 전송
			.andDo(print())
			.andExpect(status().isOk()) // 응답 상태 코드 검증
			.andExpect(jsonPath("$.data.id").value(dreamAccountId))
			.andExpect(jsonPath("$.data.userId").value("user123"))
			.andExpect(jsonPath("$.data.balance").value("1000.01"))
			.andExpect(jsonPath("$.data.total").value("1500.02"))
			.andExpect(jsonPath("$.data.totalSafeBox").value("500.01"))
			.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
			.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
	}

	@Test
	@WithMockCustomUser
	void distributeAmountToBucket() throws Exception {
		// given
		Long dreamAccountId = 1L;
		Long bucketId = 2L;
		BigDecimal distributeAmount = new BigDecimal("300.00");

		DreamAccountRequestDto.DistributeAmount requestDto = DreamAccountRequestDto.DistributeAmount.builder()
			.amount(distributeAmount)
			.build();

		DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
			.id(dreamAccountId)
			.userId("user123")
			.user(User.builder()
				.id("user123")
				.name("홍길동")
				.build())
			.balance(new BigDecimal("700.01")) // 꿈통장에서 분배 후의 잔액
			.total(new BigDecimal("2000.01"))
			.totalSafeBox(new BigDecimal("800.01")) // safebox에 추가된 금액 포함
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		// Mock 설정: distributeAmountToBucket 호출 시 반환값 지정
		when(dreamAccountService.distributeAmountToBucket(anyLong(), anyLong(), any(BigDecimal.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(
				post("/dreamAccount/{dreamAccountId}/buckets/{bucketId}/distributions", dreamAccountId, bucketId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))) // 요청 본문을 JSON으로 전송
			.andDo(print())
			.andExpect(status().isOk()) // 응답 상태 코드 검증
			.andExpect(jsonPath("$.data.id").value(dreamAccountId))
			.andExpect(jsonPath("$.data.userId").value("user123"))
			.andExpect(jsonPath("$.data.balance").value("700.01"))
			.andExpect(jsonPath("$.data.total").value("2000.01"))
			.andExpect(jsonPath("$.data.totalSafeBox").value("800.01"))
			.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
			.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
	}

	@Test
	@WithMockCustomUser
	void emptyAmount() throws Exception {
		Long dreamAccountId = 1L;
		Long bucketId = 2L;
		BigDecimal distributeAmount = new BigDecimal("300.00");

		DreamAccountRequestDto.DistributeAmount requestDto = DreamAccountRequestDto.DistributeAmount.builder()
			.amount(distributeAmount)
			.build();

		DreamAccountResponseDto.DreamAccountInfo responseDto = DreamAccountResponseDto.DreamAccountInfo.builder()
			.id(dreamAccountId)
			.userId("user123")
			.user(User.builder()
				.id("user123")
				.name("홍길동")
				.build())
			.balance(new BigDecimal("700.01")) // 꿈통장에서 분배 후의 잔액
			.total(new BigDecimal("2000.01"))
			.totalSafeBox(new BigDecimal("800.01")) // safebox에서 빠진 금액 포함
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		// Mock 설정: distributeAmountToBucket 호출 시 반환값 지정
		when(
			dreamAccountService.distributeAmountToDreamAccount(anyLong(), anyLong(), any(BigDecimal.class))).thenReturn(
			responseDto);

		// when & then
		mockMvc.perform(
				delete("/dreamAccount/{dreamAccountId}/buckets/{bucketId}/distributions", dreamAccountId, bucketId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))) // 요청 본문을 JSON으로 전송
			.andDo(print())
			.andExpect(status().isOk()) // 응답 상태 코드 검증
			.andExpect(jsonPath("$.data.id").value(dreamAccountId))
			.andExpect(jsonPath("$.data.userId").value("user123"))
			.andExpect(jsonPath("$.data.balance").value("700.01"))
			.andExpect(jsonPath("$.data.total").value("2000.01"))
			.andExpect(jsonPath("$.data.totalSafeBox").value("800.01"))
			.andExpect(jsonPath("$.data.createdAt").isNotEmpty())
			.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
	}
}