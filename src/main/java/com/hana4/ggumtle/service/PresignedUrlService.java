package com.hana4.ggumtle.service;

import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class PresignedUrlService {
	private final String bucketName;
	private final S3Presigner s3Presigner;

	public PresignedUrlService(@Value("${aws.s3.bucket-name}") String bucketName,
		@Value("${aws.s3.region}") String regionString) {
		Region region = Region.of(regionString);
		this.bucketName = bucketName;
		this.s3Presigner = S3Presigner.builder()
			.region(region)
			.build();
	}

	public URL generatePresignedUrl(String objectKey, int expirationMinutes) {
		try {
			PutObjectRequest objectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(objectKey)
				.build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(expirationMinutes))
				.putObjectRequest(objectRequest)
				.build();

			PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

			return presignedRequest.url();
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
