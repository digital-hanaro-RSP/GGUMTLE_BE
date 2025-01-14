package com.hana4.ggumtle.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
	private final com.hana4.ggumtle.model.entity.user.User user;

	public CustomUserDetails(com.hana4.ggumtle.model.entity.user.User user) {
		super(user.getTel(), user.getPassword(),
			Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));
		this.user = user;
	}

	public com.hana4.ggumtle.model.entity.user.User getUser() {
		return user;
	}
}
