package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
	void whenFindFirstByRiskRating_thenReturnAdvertisement() {
		// Arrange
		Advertisement ad = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Test Investment Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("보통위험")
			.yield("5%")
			.link("https://example.com")
			.build();

		advertisementRepository.save(ad);

		// Act
		Optional<Advertisement> found = advertisementRepository.findFirstByRiskRating("보통위험");

		// Assert
		assertThat(found).isPresent();
		assertThat(found.get().getProductName()).isEqualTo("Test Investment Product");
		assertThat(found.get().getRiskRating()).isEqualTo("보통위험");
	}

	@Test
	void whenFindFirstByRiskRating_withNonExistentRisk_thenReturnEmpty() {
		// Act
		Optional<Advertisement> found = advertisementRepository.findFirstByRiskRating("존재하지않는위험");

		// Assert
		assertThat(found).isEmpty();
	}

	@Test
	void whenMultipleAdsWithSameRiskRating_thenReturnFirst() {
		// Arrange
		Advertisement ad1 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("First Investment Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("7%")
			.link("https://example1.com")
			.build();

		Advertisement ad2 = Advertisement.builder()
			.productType(AdvertisementProductType.INVESTMENT)
			.productName("Second Investment Product")
			.locationType(AdvertisementLocationType.MAIN)
			.riskRating("높은위험")
			.yield("8%")
			.link("https://example2.com")
			.build();

		advertisementRepository.save(ad1);
		advertisementRepository.save(ad2);

		// Act
		Optional<Advertisement> found = advertisementRepository.findFirstByRiskRating("높은위험");

		// Assert
		assertThat(found).isPresent();
		assertThat(found.get().getProductName()).isEqualTo("First Investment Product");
	}
}
