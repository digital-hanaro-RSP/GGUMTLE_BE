package com.hana4.ggumtle.dto.image;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Generated
public class ImageRequestDto {

	@Schema(description = "이미지 업로드 요청 DTO")
	@Getter
	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Upload {

		@Schema(description = "이미지 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "이미지 리스트를 입력하세요.")
		private List<Image> images;

		@Schema(description = "이미지 정보")
		@Getter
		@Builder(toBuilder = true)
		@AllArgsConstructor
		@NoArgsConstructor(access = AccessLevel.PROTECTED)
		public static class Image {
			@Schema(description = "이미지 이름", example = "dogs.png", requiredMode = Schema.RequiredMode.REQUIRED)
			@NotEmpty(message = "이미지 이름을 입력하세요.")
			private String name;

			@Schema(description = "이미지 크기 (바이트 단위)", example = "1024", requiredMode = Schema.RequiredMode.REQUIRED)
			@NotNull(message = "이미지 크기를 입력하세요.")
			private Long size;
		}
	}
}