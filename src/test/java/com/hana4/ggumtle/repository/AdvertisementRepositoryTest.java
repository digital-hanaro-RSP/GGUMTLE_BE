package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementProductType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdvertisementRepositoryTest {

	@Autowired
	private AdvertisementRepository advertisementRepository;

	@BeforeEach
	void setUp() {
		advertisementRepository.deleteAll();
	}

	@Test
	void whenFindAllByRiskRatingInOrderByIdDesc_thenReturnSortedAdvertisements() {
		Advertisement ad1 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Investment Product 1")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("5%")
			.link("https://example1.com")
			.build();

		Advertisement ad2 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Investment Product 2")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("7%")
			.link("https://example2.com")
			.build();

		Advertisement ad3 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Investment Product 3")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("6%")
			.link("https://example3.com")
			.build();

		advertisementRepository.saveAll(Arrays.asList(ad1, ad2, ad3));

		List<Advertisement> found = advertisementRepository.findAllByRiskRatingInOrderByIdDesc(
			Arrays.asList("보통위험", "높은위험"));

		assertThat(found).hasSize(3);
		assertThat(found.get(0).getProductName()).isEqualTo("Investment Product 3");
		assertThat(found.get(1).getProductName()).isEqualTo("Investment Product 2");
		assertThat(found.get(2).getProductName()).isEqualTo("Investment Product 1");
	}

	@Test
	void whenFindAllByRiskRatingInOrderByIdDesc_withNonExistentRisk_thenReturnEmpty() {
		List<Advertisement> found = advertisementRepository.findAllByRiskRatingInOrderByIdDesc(
			Arrays.asList("존재하지않는위험"));

		assertThat(found).isEmpty();
	}

	@Test
	void whenFindAllByRiskRatingInOrderByIdDesc_withMultipleRiskRatings_thenReturnMatchingAds() {
		Advertisement ad1 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Low Risk Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("낮은위험")
			.yield("3%")
			.link("https://example1.com")
			.build();

		Advertisement ad2 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Medium Risk Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("5%")
			.link("https://example2.com")
			.build();

		Advertisement ad3 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("High Risk Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("7%")
			.link("https://example3.com")
			.build();

		advertisementRepository.saveAll(Arrays.asList(ad1, ad2, ad3));

		List<Advertisement> found = advertisementRepository.findAllByRiskRatingInOrderByIdDesc(
			Arrays.asList("낮은위험", "높은위험"));

		assertThat(found).hasSize(2);
		assertThat(found.get(0).getProductName()).isEqualTo("High Risk Product");
		assertThat(found.get(1).getProductName()).isEqualTo("Low Risk Product");
	}
}
