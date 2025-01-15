package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
