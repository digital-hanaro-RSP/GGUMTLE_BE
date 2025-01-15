package com.hana4.ggumtle.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

class CustomApiResponseTest {

	@Test
	void testSuccessWithoutData() {
		CustomApiResponse<Void> response = CustomApiResponse.success();

		assertEquals(200, response.getCode());
		assertNull(response.getError());
		assertEquals("ok", response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testSuccessWithData() {
		String testData = "Test Data";
		CustomApiResponse<String> response = CustomApiResponse.success(testData);

		assertEquals(200, response.getCode());
		assertNull(response.getError());
		assertEquals("ok", response.getMessage());
		assertEquals(testData, response.getData());
	}

	@Test
	void testFailure() {
		CustomApiResponse<Void> response = CustomApiResponse.failure(404, "Not Found", "Resource not found");

		assertEquals(404, response.getCode());
		assertEquals("Not Found", response.getError());
		assertEquals("Resource not found", response.getMessage());
		assertNull(response.getData());
	}

	@Test
	void testJsonInclude() {
		CustomApiResponse<Void> response = CustomApiResponse.success();

		assertNull(response.getData());
		assertNull(response.getError());
	}

	@Test
	void testDirectInstantiationPrevention() {
		InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
			Constructor<CustomApiResponse> constructor = CustomApiResponse.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		});

		Throwable cause = exception.getCause();
		assertNotNull(cause);
		assertEquals(IllegalStateException.class, cause.getClass());
		assertEquals("Cannot instantiate ApiResponse directly", cause.getMessage());
	}

}
