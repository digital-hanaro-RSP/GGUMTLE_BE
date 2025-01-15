package com.hana4.ggumtle.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
	@Bean
	public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		// http request 인증 설정
		http.authorizeHttpRequests(
			authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest()
				.permitAll());

		// CSRF 설정
		http.csrf(AbstractHttpConfigurer::disable);

		// form login 비활성화 (JWT 사용)
		http.formLogin(AbstractHttpConfigurer::disable);

		// logout 비활성화 (JWT 사용)
		http.logout(AbstractHttpConfigurer::disable);

		return http.build();
	}
}