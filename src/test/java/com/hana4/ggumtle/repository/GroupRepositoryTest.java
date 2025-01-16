package com.hana4.ggumtle.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupRepositoryTest {
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

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
		Page<Object[]> result = groupRepository.findGroupsWithMemberCount(null, null, pageable);

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
}
