// package com.hana4.ggumtle.security;
//
// import java.time.LocalDateTime;
//
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.web.SecurityFilterChain;
//
// import com.hana4.ggumtle.model.entity.user.User;
// import com.hana4.ggumtle.model.entity.user.UserRole;
//
// @TestConfiguration
// public class TestSecurityConfigV2 {
//
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// 		return http
// 			.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
// 			.csrf(AbstractHttpConfigurer::disable)
// 			.build();
// 	}
//
// 	@Bean
// 	public UserDetailsService userDetailsService() {
// 		return new UserDetailsService() {
// 			@Override
// 			public UserDetails loadUserByUsername(String username)
// 				throws UsernameNotFoundException {
// 				switch (username) {
// 					default:
// 						User mockUser = User.builder()
// 							.id("27295730-41ce-4df8-9864-4da1fa3c6caa")
// 							.tel("01098765432")
// 							.name("테스터")
// 							.permission((short)0)
// 							.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
// 							.gender("m")
// 							.role(UserRole.USER)
// 							.nickname("테스트계정")
// 							.password("mockPassword")
// 							.build();
// 						return new CustomUserDetails(mockUser);
// 				}
// 			}
// 		};
// 	}
// }
