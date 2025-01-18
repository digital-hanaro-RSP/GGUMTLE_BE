package com.hana4.ggumtle.controller;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.service.PresignedUrlService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("imageUpload")
@Tag(name = "ImageUpload", description = "s3 Image 업로드 관련 API")
public class ImageUploadController {

	@Autowired
	private PresignedUrlService presignedUrlService;

	@GetMapping
	public ResponseEntity<String> generatePresignedUrl(
		@RequestParam String objectKey,
		@RequestParam(defaultValue = "15") int expirationMinutes) {
		try {
			URL presignedUrl = presignedUrlService.generatePresignedUrl(objectKey, expirationMinutes);
			return ResponseEntity.ok(presignedUrl.toString());
		} catch (Exception e) {
			// 오류 발생 시 적절한 HTTP 상태 코드와 메시지 반환
			return ResponseEntity.status(500).body("Error generating presigned URL: " + e.getMessage());
		}
	}
}