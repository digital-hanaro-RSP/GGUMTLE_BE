package com.hana4.ggumtle.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;
import com.hana4.ggumtle.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// User가 존재하지 않으면 예외를 던지고, 해당 아이디를 로그로 남깁니다.
		User user = userRepository.findById(username)
			.orElseThrow(() -> {
				log.warn("해당 유저가 존재하지 않습니다.(만료된 토큰) 아이디 : {}", username);
				return new UsernameNotFoundException("해당 유저가 존재하지 않습니다. 아이디 : " + username);
			});
		return new CustomUserDetails(user);
	}
}
