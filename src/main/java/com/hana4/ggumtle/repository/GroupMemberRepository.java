package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
	boolean existsByGroupAndUser(Group group, User user);
}
