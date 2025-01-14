package com.hana4.ggumtle.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.model.entity.myData.MyData;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.MyDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyDataService {
	private final MyDataRepository myDataRepository;

	public MyData createRandomMyData(User user) {
		MyData myData = generateRandomMyData(user);
		return myDataRepository.save(myData);
	}

	private MyData generateRandomMyData(User user) {
		Random random = new Random();
		return MyData.builder()
			.user(user)
			.depositWithdrawal(generateRandomAmount(random))
			.savingTimeDeposit(generateRandomAmount(random))
			.investment(generateRandomAmount(random))
			.foreignCurrency(generateRandomAmount(random))
			.pension(generateRandomAmount(random))
			.etc(generateRandomAmount(random))
			.build();
	}

	private BigDecimal generateRandomAmount(Random random) {
		return BigDecimal.valueOf((random.nextInt(1000) + 1) * 10000);
	}
}
