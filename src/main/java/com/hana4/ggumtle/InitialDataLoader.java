package com.hana4.ggumtle;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;
import com.hana4.ggumtle.model.entity.portfolioTemplate.PortfolioTemplate;
import com.hana4.ggumtle.repository.AdvertisementRepository;
import com.hana4.ggumtle.repository.PortfolioTemplateRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationRunner {
	private final PortfolioTemplateRepository portfolioTemplateRepository;
	private final AdvertisementRepository advertisementRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (portfolioTemplateRepository.count() == 0) {
			PortfolioTemplate conservative = PortfolioTemplate.builder()
				.name("CONSERVATIVE")
				.depositWithdrawalRatio(new BigDecimal("0.70"))
				.savingTimeDepositRatio(new BigDecimal("0.20"))
				.investmentRatio(new BigDecimal("0.20"))
				.foreignCurrencyRatio(BigDecimal.ZERO)
				.pensionRatio(new BigDecimal("0.10"))
				.etcRatio(BigDecimal.ZERO)
				.build();

			PortfolioTemplate moderatelyConservative = PortfolioTemplate.builder()
				.name("MODERATELY_CONSERVATIVE")
				.depositWithdrawalRatio(new BigDecimal("0.50"))
				.savingTimeDepositRatio(new BigDecimal("0.30"))
				.investmentRatio(new BigDecimal("0.30"))
				.foreignCurrencyRatio(new BigDecimal("0.05"))
				.pensionRatio(new BigDecimal("0.15"))
				.etcRatio(BigDecimal.ZERO)
				.build();

			PortfolioTemplate balanced = PortfolioTemplate.builder()
				.name("BALANCED")
				.depositWithdrawalRatio(new BigDecimal("0.30"))
				.savingTimeDepositRatio(new BigDecimal("0.40"))
				.investmentRatio(new BigDecimal("0.40"))
				.foreignCurrencyRatio(new BigDecimal("0.10"))
				.pensionRatio(new BigDecimal("0.15"))
				.etcRatio(new BigDecimal("0.05"))
				.build();

			PortfolioTemplate growthOriented = PortfolioTemplate.builder()
				.name("MODERATELY_AGGRESSIVE")
				.depositWithdrawalRatio(new BigDecimal("0.20"))
				.savingTimeDepositRatio(new BigDecimal("0.50"))
				.investmentRatio(new BigDecimal("0.50"))
				.foreignCurrencyRatio(new BigDecimal("0.15"))
				.pensionRatio(new BigDecimal("0.10"))
				.etcRatio(new BigDecimal("0.05"))
				.build();

			PortfolioTemplate aggressive = PortfolioTemplate.builder()
				.name("AGGRESSIVE")
				.depositWithdrawalRatio(new BigDecimal("0.10"))
				.savingTimeDepositRatio(new BigDecimal("0.60"))
				.investmentRatio(new BigDecimal("0.60"))
				.foreignCurrencyRatio(new BigDecimal("0.15"))
				.pensionRatio(new BigDecimal("0.05"))
				.etcRatio(new BigDecimal("0.10"))
				.build();

			portfolioTemplateRepository.saveAll(
				Arrays.asList(conservative, moderatelyConservative, balanced, growthOriented, aggressive));
		}

		if (advertisementRepository.count() == 0) {
			// 메인페이지용 광고

			Advertisement main1 = Advertisement.builder()
				.productType(AdvertisementProductType.INVESTMENT)
				.productName("미래에셋TIGER200중공업증권상장지수투자신탁(주식)")
				.locationType(AdvertisementLocationType.MAIN)
				.adType(AdvertisementAdType.HANA)
				// .security("수익증권 ETF") // 종목구분
				.riskRating("매우높은위험") // 위험등급
				.yield("78.64%") // 수익률
				.link("https://www.tigeretf.com/ko/product/search/detail/index.do?ksdFund=KR7139230007")
				.build();

			Advertisement main2 = Advertisement.builder()
				.productType(AdvertisementProductType.PENSION)
				.productName("삼성글로벌메타버스증권자투자신탁UH(주식)Cpe(퇴직연금)")
				.locationType(AdvertisementLocationType.MAIN)
				.adType(AdvertisementAdType.HANA)
				.riskRating("높은위험") // 위험등급
				.yield("51.93%") // 수익률
				.link("https://www.samsungfund.com/fund/product/view.do?id=K55105DK2904")
				.build();

			Advertisement main3 = Advertisement.builder()
				.productType(AdvertisementProductType.INVESTMENT)
				.productName("신한디딤글로벌EMP증권투자신탁[혼합-재간접형](종류C-re)")
				.locationType(AdvertisementLocationType.MAIN)
				.adType(AdvertisementAdType.HANA)
				.riskRating("보통위험") // 위험등급
				.yield("3.60%") // 수익률
				.link("https://www.shinhanfund.com/ko/pc/fund/view?fundCd=270879")
				.build();

			Advertisement main4 = Advertisement.builder()
				.productType(AdvertisementProductType.INVESTMENT)
				.productName("미래에셋하나1Q퇴직연금증권자투자신탁1호(채권혼합)C-P2e")
				.locationType(AdvertisementLocationType.MAIN)
				.riskRating("낮은위험") // 위험등급
				.yield("9.22%") // 수익률
				.link("https://investments.miraeasset.com/fund/view.do?fundGb=2&fundCd=484660")
				.build();

			Advertisement main5 = Advertisement.builder()
				.productType(AdvertisementProductType.SAVING_TIME_DEPOSIT)
				.productName("OSB저축은행 정기예금 1년 DB")
				.locationType(AdvertisementLocationType.MAIN)
				.adType(AdvertisementAdType.HANA)
				.riskRating("매우낮은위험") // 위험등급
				.yield("3.60%") // 수익률
				.link("https://www.osb.co.kr/ib20/mnu/HOM00214")
				.build();

			Advertisement community1 = Advertisement.builder()
				.locationType(AdvertisementLocationType.COMMUNITY)
				.adType(AdvertisementAdType.HANA)
				.bannerImageUrl(
					"https://www.youthdaily.co.kr/data/photos/20230728/art_16892990338051_020c14.jpg")
				.link("https://www.hanaw.com/main/main/index.cmd")
				.build();

			Advertisement community2 = Advertisement.builder()
				.locationType(AdvertisementLocationType.COMMUNITY)
				.adType(AdvertisementAdType.EDUCATION)
				.bannerImageUrl(
					"https://file.newswire.co.kr/data/datafile2/thumb_640/2023/08/2041018078_20230823084016_4569338748.jpg")
				.link(
					"https://www.udemy.com/")
				.build();

			Advertisement community3 = Advertisement.builder()
				.locationType(AdvertisementLocationType.COMMUNITY)
				.adType(AdvertisementAdType.HOBBY)
				.bannerImageUrl(
					"https://d2v80xjmx68n4w.cloudfront.net/members/portfolios/6Ioei1713446955.jpg")
				.link("https://www.sssd.co.kr/main")
				.build();

			Advertisement community4 = Advertisement.builder()
				.locationType(AdvertisementLocationType.COMMUNITY)
				.adType(AdvertisementAdType.RETIREMENT)
				.bannerImageUrl(
					"https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F2353343B572016AE34")
				.link("http://www.signumhaus.com/")
				.build();

			Advertisement community5 = Advertisement.builder()
				.locationType(AdvertisementLocationType.COMMUNITY)
				.adType(AdvertisementAdType.TRAVEL)
				.bannerImageUrl(
					"https://cdn.enewstoday.co.kr/news/photo/202111/1522970_578981_4741.jpg")
				.link("https://www.hanatour.com/")
				.build();

			advertisementRepository.saveAll(
				Arrays.asList(main1, main2, main3, main4, main5, community1, community2, community3, community4,
					community5));
		}
	}
}
