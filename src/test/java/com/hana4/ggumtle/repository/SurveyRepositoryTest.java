package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.model.entity.survey.Survey;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SurveyRepositoryTest {

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void testFindByUser() {
		// Arrange
		User user = User.builder()
			.tel("010-1234-5678")
			.password("defaultPassword")
			.name("Test User")
			.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
			.gender("m")
			.nickname("TestNickname")
			.role(UserRole.USER)
			.build();
		user = userRepository.save(user);

		Survey survey = Survey.builder()
			.user(user)
			.answers(List.of(1, 2, 3, 4, 5))
			.build();
		surveyRepository.save(survey);

		// Act
		Optional<Survey> foundSurvey = surveyRepository.findByUserId(user.getId());

		// Assert
		assertThat(foundSurvey).isPresent();
		assertThat(foundSurvey.get().getUser().getTel()).isEqualTo("010-1234-5678");
		assertThat(foundSurvey.get().getAnswers()).containsExactly(1, 2, 3, 4, 5);
	}
}
