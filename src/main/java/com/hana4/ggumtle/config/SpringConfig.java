package com.hana4.ggumtle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringConfig implements WebMvcConfigurer {
		@Bean
		public OpenAPI openAPI() {
				return new OpenAPI()
						.components(new Components()
								.addSecuritySchemes("bearerAuth",
										new SecurityScheme()
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")))
						.info(info())
						.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
		}

		private Info info() {
				return new Info()
						.version("0.1.0")
						.title("GGUMTLE Api Spec.")
						.description("description");
		}
}
