package com.hana4.ggumtle.dto.advertisement;

import java.util.List;
import java.util.stream.Collectors;

import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "광고 응답 DTO")
@Generated
public class AdvertisementResponseDto {
	@Schema(description = "메인 광고 응답 배열")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class MainAdList {
		@Schema(description = "광고배열", example = "")
		private List<MainAd> mainAds;

		public static MainAdList from(List<Advertisement> advertisements) {
			List<MainAd> mainAdList = advertisements.stream()
				.map(MainAd::from)
				.collect(Collectors.toList());
			return MainAdList.builder()
				.mainAds(mainAdList)
				.build();
		}
	}

	@Schema(description = "메인 광고 응답")
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class MainAd {
		@Schema(description = "광고 ID", example = "1")
		private Long id;

		@Schema(description = "SAVING_TIME_DEPOSIT-정기예금, INVESTMENT-투자, FOREIGN_CURRENCY-외화, PENSION-연금", example = "INVESTMENT")
		private AdvertisementProductType productType;

		@Schema(description = "광고 상품명", example = "미래에셋TIGER200중공업증권상장지수투자신탁(주식)")
		private String productName;

		@Schema(description = "MAIN-메인화면, COMMUNITY-커뮤니티화면", example = "MAIN")
		private AdvertisementLocationType locationType;

		@Schema(description = "위험등급", example = "매우높은위험")
		private String riskRating;

		@Schema(description = "수익률", example = "78.64%")
		private String yield;

		@Schema(description = "광고 누르면 이동될 링크", example = "https://www.tigeretf.com/ko/product/search/detail/index.do?ksdFund=KR7139230007")
		private String link;

		public static MainAd from(Advertisement advertisement) {
			return MainAd.builder()
				.id(advertisement.getId())
				.productType(advertisement.getProductType())
				.productName(advertisement.getProductName())
				.locationType(advertisement.getLocationType())
				.riskRating(advertisement.getRiskRating())
				.yield(advertisement.getYield())
				.link(advertisement.getLink())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class CommunityAd {
		private Long id;
		private AdvertisementLocationType locationType;
		private AdvertisementAdType adType;
		private String bannerImageUrl;
		private String link;

		public static CommunityAd from(Advertisement advertisement) {
			return CommunityAd.builder()
				.id(advertisement.getId())
				.locationType(advertisement.getLocationType())
				.adType(advertisement.getAdType())
				.bannerImageUrl(advertisement.getBannerImageUrl())
				.link(advertisement.getLink())
				.build();
		}
	}
}
