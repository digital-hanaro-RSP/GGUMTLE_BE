package com.hana4.ggumtle.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BaseDto {
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
