package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.user.User;

public interface UserRepository extends JpaRepository<User, String> {
		Boolean existsUserByTel(String tel);
}
