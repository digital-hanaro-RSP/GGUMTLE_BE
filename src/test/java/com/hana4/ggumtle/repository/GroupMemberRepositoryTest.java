package com.hana4.ggumtle.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class GroupMemberRepositoryTest {
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void beforeEach() {
		groupMemberRepository.deleteAll();
		groupRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void addGroupMember_성공() {
		// given
		User user = User.builder()
			.tel("010-3333-6666")
			.password("password")
			.name("김도희")
			.permission((short)1)
			.birthDate(LocalDateTime.of(1996, 9, 9, 0, 0))
			.gender("W")
			.role(UserRole.USER)
			.profileImageUrl("https://example.com/profile.jpg")
			.nickname("telletobinana")
			.build();
		User savedUser = userRepository.save(user);

		Group group = Group.builder()  // setter 대신 builder 사용
			.name("투자자 모임")
			.category(GroupCategory.INVESTMENT)
			.description("투자 관련 정보 공유하는 모임입니다.")
			.imageUrl("https://example.com/group-image.jpg")
			.build();

		Group savedGroup = groupRepository.save(group);

		GroupMember groupMember = GroupMember.builder()  // setter 대신 builder 사용
			.user(savedUser)
			.group(savedGroup)
			.build();

		// when
		GroupMember savedGroupMember = groupMemberRepository.save(groupMember);

		// then
		assertThat(savedGroupMember.getId()).isNotNull();
		assertThat(savedGroupMember.getUser().getId()).isEqualTo(savedUser.getId());
		assertThat(savedGroupMember.getGroup().getId()).isEqualTo(savedGroup.getId());

		// 추가 검증
		assertThat(groupMemberRepository.existsByGroupAndUser(savedGroup, savedUser)).isTrue();
		assertThat(groupMemberRepository.countByGroup(savedGroup)).isEqualTo(1);
	}
}
