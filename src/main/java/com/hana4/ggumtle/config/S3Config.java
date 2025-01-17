package com.hana4.ggumtle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
	@Bean
	public String s3BucketName() {
		String bucketName = System.getenv("S3_BUCKET_NAME");
		if (bucketName == null || bucketName.isEmpty()) {
			throw new IllegalStateException("S3_BUCKET_NAME environment variable is not set");
		}
		return bucketName;
	}
}
