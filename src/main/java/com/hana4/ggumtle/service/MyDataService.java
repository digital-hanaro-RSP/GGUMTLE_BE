package com.hana4.ggumtle.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.myData.MyDataResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
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

	protected MyData generateRandomMyData(User user) {
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

	public MyDataResponseDto.CurrentPortfolio getMyDataByUserId(String userId) {
		return MyDataResponseDto.CurrentPortfolio.from(myDataRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 유저의 MyData가 연결되지 않았습니다.")));
	}

	public BigDecimal getTotalAsset(String userId) {
		MyData myData = myDataRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "유저의 자산 정보를 찾을 수 없습니다."));

		return myData.getDepositWithdrawal()
			.add(myData.getSavingTimeDeposit())
			.add(myData.getInvestment())
			.add(myData.getForeignCurrency())
			.add(myData.getPension())
			.add(myData.getEtc());
	}
}
