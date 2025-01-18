package com.hana4.ggumtle.service;

import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class PresignedUrlService {

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.s3.region}")
	private String region;

	/**
	 * Presigned URL을 생성하여 반환
	 *
	 * @param objectKey        업로드할 파일의 키 (경로)
	 * @param expirationMinutes Presigned URL의 유효 시간 (분)
	 * @return Presigned URL
	 */
	public URL generatePresignedUrl(String objectKey, int expirationMinutes) {
		// S3 Presigner 생성 (IAM 역할 사용)
		S3Presigner presigner = S3Presigner.builder()
			.region(Region.of(region)) // AWS 리전을 설정
			.build();

		// PutObjectRequest 생성
		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(objectKey)
			.build();

		// Presigned URL 생성 요청
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(expirationMinutes)) // URL 만료 시간
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

		// Presigned URL 반환
		return presignedRequest.url();
	}
}
