package com.hana4.ggumtle.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;
import com.hana4.ggumtle.security.provider.JwtProvider;
import com.hana4.ggumtle.vo.RefreshToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	public User getUserByTel(String tel) {
		return userRepository.getUserByTel(tel)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 전화번호의 사용자를 찾을 수 없습니다."));
	}

	public UserResponseDto.UserInfo register(UserRequestDto.Register userRequestDto) {

		if (userRepository.existsUserByTel(userRequestDto.getTel())){
			throw new CustomException(ErrorCode.ALREADY_EXISTS, "해당 전화번호를 사용하는 유저가 이미 존재합니다.");
		}

		// 패스워드 해싱 및 새로운 DTO 생성
		String hashedPassword = passwordEncoder.encode(userRequestDto.getPassword());
		UserRequestDto.Register updatedDto = userRequestDto.toBuilder()
			.password(hashedPassword) // 변경된 비밀번호만 설정
			.build();

		User user = userRepository.save(updatedDto.toEntity());
		return UserResponseDto.UserInfo.from(user);
	}

	public UserResponseDto.Login login(UserRequestDto.Login userRequestDto) {
		// 사용자 정보 조회
		User userInfo = this.getUserByTel(userRequestDto.getTel());

		// password 일치 여부 체크
		if(!passwordEncoder.matches(userRequestDto.getPassword(), userInfo.getPassword()))
			throw new CustomException(ErrorCode.NOT_CORRECT);

		// jwt 토큰 생성
		String accessToken = jwtProvider.generateAccessToken(userInfo.getId());

		// 기존에 가지고 있는 사용자의 refresh token 제거
		RefreshToken.removeUserRefreshToken(userInfo.getId());

		// refresh token 생성 후 저장
		String refreshToken = jwtProvider.generateRefreshToken(userInfo.getId());
		RefreshToken.putRefreshToken(refreshToken, userInfo.getId());

		return UserResponseDto.Login.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public UserResponseDto.Refresh refresh(UserRequestDto.Refresh userRequestDto) {
		// refresh token 유효성 검증
		checkRefreshToken(userRequestDto.getRefreshToken());

		// refresh token id 조회
		var id = RefreshToken.getRefreshToken(userRequestDto.getRefreshToken());

		// 새로운 access token 생성
		String newAccessToken = jwtProvider.generateAccessToken(id);

		// 기존에 가지고 있는 사용자의 refresh token 제거
		RefreshToken.removeUserRefreshToken(id);

		// 새로운 refresh token 생성 후 저장
		String newRefreshToken = jwtProvider.generateRefreshToken(id);
		RefreshToken.putRefreshToken(newRefreshToken, id);

		return UserResponseDto.Refresh.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

	/**
	 * refresh token 검증
	 */
	private void checkRefreshToken(final String refreshToken) {
		if(Boolean.FALSE.equals(jwtProvider.validateToken(refreshToken)))
			throw new CustomException(ErrorCode.TOKEN_INVALID);
	}
}
