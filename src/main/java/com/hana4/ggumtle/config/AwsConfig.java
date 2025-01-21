package com.hana4.ggumtle.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}
