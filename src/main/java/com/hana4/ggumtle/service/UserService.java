package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;

	public UserResponseDto.UserInfo register(UserRequestDto.Register userRequestDto) {

		if (userRepository.existsUserByTel(userRequestDto.getTel())){
			throw new CustomException(ErrorCode.ALREADY_EXISTS, "해당 전화번호를 사용하는 유저가 이미 존재합니다.");
		}

		User user = userRepository.save(userRequestDto.toEntity());
		return UserResponseDto.UserInfo.from(user);
	}
}
