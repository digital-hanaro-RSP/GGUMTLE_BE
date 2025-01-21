package com.hana4.ggumtle.controller;

import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.service.PresignedUrlService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("imageUpload")
@RequiredArgsConstructor
@Tag(name = "ImageUpload", description = "s3 Image 업로드 관련 API")
public class ImageUploadController {

	private final PresignedUrlService presignedUrlService;

	@GetMapping
	public ResponseEntity<CustomApiResponse<String>> generatePresignedUrl(
		@RequestParam String objectKey,
		@RequestParam(defaultValue = "15") int expirationMinutes) {
		try {
			URL presignedUrl = presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);
			return ResponseEntity.ok(CustomApiResponse.success(presignedUrl.toString()));
		} catch (CustomException ce) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
