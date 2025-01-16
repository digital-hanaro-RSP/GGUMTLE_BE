package com.hana4.ggumtle.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.dto.survey.SurveyRequestDto;
import com.hana4.ggumtle.dto.survey.SurveyResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.model.entity.survey.Survey;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;
import com.hana4.ggumtle.repository.SurveyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyService {
	private final SurveyRepository surveyRepository;
	private final PortfolioTemplateRepository portfolioTemplateRepository;
	private final GoalPortfolioService goalPortfolioService;
	private final UserService userService;

	public SurveyResponseDto.CreateResponse createSurvey(SurveyRequestDto.Create surveyRequestDto, User user) {

		if (!validateTarget(user)) {
			throw new CustomException(ErrorCode.ALREADY_EXISTS, "서베이 대상자가 아닙니다.");
		}

		PortfolioTemplate template = portfolioTemplateRepository.findByName(
				surveyRequestDto.getInvestmentType().toUpperCase())
			.orElseThrow(() -> new CustomException(
				ErrorCode.NOT_FOUND, "investmentType을 찾을 수 없습니다."));

		// PortfolioTemplate을 GoalPortfolio로 변환하여 저장
		goalPortfolioService.createGoalPortfolioAndSave(template, user);

		// user permission 3으로 update
		userService.updatePermission(user, (short)(user.getPermission() + 2));

		return SurveyResponseDto.CreateResponse.from(surveyRepository.save(surveyRequestDto.toEntity(user)));
	}

	Boolean validateTarget(User user) {
		Survey survey = surveyRepository.findByUser(user).orElse(null);

		// GoalPortfolio가 없는 경우 대상자로 간주
		if (survey == null) {
			return true;
		}

		// 서베이 작성일이 6개월 이상 지났으면 대상자로 간주
		if (survey.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(6))) {
			return true;
		}

		// 이진수의 십의 자리 1인지 확인
		return (user.getPermission() & 2) != 0;
	}
}
