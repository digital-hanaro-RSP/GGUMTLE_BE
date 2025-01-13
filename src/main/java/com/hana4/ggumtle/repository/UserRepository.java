package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.user.User;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> getUserByTel(String tel);
	Boolean existsUserByTel(String tel);
}
