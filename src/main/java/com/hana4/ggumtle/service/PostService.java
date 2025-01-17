package com.hana4.ggumtle.service;

import java.math.BigDecimal;
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
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.myData.MyData;
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
		GroupMember groupMember = groupService.getGroupMember(user, group);
		return true;
	}

	public PostResponseDto.PostInfo save(Long groupId, PostRequestDto.Write postRequestDto, User user) throws
		JsonProcessingException {
		Group group = groupService.getGroup(groupId);

		Map<String, Object> snapShot = objectMapper.readValue(postRequestDto.getSnapShot(),
			new TypeReference<Map<String, Object>>() {
			});
		Map<String, Object> snapShotResponse = new HashMap<>();
		List<Integer> bucketId = (List<Integer>)snapShot.get("bucketId");
		List<Bucket> bucketList = new ArrayList<>();

		for (int bucket : bucketId) {
			bucketList.add(bucketService.getBucket((long)bucket));
		}

		snapShotResponse.put("bucketLists", bucketList);
		if ((Boolean)snapShot.get("portfolio")) {
			snapShotResponse.put("goalPortfolio", goalPortfolioService.getGoalPortfolioByUserId(user.getId()));
			class CurrentPortfolio {
				private final BigDecimal depositWithdrawalRatio;
				private final BigDecimal savingTimeDepositRatio;
				private final BigDecimal investmentRatio;
				private final BigDecimal foreignCurrencyRatio;
				private final BigDecimal pensionRatio;
				private final BigDecimal etcRatio;

				public CurrentPortfolio(com.hana4.ggumtle.model.entity.myData.MyData mydata) {
					BigDecimal sum =
						mydata.getDepositWithdrawal()
							.add(mydata.getSavingTimeDeposit())
							.add(mydata.getInvestment())
							.add(mydata.getForeignCurrency())
							.add(mydata.getPension())
							.add(mydata.getEtc());

					this.depositWithdrawalRatio = mydata.getDepositWithdrawal()
						.divide(sum, 4, BigDecimal.ROUND_HALF_UP);
					this.savingTimeDepositRatio = mydata.getSavingTimeDeposit()
						.divide(sum, 4, BigDecimal.ROUND_HALF_UP);
					this.investmentRatio = mydata.getInvestment().divide(sum, 4, BigDecimal.ROUND_HALF_UP);
					this.foreignCurrencyRatio = mydata.getForeignCurrency().divide(sum, 4, BigDecimal.ROUND_HALF_UP);
					this.pensionRatio = mydata.getPension().divide(sum, 4, BigDecimal.ROUND_HALF_UP);
					this.etcRatio = mydata.getEtc().divide(sum, 4, BigDecimal.ROUND_HALF_UP);
				}
			}
			MyData myData = myDataService.getMyDataByUserId(user.getId());

			snapShotResponse.put("currentPortfolio", new CurrentPortfolio(myData));
		}

		postRequestDto.setSnapShot(objectMapper.writeValueAsString(snapShotResponse));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)), false);
	}

	public PostResponseDto.PostDetail getPost(Long groupId, Long postId, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			return null;
		}

		return PostResponseDto.PostDetail.from(
			getPost(postId),
			postLikeService.isAuthorLike(postId, user.getId()), postLikeService.countLikeByPostId(postId),
			commentService.countCommentByPostId(postId));
	}

	public List<PostResponseDto.PostInfo> getPostsByPage(Long groupId, int page, User user) {
		if (!checkUserWithGroup(groupId, user)) {
			return null;
		}

		Pageable pageable = PageRequest.of(page, 10);
		return postRepository.findAll(pageable)
			.map(post -> PostResponseDto.PostInfo.from(post, postLikeService.isAuthorLike(post.getId(), user.getId())))
			.getContent();
	}

	public PostResponseDto.PostInfo updatePost(Long groupId, Long postId, PostRequestDto.Write postRequestDto,
		User user) {
		if (!checkUserWithGroup(groupId, user)) {
			return null;
		}

		Post post = getPost(postId);
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
			return;
		}

		Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

		if (checkUserWithPost(user, post)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED, "해당 글에 권한이 없습니다.");
		}

		postRepository.deleteById(postId);
	}

	public Post getPost(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 글이 존재하지 않습니다."));
	}
}