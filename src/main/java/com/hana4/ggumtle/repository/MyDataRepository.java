package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.myData.MyData;
import com.hana4.ggumtle.model.entity.user.User;

public interface MyDataRepository extends JpaRepository<MyData, Long> {
}
