package com.hana4.ggumtle.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.hana4.ggumtle.security.filter.JwtAuthFilter;
import com.hana4.ggumtle.security.handler.CustomAccessDeniedHandler;
import com.hana4.ggumtle.security.handler.CustomAuthenticationEntryPointHandler;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security Config
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;

	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

		// white list (Spring Security 체크 제외 목록)
		MvcRequestMatcher[] permitAllWhiteList = {
			mvc.pattern("/auth/tokens"),
			mvc.pattern("/user"),
			mvc.pattern("/auth/refresh"),
			mvc.pattern("/verification-code"),
			mvc.pattern("/verification-code/validation"),
			mvc.pattern("/favicon.ico"),
			mvc.pattern("/error"),
			mvc.pattern("/swagger-resources/**\""),
			mvc.pattern("/v3/api-docs/**"),
			mvc.pattern("/swagger-ui/**"),
			mvc.pattern("/webjars/**"),
			mvc.pattern("/imageUpload/multiple")
		};

		// http request 인증 설정
		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers(permitAllWhiteList).permitAll() // 화이트리스트 설정
			.requestMatchers(PathRequest.toH2Console()).permitAll() // H2 콘솔 허용
			.anyRequest().authenticated()  // 마지막에 위치해야 함
		);

		// CSRF 설정: H2 콘솔에 대해 CSRF 무시
		http.csrf(csrf -> csrf
				.ignoringRequestMatchers(PathRequest.toH2Console())  // H2 콘솔에 대해 CSRF 무시 처리
			)
			.headers(
				headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));  // Same-origin 설정

		// form login 비활성화 (JWT 사용)
		http.formLogin(AbstractHttpConfigurer::disable);

		// logout 비활성화 (JWT 사용)
		http.logout(AbstractHttpConfigurer::disable);

		// csrf 비활성화
		http.csrf(AbstractHttpConfigurer::disable);

		// 세션 관리: Stateless로 설정 (세션 사용하지 않음)
		http.sessionManagement(session -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		// CORS 설정 추가
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		// JWT 필터 추가 (사용자 정의 필터)
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		// 예외 처리 핸들러
		http.exceptionHandling(conf -> conf
			.authenticationEntryPoint(customAuthenticationEntryPointHandler)
			.accessDeniedHandler(customAccessDeniedHandler)
		);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://ggumtle.topician.com"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
