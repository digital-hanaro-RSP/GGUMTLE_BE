package com.hana4.ggumtle.repository;

import java.awt.print.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hana4.ggumtle.model.entity.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
	//group 조회 시, groupMember Count
	@Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id =:groupId")
	Optional<Object> findAllWithMemberCount(Pageable pageable);
}
