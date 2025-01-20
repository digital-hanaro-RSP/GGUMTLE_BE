package com.hana4.ggumtle.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupRepositoryTest {
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
	}

	@Test
	void findGroupsWithMemberCount_성공() {
		// given
		Group group = new Group();
		group.setName("투자자 모임");
		group.setCategory(GroupCategory.INVESTMENT);
		group.setDescription("투자 관련 정보 공유하는 모임입니다.");
		group.setImageUrl("https://example.com/group-image.jpg");

		Group savedGroup = groupRepository.save(group);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Object[]> result = groupRepository.findGroupsWithFilters(null, null, null, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);

		Object[] firstRow = result.getContent().get(0);
		Group foundGroup = (Group)firstRow[0];
		Long memberCount = (Long)firstRow[1];

		assertThat(foundGroup.getId()).isEqualTo(savedGroup.getId());
		assertThat(foundGroup.getName()).isEqualTo(savedGroup.getName());
		assertThat(memberCount).isEqualTo(0L);
	}

	@Test
	void findGroupsWithFilters_내그룹조회_성공() {
		// given

		User user = new User();
		user.setTel("010-1111-1111");
		user.setName("Test User");
		user.setPassword("password");
		user.setPermission((short)1);
		user.setGender("W");
		user.setRole(UserRole.USER);
		user.setNickname("텔레토비나나");
		user.setBirthDate(LocalDateTime.of(1992, 3, 1, 3, 3, 3, 3));
		user.setProfileImageUrl("https://example.com/group-image.jpg");
		user = userRepository.save(user);

		Group group1 = new Group();
		group1.setName("투자자 모임");
		group1.setCategory(GroupCategory.INVESTMENT);
		group1.setDescription("투자 관련 정보 공유하는 모임입니다.");
		group1.setImageUrl("https://example.com/group-image.jpg");
		group1 = groupRepository.save(group1);

		Group group2 = new Group();
		group2.setName("독서 모임");
		group2.setCategory(GroupCategory.HOBBY);
		group2.setDescription("독서 관련 정보 공유하는 모임입니다.");
		group2.setImageUrl("https://example.com/group-image.jpg");
		group2 = groupRepository.save(group2);

		GroupMember groupMember = GroupMember.builder()
			.user(user)
			.group(group1)
			.build();
		groupMemberRepository.save(groupMember);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Object[]> result = groupRepository.findGroupsWithFilters(user.getId(), null, null, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		Object[] firstRow = result.getContent().get(0);
		Group foundGroup = (Group)firstRow[0];
		Long memberCount = (Long)firstRow[1];
		assertThat(foundGroup.getId()).isEqualTo(group1.getId());
		assertThat(memberCount).isEqualTo(1L);
	}
}
