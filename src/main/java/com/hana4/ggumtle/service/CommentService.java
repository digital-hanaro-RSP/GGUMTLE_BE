package com.hana4.ggumtle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.comment.CommentRequestDto;
import com.hana4.ggumtle.dto.comment.CommentResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.CommentRepository;
import com.hana4.ggumtle.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	public int countCommentByPostId(Long postId) {
		return commentRepository.countByPostId(postId);
	}

	public CommentResponseDto.CommentInfo saveComment(Long postId, CommentRequestDto.CommentWrite commentWrite,
		User user) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 글이 존재하지 않습니다."));
		return CommentResponseDto.CommentInfo.from(commentRepository.save(commentWrite.toEntity(post, user)), true);
	}

	public Page<CommentResponseDto.CommentInfo> getCommentsByPage(Long postId, Pageable pageable,
		User user) {
		return commentRepository.findAllByPostId(postId, pageable)
			.map(comment -> CommentResponseDto.CommentInfo.from(comment,
				comment.getUser().getId().equals(user.getId())));
	}

	public void deleteComment(Long commentId) {
		if (!commentRepository.existsById(commentId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 댓글이 존재하지 않습니다.");
		}

		commentRepository.deleteById(commentId);
	}
}
