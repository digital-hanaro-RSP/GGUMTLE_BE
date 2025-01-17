package com.hana4.ggumtle.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
	ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if (attribute == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER, "list는 null값일 수 없습니다.");
		}
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER, "JSON으로 파싱 중 에러가 발생했습니다.");
		}
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			return objectMapper.readValue(dbData, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting JSON to list", e);
		}
	}
}
