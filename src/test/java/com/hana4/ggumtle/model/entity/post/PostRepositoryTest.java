package com.hana4.ggumtle.model.entity.post;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hana4.ggumtle.dto.user.UserRequestDto;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.GroupRepository;
import com.hana4.ggumtle.repository.PostRepository;
import com.hana4.ggumtle.repository.UserRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {
	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	EntityManager em;

	@BeforeEach
	void beforeEach() {
		postRepository.deleteAll();
		userRepository.deleteAll();
		groupRepository.deleteAll();
	}

	@Test
	void addPostTest() {
		List<Post> allPosts = postRepository.findAll();
		System.out.println("allPosts.size() = " + allPosts.size());

		UserRequestDto.Register register = UserRequestDto.Register.builder()
			.name("남인우")
			.tel("01012341234")
			.password("password")
			.birthDate("1990-01-01")
			.gender("M")
			.nickname("inwoo")
			.build();
		User savedUser = userRepository.save(register.toEntity());

		Group group = new Group();

		group.setName("개발자 모임");
		group.setCategory(GroupCategory.TRAVEL);
		group.setDescription("개발 관련 정보와 기술을 공유하는 모임입니다.");
		group.setImageUrl("https://example.com/group-image.jpg");

		Group savedGroup = groupRepository.save(group);

		Post post = new Post();
		post.setUser(savedUser);
		post.setGroup(savedGroup);
		post.setBucketId(1L);
		post.setSnapshot("snapShot");
		post.setImageUrls("https://example.com/image.jpg");
		post.setContent("글 내용");
		post.setPostType(PostType.POST);
		Post savedPost = postRepository.save(post);
		assertThat(savedPost.getId()).isNotNull();
		assertThat(savedPost.getUser().getId()).isEqualTo(savedUser.getId());
		assertThat(savedPost.getContent()).isEqualTo(post.getContent());
	}
}

