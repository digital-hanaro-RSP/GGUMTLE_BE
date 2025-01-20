package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.service.UserService;

@WebMvcTest(
	controllers = UserController.class,
	// excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	}
)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	UserService userService;

	// @Autowired
	// List<UserDetailsService> services; // or Map<String, UserDetailsService> servicesMap;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	public void testRegisterUser() throws Exception {
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

		// 응답 DTO
		UserResponseDto.UserInfo response = UserResponseDto.UserInfo.from(user);

		when(userService.register(Mockito.any(UserRequestDto.Register.class)))
			.thenReturn(response);

		mockMvc.perform(post("/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value("27295730-41ce-4df8-9864-4da1fa3c6caa"))
			.andExpect(jsonPath("$.data.tel").value("01012341234"))
			.andExpect(jsonPath("$.data.name").value("문서아"))
			.andExpect(jsonPath("$.data.permission").value(0))
			.andExpect(jsonPath("$.data.birthDate").value("2000-01-01T00:00:00"))
			.andExpect(jsonPath("$.data.gender").value("f"))
			.andExpect(jsonPath("$.data.role").value("USER"))
			.andExpect(jsonPath("$.data.profileImageUrl").doesNotExist())
			.andExpect(jsonPath("$.data.nickname").value("익명의고라니"));
	}

	@Test
	public void testLogin() throws Exception {
		// 요청 DTO
		UserRequestDto.Login loginRequest = UserRequestDto.Login.builder()
			.tel("test@example.com")
			.password("password123")
			.build();

		// 응답 DTO
		UserResponseDto.TokensWithPermission loginResponse = UserResponseDto.TokensWithPermission.builder()
			.accessToken("testAccessToken")
			.refreshToken("testRefreshToken")
			.build();

		when(userService.login(any(UserRequestDto.Login.class))).thenReturn(loginResponse);

		mockMvc.perform(post("/auth/tokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.accessToken").value("testAccessToken"))
			.andExpect(cookie().value("refreshToken", "testRefreshToken"))
			.andExpect(cookie().httpOnly("refreshToken", true))
			.andExpect(cookie().secure("refreshToken", true));
	}

	@Test
	public void testLoginWithInvalidCredentials() throws Exception {
		// Unauthorized - 잘못된 아이디/비밀번호
		UserRequestDto.Login invalidLoginRequest = new UserRequestDto.Login("01012341234", "invalidPassword");

		when(userService.login(any(UserRequestDto.Login.class)))
			.thenThrow(new CustomException(ErrorCode.NOT_CORRECT));

		mockMvc.perform(post("/auth/tokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidLoginRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(401))
			.andExpect(jsonPath("$.error").value("Unauthorized"))
			.andExpect(jsonPath("$.message").value("아이디 혹은 비밀번호가 일치하지 않습니다."));
	}

	@Test
	public void testUserNotFound() throws Exception {
		// Not Found - 전화번호로 유저를 찾을 수 없는 경우
		UserRequestDto.Login userNotFoundRequest = new UserRequestDto.Login("0101111", "password");

		when(userService.login(any(UserRequestDto.Login.class)))
			.thenThrow(new CustomException(ErrorCode.NOT_FOUND, "해당 전화번호를 사용하는 유저를 찾을 수 없습니다. : " + "0101111"));

		mockMvc.perform(post("/auth/tokens")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userNotFoundRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(404))
			.andExpect(jsonPath("$.error").value("Not Found"))
			.andExpect(jsonPath("$.message").value("해당 전화번호를 사용하는 유저를 찾을 수 없습니다. : 0101111"));
	}

	@Test
	public void testTokenRefresh() throws Exception {
		// 요청 DTO
		UserRequestDto.Refresh refreshRequest = new UserRequestDto.Refresh("oldRefreshToken");

		// 응답 DTO
		UserResponseDto.Tokens refreshResponse = UserResponseDto.Tokens.builder()
			.accessToken("newAccessToken")
			.refreshToken("newRefreshToken")
			.build();

		when(userService.refresh(any(UserRequestDto.Refresh.class))).thenReturn(refreshResponse);

		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(refreshRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
			.andExpect(cookie().value("refreshToken", "newRefreshToken"))
			.andExpect(cookie().httpOnly("refreshToken", true))
			.andExpect(cookie().secure("refreshToken", true));

		verify(userService, times(1)).refresh(any(UserRequestDto.Refresh.class));
	}

	@Test
	public void testTokenRefreshWithInvalidToken() throws Exception {
		UserRequestDto.Refresh invalidRefreshRequest = new UserRequestDto.Refresh("invalidRefreshToken");

		when(userService.refresh(any(UserRequestDto.Refresh.class)))
			.thenThrow(new CustomException(ErrorCode.TOKEN_INVALID));

		mockMvc.perform(post("/auth/refresh")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRefreshRequest)))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(401))
			.andExpect(jsonPath("$.error").value("Unauthorized"))
			.andExpect(jsonPath("$.message").value("Refresh Token이 만료되었거나 정상적인 Token이 아닙니다."));
	}

	@Test
	@WithMockCustomUser
	// @WithUserDetails(value = "mockUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	public void testUpdatePermission() throws Exception {
		// response dto 생성용
		User mockUser = User.builder()
			.id("27295730-41ce-4df8-9864-4da1fa3c6caa")
			.tel("01098765432")
			.name("테스터")
			.permission((short)0)
			.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
			.gender("m")
			.role(UserRole.USER)
			.nickname("테스트계정")
			.password("mockPassword")
			.build();

		UserResponseDto.UserInfo updatedUserInfo = UserResponseDto.UserInfo.builder()
			.id(mockUser.getId())
			.tel(mockUser.getTel())
			.name(mockUser.getName())
			.permission((short)1)  // permission update
			.birthDate(mockUser.getBirthDate())
			.gender(mockUser.getGender())
			.role(mockUser.getRole())
			.nickname(mockUser.getNickname())
			.build();

		when(userService.updatePermission(any(User.class))).thenReturn(updatedUserInfo);

		mockMvc.perform(
				patch("/mydata/permission")
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(mockUser.getId()))
			.andExpect(jsonPath("$.data.tel").value(mockUser.getTel()))
			.andExpect(jsonPath("$.data.name").value(mockUser.getName()))
			.andExpect(jsonPath("$.data.permission").value(1)) // updated permission
			.andExpect(jsonPath("$.data.birthDate").value("1990-05-20T00:00:00"))
			.andExpect(jsonPath("$.data.gender").value("m"))
			.andExpect(jsonPath("$.data.role").value("USER"))
			.andExpect(jsonPath("$.data.nickname").value("테스트계정"));

		verify(userService, times(1)).updatePermission(any(User.class));
	}

	@Test
	public void testSendVerificationCode() throws Exception {
		UserRequestDto.VerificationCode request = new UserRequestDto.VerificationCode("01012341234");

		doNothing().when(userService).sendVerificationCode(anyString());

		mockMvc.perform(post("/verification-code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist());

		verify(userService, times(1)).sendVerificationCode("01012341234");
	}

	@Test
	public void testValidateVerificationCode() throws Exception {
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");

		doNothing().when(userService).validateVerificationCode(any(UserRequestDto.Validation.class));

		mockMvc.perform(post("/verification-code/validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist());

		verify(userService, times(1)).validateVerificationCode(any(UserRequestDto.Validation.class));
	}

	@Test
	public void testSendVerificationCodeWithInvalidRequest() throws Exception {
		UserRequestDto.VerificationCode request = new UserRequestDto.VerificationCode("");

		mockMvc.perform(post("/verification-code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());

		verify(userService, never()).sendVerificationCode(anyString());
	}

	@Test
	public void testValidateVerificationCodeWithInvalidRequest() throws Exception {
		UserRequestDto.Validation request = new UserRequestDto.Validation("", "");

		mockMvc.perform(post("/verification-code/validation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());

		verify(userService, never()).validateVerificationCode(any(UserRequestDto.Validation.class));
	}

}
