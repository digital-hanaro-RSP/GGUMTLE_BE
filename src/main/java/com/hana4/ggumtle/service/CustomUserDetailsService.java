package com.hana4.ggumtle.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;
import com.hana4.ggumtle.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CustomUserDetailsService implements UserDetailsService {
// 	private final UserRepository userRepository;
//
// 	@Override
// 	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
// 		User user = userRepository.findById(username)
// 			.orElseThrow(() -> new UsernameNotFoundException("User not found with Id: " + username));
// 		return new org.springframework.security.core.userdetails.User(
// 			user.getTel(), user.getPassword(),
// 			Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
// 		);
// 	}
// }

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findById(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));
		return new CustomUserDetails(user);
	}
}
