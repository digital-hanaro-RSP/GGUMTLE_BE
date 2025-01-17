package com.hana4.ggumtle.controller;

import java.time.Duration;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.imageUpload.ImageUploadRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RestController
@RequestMapping("/images")
@Tag(name = "ImageUpload", description = "s3 Image 업로드 관련 API")
public class ImageUploadController {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public ImageUploadController(String bucketName) {
		this.s3Client = S3Client.builder().build();
		this.s3Presigner = S3Presigner.builder().build();
	}

	@Operation(summary = "이미지 업로드 요청", description = "S3에 이미지 업로드를 요청합니다.")
	@PostMapping("/upload-url")
	public String getPresignedUrl(@RequestBody ImageUploadRequestDto request) {
		String bucketName = "your-bucket-name";
		String objectKey = "images/" + request.getFileName();

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(objectKey)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofHours(1))
			.putObjectRequest(objectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

		return presignedRequest.url().toString();
	}
}