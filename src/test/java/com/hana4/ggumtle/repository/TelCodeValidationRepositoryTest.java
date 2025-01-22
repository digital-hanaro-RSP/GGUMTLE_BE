package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class TelCodeValidationRepositoryTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private TelCodeValidationRepository telCodeValidationRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void createSmsCertification_Success() {
		String phone = "01012345678";
		String certificationNumber = "123456";

		telCodeValidationRepository.createSmsCertification(phone, certificationNumber);

		verify(valueOperations, times(1))
			.set(eq("sms:" + phone), eq(certificationNumber), eq(Duration.ofSeconds(180)));
	}

	@Test
	void incrementDailyRequestCount_FirstRequest() {
		String phone = "01012345678";
		String dailyLimitKey = "daily_sms_limit:" + phone;

		when(valueOperations.increment(dailyLimitKey)).thenReturn(1L);

		boolean result = telCodeValidationRepository.incrementDailyRequestCount(phone);

		assertThat(result).isTrue();
		verify(valueOperations, times(1)).increment(dailyLimitKey);
		verify(redisTemplate, times(1)).expire(eq(dailyLimitKey), eq(Duration.ofSeconds(86400)));
	}

	@Test
	void incrementDailyRequestCount_ExceedsLimit() {
		String phone = "01012345678";
		String dailyLimitKey = "daily_sms_limit:" + phone;

		when(valueOperations.increment(dailyLimitKey)).thenReturn(11L);

		boolean result = telCodeValidationRepository.incrementDailyRequestCount(phone);

		assertThat(result).isFalse();
		verify(valueOperations, times(1)).increment(dailyLimitKey);
		verify(redisTemplate, never()).expire(eq(dailyLimitKey), any());
	}

	@Test
	void getSmsCertification_Success() {
		String phone = "01012345678";
		String certificationNumber = "123456";

		when(valueOperations.get("sms:" + phone)).thenReturn(certificationNumber);

		String result = telCodeValidationRepository.getSmsCertification(phone);

		assertThat(result).isEqualTo(certificationNumber);
		verify(valueOperations, times(1)).get("sms:" + phone);
	}

	@Test
	void removeSmsCertification_Success() {
		String phone = "01012345678";

		when(redisTemplate.delete("sms:" + phone)).thenReturn(true);

		telCodeValidationRepository.removeSmsCertification(phone);

		verify(redisTemplate, times(1)).delete("sms:" + phone);
	}

	@Test
	void hasKey_Exists() {
		String phone = "01012345678";

		when(redisTemplate.hasKey("sms:" + phone)).thenReturn(true);

		boolean result = telCodeValidationRepository.hasKey(phone);

		assertThat(result).isTrue();
		verify(redisTemplate, times(1)).hasKey("sms:" + phone);
	}

	@Test
	void hasKey_DoesNotExist() {
		String phone = "01012345678";

		when(redisTemplate.hasKey("sms:" + phone)).thenReturn(false);

		boolean result = telCodeValidationRepository.hasKey(phone);

		assertThat(result).isFalse();
		verify(redisTemplate, times(1)).hasKey("sms:" + phone);
	}

}
