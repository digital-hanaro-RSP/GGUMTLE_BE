package com.hana4.ggumtle.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.global.error.CustomException;

class StringListConverterTest {

	private StringListConverter converter;

	@BeforeEach
	void setUp() {
		converter = new StringListConverter();
	}

	@Test
	void testConvertToDatabaseColumn() {
		// Arrange
		List<String> attribute = Arrays.asList("item1", "item2", "item3");

		// Act
		String result = converter.convertToDatabaseColumn(attribute);

		// Assert
		assertEquals("[\"item1\",\"item2\",\"item3\"]", result);
	}

	@Test
	void testConvertToEntityAttribute() {
		// Arrange
		String dbData = "[\"item1\",\"item2\",\"item3\"]";

		// Act
		List<String> result = converter.convertToEntityAttribute(dbData);

		// Assert
		assertEquals(Arrays.asList("item1", "item2", "item3"), result);
	}

	@Test
	void testConvertToDatabaseColumn_EmptyList() {
		// Arrange
		List<String> attribute = Arrays.asList();

		// Act
		String result = converter.convertToDatabaseColumn(attribute);

		// Assert
		assertEquals("[]", result);
	}

	@Test
	void testConvertToEntityAttribute_EmptyJson() {
		// Arrange
		String dbData = "[]";

		// Act
		List<String> result = converter.convertToEntityAttribute(dbData);

		// Assert
		assertTrue(result.isEmpty());
	}

	@Test
	void testConvertToDatabaseColumn_NullInput() {
		// Act & Assert
		assertThrows(CustomException.class, () -> converter.convertToDatabaseColumn(null));
	}

	@Test
	void testConvertToEntityAttribute_InvalidJson() {
		// Arrange
		String dbData = "invalid json";

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(dbData));
	}

	@Test
	void testConvertToEntityAttribute_NullInput() {
		List<String> result = converter.convertToEntityAttribute(null);
		assertTrue(result.isEmpty());
	}

	@Test
	void testConvertToEntityAttribute_EmptyString() {
		List<String> result = converter.convertToEntityAttribute("");
		assertTrue(result.isEmpty());
	}

	@Test
	void testConvertToDatabaseColumn_JsonProcessingException() throws JsonProcessingException {
		StringListConverter spyConverter = spy(converter);
		ObjectMapper mockMapper = mock(ObjectMapper.class);
		spyConverter.objectMapper = mockMapper;

		when(mockMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

		assertThrows(CustomException.class, () -> spyConverter.convertToDatabaseColumn(Arrays.asList("test")));
	}

}
