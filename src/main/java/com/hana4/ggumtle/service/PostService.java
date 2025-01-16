package com.hana4.ggumtle.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.groupMember.GroupMember;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.CommentRepository;
import com.hana4.ggumtle.repository.GroupMemberRepository;
import com.hana4.ggumtle.repository.GroupRepository;
import com.hana4.ggumtle.repository.PostLikeRepository;
import com.hana4.ggumtle.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
	private final PostRepository postRepository;
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final PostLikeRepository postLikeRepository;
	private final CommentRepository commentRepository;

	public PostResponseDto.PostInfo save(Long groupId, User user, PostRequestDto.Write postRequestDto) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)), false);
	}

	public PostResponseDto.PostDetail getPost(User user, Long groupId, Long postId) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
			.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

		return PostResponseDto.PostDetail.from(
			postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND)),
			postLikeRepository.findByPostIdAndUserId(postId, user.getId()).isPresent(),
			postLikeRepository.countByPostId(postId), commentRepository.countByPostId(postId));
	}

	public List<PostResponseDto.PostInfo> getPostsByPage(Long groupId, User user, int page) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user)
			.orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

		Pageable pageable = PageRequest.of(page, 10);
		return postRepository.findAll(pageable)
			.map(post -> PostResponseDto.PostInfo.from(post,
				postLikeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent()))
			.getContent();
	}
}