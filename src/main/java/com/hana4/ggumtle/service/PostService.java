package com.hana4.ggumtle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.dto.bucket.BucketResponseDto;
import com.hana4.ggumtle.dto.post.PostLikeResponseDto;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.postLike.PostLike;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.PostLikeRepository;
import com.hana4.ggumtle.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	private final GroupService groupService;
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

	private String makeSnapShot(PostRequestDto.Write postRequestDto, User user) throws
		JsonProcessingException {
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

		return objectMapper.writeValueAsString(snapShotResponse);
	}

	public PostResponseDto.PostInfo save(Long groupId, PostRequestDto.Write postRequestDto, User user) throws
		JsonProcessingException {
		Group group = groupService.getGroup(groupId);

		postRequestDto.setSnapShot(makeSnapShot(postRequestDto, user));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)), false, true, 0,
			0);
	}

	public PostResponseDto.PostInfo getPost(Long groupId, Long postId, User user) {
		Post post = getPostById(postId);
		if (!post.getGroup().getId().equals(groupId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "글이 해당 그룹에 있지 않습니다.");
		}
		return PostResponseDto.PostInfo.from(post, isAuthorLike(postId, user.getId()),
			post.getUser().getId().equals(user.getId()), countLikeByPostId(postId),
			commentService.countCommentByPostId(postId));
	}

	public Page<PostResponseDto.PostInfo> getPostsByPage(Long groupId, Pageable pageable, User user) {
		return postRepository.findAllByGroupId(groupId, pageable)
			.map(post -> {
				boolean isLiked = isAuthorLike(post.getId(), user.getId());
				boolean isMine = post.getUser().getId().equals(user.getId());
				return PostResponseDto.PostInfo.from(post, isLiked, isMine, countLikeByPostId(post.getId()),
					commentService.countCommentByPostId(post.getId()));
			});
	}

	public Page<PostResponseDto.PostInfo> getPopularPostsByPage(Pageable pageable, User user) {
		return postRepository.findAllPostsWithLikeCount(pageable)
			.map(post -> {
				boolean isLiked = isAuthorLike(post.getId(), user.getId());
				boolean isMine = post.getUser().getId().equals(user.getId());
				return PostResponseDto.PostInfo.from(post, isLiked, isMine, countLikeByPostId(post.getId()),
					commentService.countCommentByPostId(post.getId()));
			});
	}

	public PostResponseDto.PostInfo updatePost(Long groupId, Long postId, PostRequestDto.Write postRequestDto,
		User user) throws JsonProcessingException {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다.");
		}

		Post post = getPostById(postId);
		if (checkUserWithPost(user, post)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "해당 글에 권한이 없습니다.");
		}

		post.setImageUrls(postRequestDto.getImageUrls());
		post.setContent(postRequestDto.getContent());
		post.setSnapshot(makeSnapShot(postRequestDto, user));

		return PostResponseDto.PostInfo.from(postRepository.save(post), isAuthorLike(post.getId(), user.getId()), true,
			countLikeByPostId(postId), commentService.countCommentByPostId(postId));
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

	public boolean isAuthorLike(Long postId, String userId) {
		return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
	}

	public int countLikeByPostId(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}

	public PostLikeResponseDto.Add addLike(Long groupId, Long postId, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다.");
		}

		return PostLikeResponseDto.Add.from(
			postLikeRepository.save(PostLike.builder().user(user).post(getPostById(postId)).build()));
	}

	public void removeLike(Long groupId, Long postId, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 그룹에 권한이 없습니다.");
		}
		Post post = getPostById(postId);

		postLikeRepository.findByPostIdAndUserId(postId, user.getId()).ifPresent(postLikeRepository::delete);
	}

	public PostResponseDto.ShareInfo saveNews(Long groupId, PostRequestDto.Share share, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "해당 그룹에 권한이 없습니다.");
		}
		Group group = groupService.getGroup(groupId);
		return PostResponseDto.ShareInfo.from(postRepository.save(share.toEntity(user, group)));
	}
}