package com.hana4.ggumtle.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	void testFindUserByTel() {
		User user = User.builder()
			.tel("010-1234-5678")
			.password("defaultPassword")
			.name("Test User")
			.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
			.gender("m")
			.nickname("TestNickname")
			.role(UserRole.USER)
			.build();

		userRepository.save(user);

		Optional<User> foundUser = userRepository.findUserByTel("010-1234-5678");

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getName()).isEqualTo("Test User");
	}

	@Test
	void testExistsUserByTel_True() {
		User user = User.builder()
			.tel("010-1234-5678")
			.password("defaultPassword")
			.name("Test User")
			.birthDate(LocalDateTime.of(1990, 5, 20, 0, 0))
			.gender("m")
			.nickname("TestNickname")
			.role(UserRole.USER)
			.build();

		userRepository.save(user);

		boolean exists = userRepository.existsUserByTel("010-1234-5678");

		assertThat(exists).isTrue();
	}

	@Test
	void testExistsUserByTel_False() {
		// Act
		boolean exists = userRepository.existsUserByTel("010-9876-5432");

		// Assert
		assertThat(exists).isFalse();
	}
}
