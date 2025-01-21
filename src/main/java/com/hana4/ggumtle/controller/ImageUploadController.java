package com.hana4.ggumtle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.CustomApiResponse;
import com.hana4.ggumtle.dto.image.ImageRequestDto;
import com.hana4.ggumtle.service.PresignedUrlService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("imageUpload")
@RequiredArgsConstructor
@Tag(name = "ImageUpload", description = "s3 Image 업로드 관련 API")
public class ImageUploadController {

	private final PresignedUrlService presignedUrlService;

	@Operation(summary = "다중 이미지 업로드 URL 생성", description = "여러 이미지 업로드를 위한 presigned URL들을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "URL 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(mediaType = "application/json",
				schema = @Schema(example = "{ \"code\": 400, \"error\": \"Bad_Request\", \"message\": \"잘못된 요청입니다.\" }"))),
		@ApiResponse(responseCode = "413", description = "파일 크기 초과",
			content = @Content(mediaType = "application/json",
				schema = @Schema(example = "{ \"code\": 413, \"error\": \"Payload_Too_Large\", \"message\": \"파일 크기가 제한을 초과했습니다.\" }"))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류(파일 크기 제한)",
			content = @Content(mediaType = "application/json",
				schema = @Schema(example = "{ \"code\": 500, \"error\": \"Internal_Server_Error\", \"message\": \"내부 서버 오류가 발생했습니다.\" }")))
	})
	@PostMapping("/multiple")
	public ResponseEntity<CustomApiResponse<List<String>>> generateMultiplePresignedUrls(
		@RequestBody @Valid ImageRequestDto.Upload imageUrls) {
		List<String> presignedUrls = presignedUrlService.generateMultiplePresignedUrls(imageUrls);
		return ResponseEntity.ok(CustomApiResponse.success(presignedUrls));
	}
}
