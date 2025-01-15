package com.hana4.ggumtle.model.entity.post;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.GroupRepository;
import com.hana4.ggumtle.repository.PostRepository;
import com.hana4.ggumtle.service.PostService;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@InjectMocks
	private PostService postService; // 테스트 대상 Service 클래스

	@Mock
	private PostRepository postRepository; // Repository를 Mock 처리

	@Mock
	private GroupRepository groupRepository;

	@Test
	void savePost_성공() {
		// given
		String userId = "1";
		Long groupId = 1L;
		PostResponseDto.PostInfo expectedPostInfo = new PostResponseDto.PostInfo(
			"1", groupId, 1L, null, "imageUrl", "content", PostType.POST
		);

		String imageUrls = "imageUrl";
		String content = "content";
		PostRequestDto.Write write = new PostRequestDto.Write(imageUrls, content, PostType.POST);
		PostResponseDto.PostInfo postInfo = new PostResponseDto.PostInfo("1", 1L, 1L, null, imageUrls, content,
			PostType.POST);

		Post post = new Post();
		User user = new User(
			"1", // id
			"010-1234-5678", // tel
			"password123", // password
			"홍길동", // name
			(short)1, // permission
			LocalDateTime.of(1990, 1, 1, 0, 0, 0, 0), // birthDate
			"M", // gender
			UserRole.USER, // role
			"https://example.com/profile.jpg", // profileImageUrl
			"hgildong" // nickname
		);

		Group group = new Group(
			1L, // id
			"개발자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"개발 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		post.setUser(user);
		post.setGroup(group);
		post.setContent("content");
		when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
		when(postRepository.save(any(Post.class))).thenReturn(post);

		// when
		PostResponseDto.PostInfo result = postService.save(groupId, user, write);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(expectedPostInfo.getUserId());
		assertThat(result.getContent()).isEqualTo(expectedPostInfo.getContent());

		// verify
		verify(postRepository, times(1)).save(any(Post.class));
	}

	@Test
	void save_throwsException_whenGroupNotFound() {
		// given
		Long groupId = 1L;
		String imageUrls = "imageUrl";
		String content = "content";
		User user = new User();
		PostRequestDto.Write postRequestDto = new PostRequestDto.Write(imageUrls, content, PostType.POST);

		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		// when, then
		CustomException exception = assertThrows(CustomException.class, () -> {
			postService.save(groupId, user, postRequestDto);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
	}
}