package com.hana4.ggumtle.service;

import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;

public interface UserService {
	void exists(String tel);
	UserResponseDto.UserInfo register(UserRequestDto.Register userRequestDto);
}
