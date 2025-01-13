package com.hana4.ggumtle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class) // Spring Boot Security Auto Configuration을 비활성화
public class GgumtleApplication {

    public static void main(String[] args) {
        SpringApplication.run(GgumtleApplication.class, args);
    }

}
