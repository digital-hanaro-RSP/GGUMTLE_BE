package com.hana4.ggumtle.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

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
import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.service.AdvertisementService;

@WebMvcTest(
	controllers = AdvertisementController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	}
)
@Import(TestSecurityConfig.class)
public class AdvertisementControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	AdvertisementService advertisementService;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.build();
	}

	@Test
	@WithMockCustomUser
	public void testGetMainAd() throws Exception {
		AdvertisementResponseDto.MainAd mockAd1 = AdvertisementResponseDto.MainAd.builder()
			.id(1L)
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("미래에셋TIGER200중공업증권상장지수투자신탁(주식)")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("5.23%")
			.link("https://example1.com")
			.build();

		AdvertisementResponseDto.MainAd mockAd2 = AdvertisementResponseDto.MainAd.builder()
			.id(2L)
			.productType(AdvertisementProductType.PENSION)
			.productName("삼성글로벌메타버스증권자투자신탁UH(주식)Cpe(퇴직연금)")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("51.93%")
			.link("https://example2.com")
			.build();

		AdvertisementResponseDto.MainAdList mockAdList = AdvertisementResponseDto.MainAdList.builder()
			.mainAds(Arrays.asList(mockAd1, mockAd2))
			.build();

		when(advertisementService.getMainAd(any(User.class))).thenReturn(mockAdList);

		mockMvc.perform(get("/ads/main")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.mainAds").isArray())
			.andExpect(jsonPath("$.data.mainAds[0].id").value(1L))
			.andExpect(jsonPath("$.data.mainAds[0].productType").value("INVESTMENT"))
			.andExpect(jsonPath("$.data.mainAds[0].productName").value("미래에셋TIGER200중공업증권상장지수투자신탁(주식)"))
			.andExpect(jsonPath("$.data.mainAds[0].locationType").value("MAIN"))
			.andExpect(jsonPath("$.data.mainAds[0].riskRating").value("보통위험"))
			.andExpect(jsonPath("$.data.mainAds[0].yield").value("5.23%"))
			.andExpect(jsonPath("$.data.mainAds[0].link").value("https://example1.com"))
			.andExpect(jsonPath("$.data.mainAds[1].id").value(2L))
			.andExpect(jsonPath("$.data.mainAds[1].productType").value("PENSION"))
			.andExpect(jsonPath("$.data.mainAds[1].productName").value("삼성글로벌메타버스증권자투자신탁UH(주식)Cpe(퇴직연금)"))
			.andExpect(jsonPath("$.data.mainAds[1].locationType").value("MAIN"))
			.andExpect(jsonPath("$.data.mainAds[1].riskRating").value("높은위험"))
			.andExpect(jsonPath("$.data.mainAds[1].yield").value("51.93%"))
			.andExpect(jsonPath("$.data.mainAds[1].link").value("https://example2.com"));
	}
}
