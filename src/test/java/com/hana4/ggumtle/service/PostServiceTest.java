package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.dto.goalPortfolio.GoalPortfolioResponseDto;
import com.hana4.ggumtle.dto.myData.MyDataResponseDto;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.post.PostType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@InjectMocks
	private PostService postService;

	@Mock
	private PostRepository postRepository;

	@Mock
	private GroupService groupService;

	@Mock
	private BucketService bucketService;

	@Mock
	private GoalPortfolioService goalPortfolioService;

	@Mock
	private MyDataService myDataService;

	@Mock
	private PostLikeService postLikeService;

	@Mock
	private CommentService commentService;

	@Mock
	private ObjectMapper objectMapper;

	@Test
	void savePost_포트폴리오없이성공() throws JsonProcessingException {
		// given
		String userId = "1";
		Long groupId = 1L;
		String imageUrls = "imageUrls";
		String content = "content";

		PostResponseDto.PostInfo expectedPostInfo = PostResponseDto.PostInfo.builder()
			.userId(userId)
			.groupId(groupId)
			.snapShot(
				"{\"bucketLists\":[{\"createdAt\":\"2025-01-16T19:34:47.057385\",\"updatedAt\":\"2025-01-16T19:34:47.057385\",\"id\":2,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false},{\"createdAt\":\"2025-01-16T19:35:31.891711\",\"updatedAt\":\"2025-01-16T19:35:31.891711\",\"id\":4,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false},{\"createdAt\":\"2025-01-16T19:35:33.42489\",\"updatedAt\":\"2025-01-16T19:35:33.42489\",\"id\":5,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false}]}")
			.imageUrls(imageUrls)
			.content(content)
			.postType(PostType.POST)
			.build();

		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.imageUrls(imageUrls)
			.content(content)
			.snapShot("{\"bucketId\":[1,2,3],\"portfolio\":false}")
			.postType(PostType.POST)
			.build();

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
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Bucket bucket = Bucket.builder()
			.id(1L) // ID는 보통 DB에서 자동 생성되므로 테스트에서는 생략할 수 있음
			.dreamAccount(null) // DreamAccount 객체
			.user(user) // User 객체
			.title("Save for vacation") // 필수 값
			.tagType(BucketTagType.DO) // 예시: BucketTagType enum
			.dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59)) // 필수값
			.isDueSet(true) // 필수값
			.memo("Save for a trip to Japan") // 선택 값
			.howTo(BucketHowTo.EFFORT) // 예시: BucketHowTo enum
			.goalAmount(new BigDecimal("5000.00")) // 예시 값
			.followers(50L) // 예시 값
			.status(BucketStatus.DOING) // 예시: BucketStatus enum
			.isAutoAllocate(false) // 예시 값
			.allocateAmount(new BigDecimal("1000.00")) // 예시 값
			.cronCycle("0 0 0 * * ?") // 예시 값
			.safeBox(new BigDecimal("1000.00")) // 예시 값
			.isRecommended(true) // 예시 값
			.build();

		post.setUser(user);
		post.setGroup(group);
		post.setContent("content");
		List<Integer> bucketIds = List.of(1, 2, 3);
		Map<String, Object> snapshot = new HashMap<>();
		snapshot.put("bucketId", bucketIds);
		snapshot.put("portfolio", false);

		when(groupService.getGroup(1L)).thenReturn(group);
		when(postRepository.save(any(Post.class))).thenReturn(post);
		when(objectMapper.readValue(eq(write.getSnapShot()), any(TypeReference.class))).thenReturn(snapshot);
		when(objectMapper.convertValue(eq(snapshot.get("bucketId")), any(TypeReference.class))).thenReturn(bucketIds);
		when(objectMapper.convertValue(eq(snapshot.get("portfolio")), any(TypeReference.class))).thenReturn(false);
		when(bucketService.getBucket(eq(1L))).thenReturn(bucket);
		when(bucketService.getBucket(eq(2L))).thenReturn(bucket);
		when(bucketService.getBucket(eq(3L))).thenReturn(bucket);

		// when
		PostResponseDto.PostInfo result = postService.save(groupId, write, user);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(expectedPostInfo.getUserId());
		assertThat(result.getContent()).isEqualTo(expectedPostInfo.getContent());

		// verify
		verify(postRepository, times(1)).save(any(Post.class));
	}

	@Test
	void savePost_포트폴리오성공() throws JsonProcessingException {
		// given
		String userId = "1";
		Long groupId = 1L;
		String imageUrls = "imageUrls";
		String content = "content";

		PostResponseDto.PostInfo expectedPostInfo = PostResponseDto.PostInfo.builder()
			.userId(userId)
			.groupId(groupId)
			.snapShot(
				"{\"bucketLists\":[{\"createdAt\":\"2025-01-16T19:34:47.057385\",\"updatedAt\":\"2025-01-16T19:34:47.057385\",\"id\":2,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false},{\"createdAt\":\"2025-01-16T19:35:31.891711\",\"updatedAt\":\"2025-01-16T19:35:31.891711\",\"id\":4,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false},{\"createdAt\":\"2025-01-16T19:35:33.42489\",\"updatedAt\":\"2025-01-16T19:35:33.42489\",\"id\":5,\"dreamAccount\":{\"createdAt\":\"2025-01-16T19:33:24.542526\",\"updatedAt\":\"2025-01-16T19:33:24.542526\",\"id\":2,\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"balance\":10000.00,\"total\":10000.00},\"user\":{\"createdAt\":\"2025-01-16T19:27:54.168575\",\"updatedAt\":\"2025-01-16T19:27:54.168613\",\"id\":\"099583a9-0add-4f28-9ab7-8a3b74e2d0e4\",\"tel\":\"01012341235\",\"password\":\"$2a$10$aX9/XJDk1BxlaSKKHa961expG2zUh0cvvn9uZkyE1ErWszaW2GbZu\",\"name\":\"문서아\",\"permission\":0,\"birthDate\":\"2000-01-01T00:00:00\",\"gender\":\"f\",\"role\":\"USER\",\"profileImageUrl\":null,\"nickname\":\"익명의고라니\"},\"title\":\"title\",\"tagType\":\"DO\",\"dueDate\":null,\"memo\":null,\"howTo\":null,\"goalAmount\":null,\"followers\":null,\"status\":\"HOLD\",\"allocateAmount\":null,\"cronCycle\":null,\"safeBox\":null,\"autoAllocate\":false,\"recommended\":false,\"dueSet\":false}]}")
			.imageUrls(imageUrls)
			.content(content)
			.postType(PostType.POST)
			.build();

		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.imageUrls(imageUrls)
			.content(content)
			.snapShot("{\"bucketId\":[1,2,3],\"portfolio\":false}")
			.postType(PostType.POST)
			.build();

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
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		Bucket bucket = Bucket.builder()
			.id(1L) // ID는 보통 DB에서 자동 생성되므로 테스트에서는 생략할 수 있음
			.dreamAccount(null) // DreamAccount 객체
			.user(user) // User 객체
			.title("Save for vacation") // 필수 값
			.tagType(BucketTagType.DO) // 예시: BucketTagType enum
			.dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59)) // 필수값
			.isDueSet(true) // 필수값
			.memo("Save for a trip to Japan") // 선택 값
			.howTo(BucketHowTo.EFFORT) // 예시: BucketHowTo enum
			.goalAmount(new BigDecimal("5000.00")) // 예시 값
			.followers(50L) // 예시 값
			.status(BucketStatus.DOING) // 예시: BucketStatus enum
			.isAutoAllocate(false) // 예시 값
			.allocateAmount(new BigDecimal("1000.00")) // 예시 값
			.cronCycle("0 0 0 * * ?") // 예시 값
			.safeBox(new BigDecimal("1000.00")) // 예시 값
			.isRecommended(true) // 예시 값
			.build();

		post.setUser(user);
		post.setGroup(group);
		post.setContent("content");
		List<Integer> bucketIds = List.of(1, 2, 3);
		Map<String, Object> snapshot = new HashMap<>();
		snapshot.put("bucketId", bucketIds);
		snapshot.put("portfolio", true);

		when(groupService.getGroup(1L)).thenReturn(group);
		when(postRepository.save(any(Post.class))).thenReturn(post);
		when(objectMapper.readValue(eq(write.getSnapShot()), any(TypeReference.class))).thenReturn(snapshot);
		when(objectMapper.convertValue(eq(snapshot.get("bucketId")), any(TypeReference.class))).thenReturn(bucketIds);
		when(objectMapper.convertValue(eq(snapshot.get("portfolio")), any(TypeReference.class))).thenReturn(true);
		when(bucketService.getBucket(eq(1L))).thenReturn(bucket);
		when(bucketService.getBucket(eq(2L))).thenReturn(bucket);
		when(bucketService.getBucket(eq(3L))).thenReturn(bucket);
		when(goalPortfolioService.getGoalPortfolioByUserId(user.getId())).thenReturn(
			new GoalPortfolioResponseDto.Ratio(BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
				BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7)));
		when(myDataService.getMyDataByUserId(user.getId())).thenReturn(
			new MyDataResponseDto.CurrentPortfolio(BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
				BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7)));

		// when
		PostResponseDto.PostInfo result = postService.save(groupId, write, user);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(expectedPostInfo.getUserId());
		assertThat(result.getContent()).isEqualTo(expectedPostInfo.getContent());

		// verify
		verify(postRepository, times(1)).save(any(Post.class));
	}

	@Test
	void save_throwsException_스냅샷버켓리스트없음() throws JsonProcessingException {
		Long groupId = 1L;
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		User user = new User();
		PostRequestDto.Write postRequestDto = new PostRequestDto.Write(imageUrls, content, snapShot, PostType.POST);
		Map<String, Object> snapshot = new HashMap<>();
		snapshot.put("bucketId", List.of(1, 2, 3));
		snapshot.put("portfolio", false);

		when(objectMapper.readValue(eq(postRequestDto.getSnapShot()), any(TypeReference.class))).thenReturn(snapshot);
		when(objectMapper.convertValue(eq(snapshot.get("bucketId")), any(TypeReference.class))).thenReturn(null);
		when(objectMapper.convertValue(eq(snapshot.get("portfolio")), any(TypeReference.class))).thenReturn(false);

		assertThrows(CustomException.class, () -> {
			postService.save(groupId, postRequestDto, user);
		});
	}

	@Test
	void save_throwsException_스냅샷포트폴리오없음() throws JsonProcessingException {
		Long groupId = 1L;
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		User user = new User();
		PostRequestDto.Write postRequestDto = new PostRequestDto.Write(imageUrls, content, snapShot, PostType.POST);
		Map<String, Object> snapshot = new HashMap<>();
		snapshot.put("bucketId", List.of(1, 2, 3));
		snapshot.put("portfolio", false);

		when(objectMapper.readValue(eq(postRequestDto.getSnapShot()), any(TypeReference.class))).thenReturn(snapshot);
		when(objectMapper.convertValue(eq(snapshot.get("bucketId")), any(TypeReference.class))).thenReturn(
			List.of(1, 2, 3));
		when(objectMapper.convertValue(eq(snapshot.get("portfolio")), any(TypeReference.class))).thenReturn(null);

		assertThrows(CustomException.class, () -> {
			postService.save(groupId, postRequestDto, user);
		});
	}

	@Test
	void getPost_성공() {
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
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);

		GroupMember groupMember = new GroupMember(1L, group, user);

		Post post = new Post();
		post.setId(1L);
		post.setUser(user);
		post.setGroup(group);
		post.setImageUrls("imageUrls");
		post.setPostType(PostType.POST);
		post.setContent("content");

		PostResponseDto.PostDetail postDetail = PostResponseDto.PostDetail.from(post, false, 0, 0);

		when(postLikeService.isAuthorLike(post.getId(), user.getId())).thenReturn(false);
		when(postLikeService.countLikeByPostId(post.getId())).thenReturn(0);
		when(commentService.countCommentByPostId(post.getId())).thenReturn(0);
		when(postRepository.findById(1L)).thenReturn(Optional.of(post));

		PostResponseDto.PostDetail result = postService.getPost(group.getId(), 1L, user);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(postDetail.getUserId());
		assertThat(result.getContent()).isEqualTo(postDetail.getContent());

		// verify
		verify(postRepository, times(1)).findById(any(Long.class));
	}

	@Test
	void getPost_실패_글이그룹에없음() {
		// Given
		Long groupId = 1L;
		Long postId = 1L;
		User user = new User(); // 적절한 User 객체 생성
		Post post = new Post(); // 적절한 Post 객체 생성
		Group group = new Group();
		group.setId(2L); // 다른 그룹 ID 설정
		post.setGroup(group);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			postService.getPost(groupId, postId, user);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 그룹의 글이 아닙니다.", exception.getMessage());
	}

	@Test
	void getPostsByPage_성공() {
		// Given
		Long groupId = 1L;
		Group group = new Group(groupId, "여행자 모임", GroupCategory.TRAVEL, "설명", "이미지URL");

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

		Post post = new Post();
		post.setId(1L);
		post.setUser(user);
		post.setGroup(group);
		post.setImageUrls("imageUrls");
		post.setPostType(PostType.POST);
		post.setContent("content");

		int page = 0;
		Pageable pageable = PageRequest.of(page, 10);
		List<Post> posts = List.of(post);
		Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

		when(postRepository.findAllByGroupId(groupId, pageable)).thenReturn(postPage);
		when(postLikeService.isAuthorLike(eq(post.getId()), eq(user.getId()))).thenReturn(true);

		// When
		List<PostResponseDto.PostInfo> result = postService.getPostsByPage(groupId, page, user);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		// Add more assertions...

		// verify(groupService).isMatchedGroupUser(eq(user), any(Group.class));
		verify(postRepository).findAllByGroupId(groupId, pageable);
		verify(postLikeService).isAuthorLike(eq(1L), eq("1"));
	}

	@Test
	void updatePost_성공() {
		Long groupId = 1L;
		Group group = new Group(groupId, "여행자 모임", GroupCategory.TRAVEL, "설명", "이미지URL");
		String newContent = "newContent";
		String newImageUrl = "newImageUrl";
		int page = 0;
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

		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.content(newContent)
			.imageUrls(newImageUrl)
			.build();
		Post post = new Post();
		when(groupService.getGroup(eq(1L))).thenReturn(group);
		when(groupService.isMatchedGroupUser(any(User.class), any(Group.class))).thenReturn(true);
		when(postRepository.findById(eq(1L))).thenReturn(Optional.of(post));
		Post newPost = new Post();
		newPost.setImageUrls(newImageUrl);
		post.setContent(newContent);
		when(postLikeService.isAuthorLike(eq(post.getId()), eq(user.getId()))).thenReturn(true);
		when(postRepository.save(post)).thenReturn(post);
		post.setUser(user);
		post.setGroup(group);
		user.setId(post.getUser().getId());
		PostResponseDto.PostInfo postInfo = postService.updatePost(groupId, 1L, write, user);

		assertThat(postInfo.getContent()).isEqualTo(newContent);
		assertThat(postInfo.getImageUrls()).isEqualTo(newImageUrl);
	}

	@Test
	void updatePost_실패_사용자가그룹에없음() {
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
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);
		String newContent = "newContent";
		String newImageUrl = "newImageUrl";
		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.content(newContent)
			.imageUrls(newImageUrl)
			.build();

		when(groupService.getGroup(group.getId())).thenReturn(group);
		when(groupService.isMatchedGroupUser(eq(user), eq(group))).thenReturn(false);

		assertThrows(CustomException.class, () -> {
			postService.updatePost(group.getId(), 1L, write, user);
		});
	}

	@Test
	void updatePost_실패_작성자가아님() {
		Long groupId = 1L;
		Group group = new Group(groupId, "여행자 모임", GroupCategory.TRAVEL, "설명", "이미지URL");
		String newContent = "newContent";
		String newImageUrl = "newImageUrl";
		int page = 0;
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

		Post post = new Post();
		User newUser = new User();
		newUser.setId("2");
		post.setUser(newUser);
		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.content(newContent)
			.imageUrls(newImageUrl)
			.build();
		when(groupService.getGroup(eq(1L))).thenReturn(group);
		when(groupService.isMatchedGroupUser(any(User.class), any(Group.class))).thenReturn(true);
		when(postRepository.findById(eq(1L))).thenReturn(Optional.of(post));

		assertThrows(CustomException.class, () -> {
			postService.updatePost(groupId, 1L, write, user);
		});
	}

	@Test
	void deletePost_성공() {
		Long groupId = 1L;
		Group group = new Group(groupId, "여행자 모임", GroupCategory.TRAVEL, "설명", "이미지URL");
		String newContent = "newContent";
		String newImageUrl = "newImageUrl";
		int page = 0;
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

		Post post = new Post();
		post.setUser(user);
		post.setId(1L);
		when(groupService.getGroup(eq(1L))).thenReturn(group);
		when(groupService.isMatchedGroupUser(any(User.class), any(Group.class))).thenReturn(true);
		when(postRepository.findById(eq(1L))).thenReturn(Optional.of(post));

		postService.deletePost(groupId, post.getId(), user);

		verify(postRepository, times(1)).deleteById(eq(1L));
	}

	@Test
	void deletePost_실패_사용자가그룹에없음() {
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
			"여행자 모임", // name
			GroupCategory.TRAVEL, // category (가정: GroupCategory.TECH)
			"여행 관련 정보와 기술을 공유하는 모임입니다.", // description
			"https://example.com/group-image.jpg" // imageUrl
		);
		when(groupService.getGroup(group.getId())).thenReturn(group);
		when(groupService.isMatchedGroupUser(eq(user), eq(group))).thenReturn(false);

		assertThrows(CustomException.class, () -> {
			postService.deletePost(group.getId(), 1L, user);
		});
	}

	@Test
	void deletePost_실패_작성자가아님() {
		Long groupId = 1L;
		Group group = new Group(groupId, "여행자 모임", GroupCategory.TRAVEL, "설명", "이미지URL");
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

		Post post = new Post();
		User newUser = new User();
		newUser.setId("2");
		post.setUser(newUser);
		when(groupService.getGroup(eq(1L))).thenReturn(group);
		when(groupService.isMatchedGroupUser(any(User.class), any(Group.class))).thenReturn(true);
		when(postRepository.findById(eq(1L))).thenReturn(Optional.of(post));

		assertThrows(CustomException.class, () -> {
			postService.deletePost(groupId, 1L, user);
		});
	}

	@Test
	void getPostById_존재하는경우_포스트반환() {
		// Given
		Long postId = 1L;
		Post expectedPost = new Post();
		expectedPost.setId(postId);
		when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

		// When
		Post result = postService.getPostById(postId);

		// Then
		assertNotNull(result);
		assertEquals(postId, result.getId());
		verify(postRepository).findById(postId);
	}

	@Test
	void getPostById_존재하지않는경우_예외발생() {
		// Given
		Long postId = 1L;
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// When & Then
		CustomException exception = assertThrows(CustomException.class, () -> {
			postService.getPostById(postId);
		});

		assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
		assertEquals("해당 글이 존재하지 않습니다.", exception.getMessage());
		verify(postRepository).findById(postId);
	}
}