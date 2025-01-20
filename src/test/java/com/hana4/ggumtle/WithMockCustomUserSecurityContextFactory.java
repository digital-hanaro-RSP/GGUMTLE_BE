package com.hana4.ggumtle;

import java.time.LocalDateTime;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.security.CustomUserDetails;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		String tel = customUser.user();
		UserRole userRole = customUser.role();

		User user = new User(
			"1", // id
			tel, // tel
			"password", // password
			"남인우", // name
			(short)1, // permission
			LocalDateTime.of(1990, 1, 1, 0, 0, 0, 0), // birthDate
			"M", // gender
			userRole, // role
			"https://example.com/profile.jpg", // profileImageUrl
			"inwoo" // nickname
		);

		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}
}
