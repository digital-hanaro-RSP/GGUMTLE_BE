package com.hana4.ggumtle.service;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.hana4.ggumtle.dto.image.ImageRequestDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import jakarta.validation.Valid;
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

	//다중 파일 업로드 및 용량 제한
	public List<String> generateMultiplePresignedUrls(@RequestBody @Valid ImageRequestDto.Upload imageUrls) {
		if (imageUrls.getImages().isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		List<String> presignedUrls = new ArrayList<>();
		long MAX_TOTAL_SIZE = 1 * 1024 * 1024; // 1MB in bytes

		for (ImageRequestDto.Upload.Image fileInfo : imageUrls.getImages()) {
			if (fileInfo.getSize() > MAX_TOTAL_SIZE) {
				throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
			}
		}

		for (ImageRequestDto.Upload.Image fileInfo : imageUrls.getImages()) {

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileInfo.getName())
				.contentLength(fileInfo.getSize())
				.build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(15))
				.putObjectRequest(putObjectRequest)
				.build();

			try {
				PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
				presignedUrls.add(presignedRequest.url().toString());
			} catch (Exception e) {
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}

		return presignedUrls;
	}
}
