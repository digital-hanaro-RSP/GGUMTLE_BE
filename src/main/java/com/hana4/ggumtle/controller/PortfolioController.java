package com.hana4.ggumtle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.service.GoalPortfolioService;
import com.hana4.ggumtle.service.MyDataService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

	private final GoalPortfolioService goalPortfolioService;
	private final MyDataService myDataService;

	public PortfolioController(GoalPortfolioService goalPortfolioService, MyDataService myDataService) {
		this.goalPortfolioService = goalPortfolioService;
		this.myDataService = myDataService;
	}

	@GetMapping("/goal-portfolio")
	public ResponseEntity<GoalPortfolioResponseDto.Ratio> getGoalPortfolioByUserId(@RequestParam String userId) {
		GoalPortfolioResponseDto.Ratio goalPortfolio = goalPortfolioService.getGoalPortfolioByUserId(userId);
		return ResponseEntity.ok(goalPortfolio);
	}

	// @GetMapping("/my-data")
	// public ResponseEntity<MyDataResponseDto> getMyDataByUserId(@RequestParam String userId) {
	// 	MyDataResponseDto.CurrentPortfolio myData = myDataService.getMyDataByUserId(userId);
	// 	return ResponseEntity.ok(myData);
	// }
	//
	// @GetMapping("/combined")
	// public ResponseEntity<Object> getCombinedPortfolioData(@RequestParam String userId) {
	// 	GoalPortfolioResponseDto.Ratio goalPortfolio = goalPortfolioService.getGoalPortfolioByUserId(userId);
	// 	MyDataResponseDto.CurrentPortfolio myData = myDataService.getMyDataByUserId(userId);
	//
	// 	// Combine both GoalPortfolio and MyData into a single response
	// 	return ResponseEntity.ok(new CombinedPortfolioResponseDto(goalPortfolio, myData));
	// }
}
