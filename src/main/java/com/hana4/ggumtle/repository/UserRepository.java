package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.ggumtle.model.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
<<<<<<< HEAD
		Optional<User> findFirstByName(String name);

		Boolean existsUserByTel(String tel);
=======
		Boolean existsUserByTel(String tel);

		Optional<User> findFirstByName(String name);
>>>>>>> d0d0d8a ([feat] group, groupMemer관련api개발 최초커밋)
}
