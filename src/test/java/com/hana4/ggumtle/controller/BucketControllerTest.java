// package com.hana4.ggumtle.controller;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
//
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
// import com.hana4.ggumtle.dto.bucketList.BucketRequestDto;
// import com.hana4.ggumtle.dto.bucketList.BucketResponseDto;
// import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
// import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
// import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
// import com.hana4.ggumtle.service.BucketService;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// class BucketControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockitoBean
// 	private BucketService bucketService;
//
// 	private BucketRequestDto bucketRequestDto;
// 	private BucketResponseDto.BucketInfo bucketResponseDto;
//
// 	@Test
// 	void createBucket_shouldReturnCreatedBucket() throws Exception {
// 		// Arrange
// 		BucketResponseDto.BucketInfo mockResponse = BucketResponseDto.BucketInfo.builder()
// 			.title("Sample Title")
// 			.tagType(BucketTagType.GO) // Add enum or actual value here
// 			.dueDate(LocalDateTime.of(2025, 1, 14, 0, 0)) // Replace with LocalDateTime value
// 			.howTo(BucketHowTo.MONEY)   // Add BucketHowTo or mock value here
// 			.isDueSet(true)
// 			.isAutoAllocate(true)
// 			.allocateAmount(BigDecimal.valueOf(500)) // Add BigDecimal or mock value here
// 			.cronCycle("0 0 * * *")
// 			.goalAmount(BigDecimal.valueOf(500)) // Add BigDecimal or mock value here
// 			.memo("Sample Memo")
// 			.status(BucketStatus.DOING)     // Add BucketStatus or mock value here
// 			.isRecommended(true)
// 			.originId(BigDecimal.valueOf(2))   // Add BigDecimal or mock value here
// 			.build();
//
// 		Mockito.when(bucketService.createBucket(any(BucketRequestDto.class))).thenReturn(mockResponse);
//
// 		// Act & Assert
// 		mockMvc.perform(MockMvcRequestBuilders.post("/api/buckets")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content("""
//
// 					{
// 					                       "title": "Sample Title",
// 					                       "tagType": "GO",
// 					                       "dueDate": "2025-01-14T00:00",
// 					                       "howTo": "MONEY",
// 					                       "isDueSet": true,
// 					                       "isAutoAllocate": true,
// 					                       "allocateAmount": 500,
// 					                       "cronCycle": "0 0 * * *",
// 					                       "goalAmount": 500,
// 					                       "memo": "Sample Memo",
// 					                       "status": "DOING",
// 					                       "isRecommended": true,
// 					                       "originId": 2
// 					                   }
// 					"""))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.title").value("Sample Title"))
// 			.andExpect(jsonPath("$.data.isDueSet").value(true));
// 	}
//
// 	@Test
// 	void updateBucket_shouldReturnUpdatedBucket() throws Exception {
// 		// Arrange
// 		Long bucketId = 1L;
// 		BucketResponseDto.BucketInfo mockResponse = BucketResponseDto.BucketInfo.builder()
// 			.title("Updated Title")
// 			.tagType(BucketTagType.GO) // Add enum or actual value here
// 			.dueDate(LocalDateTime.of(2025, 1, 14, 0, 0)) // Replace with LocalDateTime value
// 			.howTo(BucketHowTo.MONEY)   // Add BucketHowTo or mock value here
// 			.isDueSet(true)
// 			.isAutoAllocate(true)
// 			.allocateAmount(BigDecimal.valueOf(500)) // Add BigDecimal or mock value here
// 			.cronCycle("0 0 * * *")
// 			.goalAmount(BigDecimal.valueOf(500)) // Add BigDecimal or mock value here
// 			.memo("Updated Memo")
// 			.status(BucketStatus.DOING)     // Add BucketStatus or mock value here
// 			.isRecommended(true)
// 			.originId(BigDecimal.valueOf(2))   // Add BigDecimal or mock value here
// 			.build();
//
// 		Mockito.when(bucketService.updateBucket(eq(bucketId), any(BucketRequestDto.class))).thenReturn(mockResponse);
//
// 		// Act & Assert
// 		mockMvc.perform(MockMvcRequestBuilders.put("/api/buckets/{bucketId}", bucketId)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content("""
// 					{
// 					    "title": "Updated Title",
// 					                       "tagType": "GO",
// 					                       "dueDate": "2025-01-14T00:00",
// 					                       "howTo": "MONEY",
// 					                       "isDueSet": true,
// 					                       "isAutoAllocate": true,
// 					                       "allocateAmount": 500,
// 					                       "cronCycle": "0 0 * * *",
// 					                       "goalAmount": 500,
// 					                       "memo": "Updated Memo",
// 					                       "status": "DOING",
// 					                       "isRecommended": true,
// 					                       "originId": 2
// 					}
// 					"""))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.data.title").value("Updated Title"))
// 			.andExpect(jsonPath("$.data.memo").value("Updated Memo"));
// 	}
// }
