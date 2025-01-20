package com.hana4.ggumtle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.dto.bucket.BucketResponseDto;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
	private final PostRepository postRepository;
	private final GroupService groupService;
	private final PostLikeService postLikeService;
	private final CommentService commentService;
	private final BucketService bucketService;
	private final GoalPortfolioService goalPortfolioService;
	private final MyDataService myDataService;
	private final ObjectMapper objectMapper;

	private boolean checkUserWithPost(User user, Post post) {
		return !user.getId().equals(post.getUser().getId());
	}

	private boolean checkUserWithGroup(Long groupId, User user) {
		Group group = groupService.getGroup(groupId);
		return groupService.isMatchedGroupUser(user, group);
	}

	public PostResponseDto.PostInfo save(Long groupId, PostRequestDto.Write postRequestDto, User user) throws
		JsonProcessingException {
		Group group = groupService.getGroup(groupId);
		Map<String, Object> snapShotResponse = new HashMap<>();
		List<BucketResponseDto.BriefInfo> bucketList = new ArrayList<>();

		Map<String, Object> snapShot = objectMapper.readValue(postRequestDto.getSnapShot(),
			new TypeReference<>() {
			});

		List<Integer> bucketIds = objectMapper.convertValue(snapShot.get("bucketId"),
			new TypeReference<>() {
			});

		Boolean portfolio = objectMapper.convertValue(snapShot.get("portfolio"), new TypeReference<>() {
		});

		if (bucketIds == null || portfolio == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER, "snapshot 데이터에 문제가 있습니다.");
		}

		for (int bucketId : bucketIds) {
			bucketList.add(BucketResponseDto.BriefInfo.from(bucketService.getBucket((long)bucketId)));
		}

		snapShotResponse.put("bucketLists", bucketList);

		if (portfolio) {
			snapShotResponse.put("goalPortfolio", goalPortfolioService.getGoalPortfolioByUserId(user.getId()));
			snapShotResponse.put("currentPortfolio", myDataService.getMyDataByUserId(user.getId()));
		}

		postRequestDto.setSnapShot(objectMapper.writeValueAsString(snapShotResponse));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)), false);
	}

	public PostResponseDto.PostDetail getPost(Long groupId, Long postId, User user) {
		Post post = getPostById(postId);
		if (!post.getGroup().getId().equals(groupId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "글이 해당 그룹에 있지 않습니다.");
		}
		return PostResponseDto.PostDetail.from(post, postLikeService.isAuthorLike(postId, user.getId()),
			postLikeService.countLikeByPostId(postId), commentService.countCommentByPostId(postId));
	}

	public List<PostResponseDto.PostInfo> getPostsByPage(Long groupId, int page, User user) {
		Pageable pageable = PageRequest.of(page, 10);
		return postRepository.findAllByGroupId(groupId, pageable)
			.map(post -> PostResponseDto.PostInfo.from(post, postLikeService.isAuthorLike(post.getId(), user.getId())))
			.getContent();
	}

	public PostResponseDto.PostInfo updatePost(Long groupId, Long postId, PostRequestDto.Write postRequestDto,
		User user) {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다.");
		}

		Post post = getPostById(postId);
		if (checkUserWithPost(user, post)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "해당 글에 권한이 없습니다.");
		}

		post.setImageUrls(postRequestDto.getImageUrls());
		post.setContent(postRequestDto.getContent());

		return PostResponseDto.PostInfo.from(postRepository.save(post),
			postLikeService.isAuthorLike(post.getId(), user.getId()));
	}

	public void deletePost(Long groupId, Long postId, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다.");
		}

		Post post = getPostById(postId);

		if (checkUserWithPost(user, post)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "해당 글에 권한이 없습니다.");
		}

		postRepository.deleteById(postId);
	}

	public Post getPostById(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 글이 존재하지 않습니다."));
	}
}