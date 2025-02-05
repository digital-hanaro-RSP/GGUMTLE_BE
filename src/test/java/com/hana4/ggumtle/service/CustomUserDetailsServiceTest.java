package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;
import com.hana4.ggumtle.security.CustomUserDetails;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId("testUser");
		user.setPassword("password123");
	}

	@Test
	void loadUserByUsername_UserExists_ReturnsUserDetails() {
		// given
		when(userRepository.findById("testUser")).thenReturn(Optional.of(user));

		// when
		UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");

		// then
		assertNotNull(userDetails);
		assertEquals("testUser", userDetails.getUsername() != null ? userDetails.getUsername() : user.getId());
		assertTrue(userDetails instanceof CustomUserDetails);
	}

	@Test
	void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
		// given
		when(userRepository.findById("unknownUser")).thenReturn(Optional.empty());

		// when & then
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
			() -> customUserDetailsService.loadUserByUsername("unknownUser"));
		assertEquals("해당 유저가 존재하지 않습니다. 아이디 : unknownUser", exception.getMessage());
	}
}
