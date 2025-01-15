package com.hana4.ggumtle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

	private boolean isValidAmount(BigDecimal amount) {
		return amount.compareTo(BigDecimal.valueOf(10000)) >= 0 &&
			amount.compareTo(BigDecimal.valueOf(10000000)) <= 0 &&
			amount.remainder(BigDecimal.valueOf(10000)).equals(BigDecimal.ZERO);
	}
}
