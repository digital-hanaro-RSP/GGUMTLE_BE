package com.hana4.ggumtle.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
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
		// 요청 DTO
		UserRequestDto.Register request = UserRequestDto.Register.builder()
			.name("문서아")
			.tel("01012341234")
			.password("password")
			.birthDate("2000-01-01")
			.gender("f")
			.nickname("익명의고라니")
			.build();

		// User 엔티티
		User user = request.toEntity();
		user.setId("27295730-41ce-4df8-9864-4da1fa3c6caa");
		user.setRole(UserRole.USER);
		user.setPermission((short)0);

		AdvertisementResponseDto.MainAd mockAd = AdvertisementResponseDto.MainAd.builder()
			.id(2L)
			.productType(AdvertisementProductType.PENSION)
			.productName("삼성글로벌메타버스증권자투자신탁UH(주식)Cpe(퇴직연금)")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("51.93%")
			.link("https://www.samsungfund.com/fund/product/view.do?id=K55105DK2904")
			.build();

		when(advertisementService.getMainAd(any(User.class))).thenReturn(mockAd);

		mockMvc.perform(get("/ads/main")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(2L))
			.andExpect(jsonPath("$.data.productType").value("PENSION"))
			.andExpect(jsonPath("$.data.productName").value("삼성글로벌메타버스증권자투자신탁UH(주식)Cpe(퇴직연금)"))
			.andExpect(jsonPath("$.data.locationType").value("MAIN"))
			.andExpect(jsonPath("$.data.riskRating").value("높은위험"))
			.andExpect(jsonPath("$.data.yield").value("51.93%"))
			.andExpect(
				jsonPath("$.data.link").value("https://www.samsungfund.com/fund/product/view.do?id=K55105DK2904"));
	}
}
