package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.myData.MyData;

public interface MyDataRepository extends JpaRepository<MyData, Long> {
	Optional<MyData> findByUserId(String userId);
}
