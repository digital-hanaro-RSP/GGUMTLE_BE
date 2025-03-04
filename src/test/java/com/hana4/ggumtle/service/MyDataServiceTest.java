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
		myData.setId(1L);

		when(myDataRepository.findByUserId("1")).thenReturn(Optional.of(myData));

		assertThat(myDataService.getMyDataByUserId("1").getDepositWithdrawal()).isEqualTo(
			myData.getDepositWithdrawal());
		assertThat(myDataService.getMyDataByUserId("1").getSavingTimeDeposit()).isEqualTo(
			myData.getSavingTimeDeposit());
		assertThat(myDataService.getMyDataByUserId("1").getInvestment()).isEqualTo(myData.getInvestment());
		assertThat(myDataService.getMyDataByUserId("1").getForeignCurrency()).isEqualTo(myData.getForeignCurrency());
		assertThat(myDataService.getMyDataByUserId("1").getPension()).isEqualTo(myData.getPension());
		assertThat(myDataService.getMyDataByUserId("1").getEtc()).isEqualTo(myData.getEtc());
		assertThat(myDataService.getMyDataByUserId("1").getId()).isEqualTo(1L);
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

	@Test
	void getTotalAsset_Success() {
		String userId = "testUser";
		MyData myData = new MyData();
		myData.setDepositWithdrawal(BigDecimal.valueOf(10000));
		myData.setSavingTimeDeposit(BigDecimal.valueOf(20000));
		myData.setInvestment(BigDecimal.valueOf(30000));
		myData.setForeignCurrency(BigDecimal.valueOf(40000));
		myData.setPension(BigDecimal.valueOf(50000));
		myData.setEtc(BigDecimal.valueOf(60000));

		when(myDataRepository.findByUserId(userId)).thenReturn(Optional.of(myData));

		BigDecimal result = myDataService.getTotalAsset(userId);

		assertEquals(BigDecimal.valueOf(210000), result);
		verify(myDataRepository).findByUserId(userId);
	}

	@Test
	void getTotalAsset_UserNotFound() {
		String userId = "nonExistentUser";
		when(myDataRepository.findByUserId(userId)).thenReturn(Optional.empty());

		CustomException exception = assertThrows(CustomException.class, () -> {
			myDataService.getTotalAsset(userId);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("유저의 자산 정보를 찾을 수 없습니다.", exception.getMessage());
		verify(myDataRepository).findByUserId(userId);
	}

	@Test
	void getMyDataRate_성공() {
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
		myData.setId(1L);

		BigDecimal sum = myData.getDepositWithdrawal().add(myData.getSavingTimeDeposit())
			.add(myData.getInvestment())
			.add(myData.getForeignCurrency())
			.add(myData.getPension())
			.add(myData.getEtc());

		when(myDataRepository.findByUserId("1")).thenReturn(Optional.of(myData));

		assertThat(myDataService.getMyDataRateByUserId("1").getDepositWithdrawal()).isEqualTo(
			myData.getDepositWithdrawal().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getSavingTimeDeposit()).isEqualTo(
			myData.getSavingTimeDeposit().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getInvestment()).isEqualTo(
			myData.getInvestment().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getForeignCurrency()).isEqualTo(
			myData.getForeignCurrency().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getPension()).isEqualTo(
			myData.getPension().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getEtc()).isEqualTo(
			myData.getEtc().divide(sum, 2, RoundingMode.HALF_UP));
		assertThat(myDataService.getMyDataRateByUserId("1").getId()).isEqualTo(1L);
	}

	@Test
	void getMyDataRate_실패_사용자MyData없음() {
		User user = new User();
		user.setId("1");

		CustomException exception = assertThrows(CustomException.class, () -> {
			myDataService.getMyDataRateByUserId(user.getId());
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 유저의 MyData가 연결되지 않았습니다.", exception.getMessage());
		verify(myDataRepository).findByUserId(user.getId());
	}
}
