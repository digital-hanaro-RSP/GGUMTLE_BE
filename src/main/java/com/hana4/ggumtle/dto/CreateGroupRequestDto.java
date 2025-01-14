package com.hana4.ggumtle.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateGroupRequestDto {
	private String name;
	private String category;
	private String description;
	private String imageUrl;
}
