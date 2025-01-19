package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.myData.MyData;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.MyDataRepository;

class MyDataServiceTest {

	@Mock
	private MyDataRepository myDataRepository;

	@InjectMocks
	private MyDataService myDataService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateRandomMyData() {
		// Given
		User user = new User();
		MyData myData = new MyData();
		when(myDataRepository.save(any(MyData.class))).thenReturn(myData);

		// When
		MyData result = myDataService.createRandomMyData(user);

		// Then
		assertNotNull(result);
		verify(myDataRepository, times(1)).save(any(MyData.class));
	}

	@Test
	void testGenerateRandomMyData() {
		// Given
		User user = new User();

		// When
		MyData result = myDataService.generateRandomMyData(user);

		// Then
		assertNotNull(result);
		assertEquals(user, result.getUser());
		assertNotNull(result.getDepositWithdrawal());
		assertNotNull(result.getSavingTimeDeposit());
		assertNotNull(result.getInvestment());
		assertNotNull(result.getForeignCurrency());
		assertNotNull(result.getPension());
		assertNotNull(result.getEtc());

		assertTrue(isValidAmount(result.getDepositWithdrawal()));
		assertTrue(isValidAmount(result.getSavingTimeDeposit()));
		assertTrue(isValidAmount(result.getInvestment()));
		assertTrue(isValidAmount(result.getForeignCurrency()));
		assertTrue(isValidAmount(result.getPension()));
		assertTrue(isValidAmount(result.getEtc()));
	}

	@Test
	void getMyData_성공() {
		MyData myData = new MyData();
		User user = new User();
		user.setId("1");
		myData.setUser(new User());
		myData.setDepositWithdrawal(BigDecimal.ONE);
		myData.setSavingTimeDeposit(BigDecimal.ONE);
		myData.setInvestment(BigDecimal.ONE);
		myData.setForeignCurrency(BigDecimal.ONE);
		myData.setPension(BigDecimal.ONE);
		myData.setEtc(BigDecimal.ONE);

		when(myDataRepository.findByUserId("1")).thenReturn(Optional.of(myData));

		assertThat(myDataService.getMyDataByUserId("1").getDepositWithdrawalRatio()).isEqualTo(
			myData.getDepositWithdrawal().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataByUserId("1").getSavingTimeDepositRatio()).isEqualTo(
			myData.getSavingTimeDeposit().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataByUserId("1").getInvestmentRatio()).isEqualTo(
			myData.getInvestment().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataByUserId("1").getForeignCurrencyRatio()).isEqualTo(
			myData.getForeignCurrency().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataByUserId("1").getPensionRatio()).isEqualTo(
			myData.getPension().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataByUserId("1").getEtcRatio()).isEqualTo(
			myData.getEtc().divide(BigDecimal.valueOf(6), 4, RoundingMode.HALF_UP));
	}

	@Test
	void getMyData_실패_사용자MyData없음() {
		User user = new User();
		user.setId("1");

		CustomException exception = assertThrows(CustomException.class, () -> {
			myDataService.getMyDataByUserId(user.getId());
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 MyData가 연결되지 않았습니다.", exception.getMessage());
		verify(myDataRepository).findByUserId(user.getId());
	}

	private boolean isValidAmount(BigDecimal amount) {
		return amount.compareTo(BigDecimal.valueOf(10000)) >= 0 &&
			amount.compareTo(BigDecimal.valueOf(10000000)) <= 0 &&
			amount.remainder(BigDecimal.valueOf(10000)).equals(BigDecimal.ZERO);
	}
}
