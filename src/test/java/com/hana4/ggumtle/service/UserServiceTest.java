package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.TelCodeValidationRepository;
import com.hana4.ggumtle.repository.UserRepository;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.vo.RefreshToken;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private MyDataService myDataService;

	@InjectMocks
	private UserService userService;

	@Mock
	private TelCodeValidationRepository telCodeValidationRepository;

	@Mock
	private SmsService smsService;

	// @BeforeEach
	// void setUp() {
	// 	MockitoAnnotations.openMocks(this);
	// }

	@Test
	void testUpdatePermission_Success() {
		// given
		User mockUser = new User();
		mockUser.setPermission((short)0); // initial permission

		// when
		UserResponseDto.UserInfo result = userService.addMyDataPermission(mockUser);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPermission()).isEqualTo((short)1); // check if permission is updated
	}

	@Test
	void testGetUserByTel_UserExists() {
		// given
		String tel = "01012341234";
		User mockUser = new User();
		when(userRepository.findUserByTel(tel)).thenReturn(Optional.of(mockUser));

		// when
		User result = userService.getUserByTel(tel);

		// then
		assertThat(result).isEqualTo(mockUser);
		verify(userRepository).findUserByTel(tel);
	}

	@Test
	void testGetUserByTel_UserNotFound() {
		// given
		String tel = "01012341234";
		when(userRepository.findUserByTel(tel)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userService.getUserByTel(tel))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("해당 전화번호를 사용하는 유저를 찾을 수 없습니다. : " + tel);
		verify(userRepository).findUserByTel(tel);
	}

	@Test
	void testRegister_UserAlreadyExists() {
		// given
		UserRequestDto.Register registerDto = UserRequestDto.Register.builder()
			.name("문서아")
			.tel("01012341234")
			.password("password")
			.birthDate("2000-01-01")
			.gender("f")
			.nickname("익명의고라니")
			.build();
		when(userRepository.existsUserByTel(registerDto.getTel())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> userService.register(registerDto))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("해당 전화번호를 사용하는 유저가 이미 존재합니다.");
		verify(userRepository).existsUserByTel(registerDto.getTel());
	}

	@Test
	void testRegister_Success() {
		// given
		// UserRequestDto.Register registerDto = new UserRequestDto.Register("01012341234", "password", "name");
		UserRequestDto.Register registerDto = UserRequestDto.Register.builder()
			.name("문서아")
			.tel("01012341234")
			.password("password")
			.birthDate("2000-01-01")
			.gender("f")
			.nickname("익명의고라니")
			.build();
		when(userRepository.existsUserByTel(registerDto.getTel())).thenReturn(false);
		when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("hashedPassword");
		when(userRepository.save(any(User.class))).thenReturn(new User());

		// when
		UserResponseDto.UserInfo result = userService.register(registerDto);

		// then
		assertThat(result).isNotNull();
		verify(passwordEncoder).encode(registerDto.getPassword());
		verify(userRepository).save(any(User.class));
		verify(myDataService).createRandomMyData(any(User.class));
	}

	@Test
	void testLogin_Success() {
		try (MockedStatic<RefreshToken> mockedStatic = mockStatic(RefreshToken.class)) {
			// Mock the static methods of RefreshToken
			mockedStatic.when(() -> RefreshToken.getRefreshToken(any()))
				.thenReturn("userId");
			mockedStatic.when(() -> RefreshToken.removeUserRefreshToken(any()))
				.thenAnswer(invocation -> null);
			mockedStatic.when(() -> RefreshToken.putRefreshToken(any(), any()))
				.thenAnswer(invocation -> null);

			// Given
			String tel = "01012341234";
			String password = "password";
			UserRequestDto.Login loginDto = new UserRequestDto.Login(tel, password);
			User mockUser = new User();
			mockUser.setPassword("hashedPassword");

			when(userRepository.findUserByTel(tel)).thenReturn(Optional.of(mockUser));
			when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);
			when(jwtProvider.generateAccessToken(any())).thenReturn("accessToken");
			when(jwtProvider.generateRefreshToken(any())).thenReturn("refreshToken");

			// When
			UserResponseDto.TokensWithPermission result = userService.login(loginDto);

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getAccessToken()).isEqualTo("accessToken");
			assertThat(result.getRefreshToken()).isEqualTo("refreshToken");

			// Verify that static methods were called
			mockedStatic.verify(() -> RefreshToken.removeUserRefreshToken(any()), atLeastOnce());
			mockedStatic.verify(() -> RefreshToken.putRefreshToken(any(), any()), atLeastOnce());
		}
	}

	@Test
	void testLogin_InvalidPassword() {
		// given
		String tel = "01012341234";
		String password = "password";
		UserRequestDto.Login loginDto = new UserRequestDto.Login(tel, password);
		User mockUser = new User();
		mockUser.setPassword("hashedPassword");
		when(userRepository.findUserByTel(tel)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> userService.login(loginDto))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_CORRECT.getMessage());
	}

	@Test
	void testRefresh_Success() {
		// refreshtoken static 메서드 모킹
		try (MockedStatic<RefreshToken> mockedStatic = mockStatic(RefreshToken.class)) {
			mockedStatic.when(() -> RefreshToken.getRefreshToken(any()))
				.thenReturn("userId");
			mockedStatic.when(() -> RefreshToken.removeUserRefreshToken(any()))
				.thenAnswer(invocation -> null);
			mockedStatic.when(() -> RefreshToken.putRefreshToken(any(), any()))
				.thenAnswer(invocation -> null);

			String oldRefreshToken = "oldRefreshToken";
			String newRefreshToken = "newRefreshToken";
			String newAccessToken = "newAccessToken";
			UserRequestDto.Refresh refreshDto = new UserRequestDto.Refresh(oldRefreshToken);

			when(jwtProvider.validateToken(oldRefreshToken)).thenReturn(true);
			when(jwtProvider.generateAccessToken(any())).thenReturn(newAccessToken);
			when(jwtProvider.generateRefreshToken(any())).thenReturn(newRefreshToken);

			UserResponseDto.Tokens result = userService.refresh(refreshDto);

			assertThat(result).isNotNull();
			assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
			assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);

			mockedStatic.verify(() -> RefreshToken.getRefreshToken(oldRefreshToken), atLeastOnce());
			mockedStatic.verify(() -> RefreshToken.removeUserRefreshToken("userId"), atLeastOnce());
			mockedStatic.verify(() -> RefreshToken.putRefreshToken(newRefreshToken, "userId"), atLeastOnce());
		}
	}

	@Test
	void testCheckRefreshToken_InvalidToken() {
		// given
		String invalidToken = "invalidToken";
		when(jwtProvider.validateToken(invalidToken)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> userService.refresh(new UserRequestDto.Refresh(invalidToken)))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_INVALID);
	}

	@Test
	void checkUpdatePermission() {
		// given
		User mockUser = new User();
		mockUser.setPermission((short)1); // initial permission
		short newPermission = 3;
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// when
		User updatedUser = userService.addSurveyPermission(mockUser);

		// then
		assertThat(updatedUser.getPermission()).isEqualTo(newPermission);
		verify(userRepository).save(mockUser);
	}

	@Test
	void testSendVerificationCode_Success() {
		// Given
		String userTel = "01012341234";
		when(telCodeValidationRepository.hasKey(userTel)).thenReturn(false);
		when(telCodeValidationRepository.incrementDailyRequestCount(userTel)).thenReturn(true);

		// When
		assertDoesNotThrow(() -> userService.sendVerificationCode(userTel));

		// Then
		verify(smsService).sendOne(eq(userTel), anyString());
		verify(telCodeValidationRepository).createSmsCertification(eq(userTel), anyString());
	}

	@Test
	void testSendVerificationCode_AlreadySent() {
		// Given
		String userTel = "01012341234";
		when(telCodeValidationRepository.hasKey(userTel)).thenReturn(true);

		// When & Then
		assertThatThrownBy(() -> userService.sendVerificationCode(userTel))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.SMS_ALREADY_SENT);
	}

	@Test
	void testSendVerificationCode_DailyLimitExceeded() {
		// Given
		String userTel = "01012341234";
		when(telCodeValidationRepository.hasKey(userTel)).thenReturn(false);
		when(telCodeValidationRepository.incrementDailyRequestCount(userTel)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> userService.sendVerificationCode(userTel))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.DAILY_LIMIT_EXCEEDED);
	}

	@Test
	void testSendVerificationCode_RedisConnectionFailure() {
		// Given
		String userTel = "01012341234";
		when(telCodeValidationRepository.hasKey(userTel)).thenReturn(false);
		when(telCodeValidationRepository.incrementDailyRequestCount(userTel)).thenReturn(true);
		doThrow(new RedisConnectionFailureException("Redis connection failed"))
			.when(telCodeValidationRepository).createSmsCertification(eq(userTel), anyString());

		// When & Then
		assertThatThrownBy(() -> userService.sendVerificationCode(userTel))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.REDIS_CONNECTION_FAILURE);
	}

	@Test
	void testSendVerificationCode_SmsFailure() {
		// Arrange
		String userTel = "01012341234";
		when(telCodeValidationRepository.hasKey(userTel)).thenReturn(false);
		when(telCodeValidationRepository.incrementDailyRequestCount(userTel)).thenReturn(true);
		doThrow(new RuntimeException("SMS sending failed")).when(smsService).sendOne(eq(userTel), anyString());

		// Act & Assert
		assertThatThrownBy(() -> userService.sendVerificationCode(userTel))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.SMS_FAILURE);
	}

	@Test
	void testValidateVerificationCode_Success() {
		// Given
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");
		when(telCodeValidationRepository.getSmsCertification(request.getTel())).thenReturn("123456");

		// When
		assertDoesNotThrow(() -> userService.validateVerificationCode(request));

		// Then
		verify(telCodeValidationRepository).removeSmsCertification(request.getTel());
	}

	@Test
	void testValidateVerificationCode_CodeNotFound() {
		// Given
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");
		when(telCodeValidationRepository.getSmsCertification(request.getTel())).thenReturn(null);

		// When & Then
		assertThatThrownBy(() -> userService.validateVerificationCode(request))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_FOUND);
	}

	@Test
	void testValidateVerificationCode_CodeMismatch() {
		// Given
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");
		when(telCodeValidationRepository.getSmsCertification(request.getTel())).thenReturn("654321");

		// When & Then
		assertThatThrownBy(() -> userService.validateVerificationCode(request))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.SMS_VALIDATION_FAILURE);
	}

	@Test
	void testValidateVerificationCode_RedisConnectionFailure() {
		// Given
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");
		when(telCodeValidationRepository.getSmsCertification(request.getTel()))
			.thenThrow(new RedisConnectionFailureException("Redis connection failed"));

		// When & Then
		assertThatThrownBy(() -> userService.validateVerificationCode(request))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.REDIS_CONNECTION_FAILURE);
	}

	@Test
	void testValidateVerificationCode_UnexpectedError() {
		// Given
		UserRequestDto.Validation request = new UserRequestDto.Validation("01012341234", "123456");
		when(telCodeValidationRepository.getSmsCertification(request.getTel()))
			.thenThrow(new RuntimeException("Unexpected error"));

		// When & Then
		assertThatThrownBy(() -> userService.validateVerificationCode(request))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.SMS_FAILURE);
	}

	@Test
	void testGetUserInfo_UserExists() {
		// given
		String userId = "user123";
		User mockUser = new User();
		mockUser.setId(userId);
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// when
		UserResponseDto.UserInfo result = userService.getUserInfo(userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(userId);
		verify(userRepository).findById(userId);
	}

	@Test
	void testGetUserInfo_UserNotFound() {
		// given
		String userId = "user123";
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userService.getUserInfo(userId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("해당 유저를 찾을 수 없습니다");
		verify(userRepository).findById(userId);
	}

	@Test
	void testUpdateUserInfo_Success() {
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder()
			.password("newPassword")
			.profileImageUrl("http://example.com/image.jpg")
			.nickname("newNickname")
			.build();

		User mockUser = new User();
		mockUser.setId(userId);
		mockUser.setPassword("oldPassword");

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.encode(updateUserRequest.getPassword())).thenReturn("hashedNewPassword");

		// when
		UserResponseDto.UserInfo result = userService.updateUserInfo(userId, updateUserRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPassword()).isEqualTo("hashedNewPassword");
		assertThat(mockUser.getProfileImageUrl()).isEqualTo("http://example.com/image.jpg");
		assertThat(mockUser.getNickname()).isEqualTo("newNickname");

		verify(userRepository).save(mockUser);
	}

	@Test
	void testUpdateUserInfo_UserNotFound() {
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder()
			.password("newPassword")
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userService.updateUserInfo(userId, updateUserRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("유저를 찾을 수 없습니다.");

		verify(userRepository).findById(userId);
	}

	@Test
	void testUpdateUserInfo_OnlyPassword() {
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder()
			.password("newPassword")
			.build();

		User mockUser = new User();
		mockUser.setId(userId);
		mockUser.setPassword("oldPassword");
		mockUser.setProfileImageUrl("oldImageUrl");
		mockUser.setNickname("oldNickname");

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");

		// when
		UserResponseDto.UserInfo result = userService.updateUserInfo(userId, updateUserRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPassword()).isEqualTo("hashedNewPassword");
		assertThat(mockUser.getProfileImageUrl()).isEqualTo("oldImageUrl");
		assertThat(mockUser.getNickname()).isEqualTo("oldNickname");

		verify(userRepository).save(mockUser);
		verify(passwordEncoder).encode("newPassword");
	}

	@Test
	void testUpdateUserInfo_OnlyProfileImageUrl() {
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder()
			.profileImageUrl("newImageUrl")
			.build();

		User mockUser = new User();
		mockUser.setId(userId);
		mockUser.setPassword("oldPassword");
		mockUser.setProfileImageUrl("oldImageUrl");
		mockUser.setNickname("oldNickname");

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// when
		UserResponseDto.UserInfo result = userService.updateUserInfo(userId, updateUserRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPassword()).isEqualTo("oldPassword");
		assertThat(mockUser.getProfileImageUrl()).isEqualTo("newImageUrl");
		assertThat(mockUser.getNickname()).isEqualTo("oldNickname");

		verify(userRepository).save(mockUser);
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void testUpdateUserInfo_OnlyNickname() {
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder()
			.nickname("newNickname")
			.build();

		User mockUser = new User();
		mockUser.setId(userId);
		mockUser.setPassword("oldPassword");
		mockUser.setProfileImageUrl("oldImageUrl");
		mockUser.setNickname("oldNickname");

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// when
		UserResponseDto.UserInfo result = userService.updateUserInfo(userId, updateUserRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPassword()).isEqualTo("oldPassword");
		assertThat(mockUser.getProfileImageUrl()).isEqualTo("oldImageUrl");
		assertThat(mockUser.getNickname()).isEqualTo("newNickname");

		verify(userRepository).save(mockUser);
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void testUpdateUserInfo_AllFieldsNull() {
		// 이 부분은 controller 단에서 사전에 차단됨
		// given
		String userId = "user123";
		UserRequestDto.UpdateUser updateUserRequest = UserRequestDto.UpdateUser.builder().build();

		User mockUser = new User();
		mockUser.setId(userId);
		mockUser.setPassword("oldPassword");
		mockUser.setProfileImageUrl("oldImageUrl");
		mockUser.setNickname("oldNickname");

		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// when
		UserResponseDto.UserInfo result = userService.updateUserInfo(userId, updateUserRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(mockUser.getPassword()).isEqualTo("oldPassword");
		assertThat(mockUser.getProfileImageUrl()).isEqualTo("oldImageUrl");
		assertThat(mockUser.getNickname()).isEqualTo("oldNickname");

		verify(userRepository).save(mockUser);
		verify(passwordEncoder, never()).encode(any());
	}

}
