package com.hana4.ggumtle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.comment.CommentLikeResponseDto;
import com.hana4.ggumtle.dto.comment.CommentRequestDto;
import com.hana4.ggumtle.dto.comment.CommentResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.comment.Comment;
import com.hana4.ggumtle.model.entity.commentLike.CommentLike;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.CommentLikeRepository;
import com.hana4.ggumtle.repository.CommentRepository;
import com.hana4.ggumtle.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final CommentLikeRepository commentLikeRepository;

	public int countCommentByPostId(Long postId) {
		return commentRepository.countByPostId(postId);
	}

	public int countLikeByCommentId(Long commentId) {
		return commentLikeRepository.countByCommentId(commentId);
	}

	public boolean isAuthorLike(Long commentId, String userId) {
		return commentLikeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
	}

	public CommentResponseDto.CommentInfo saveComment(Long postId, CommentRequestDto.CommentWrite commentWrite,
		User user) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 글이 존재하지 않습니다."));
		return CommentResponseDto.CommentInfo.from(commentRepository.save(commentWrite.toEntity(post, user)),
			false, true, 0);
	}

	public Page<CommentResponseDto.CommentInfo> getCommentsByPage(Long postId, Pageable pageable, User user) {
		if (!postRepository.existsById(postId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 글이 존재하지 않습니다.");
		}
		return commentRepository.findAllByPostId(postId, pageable)
			.map(comment -> CommentResponseDto.CommentInfo.from(comment,
				isAuthorLike(comment.getId(), user.getId()), comment.getUser().getId().equals(user.getId()),
				countLikeByCommentId(comment.getId())));
	}

	public void deleteComment(Long commentId) {
		if (!commentRepository.existsById(commentId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 댓글이 존재하지 않습니다.");
		}

		commentRepository.deleteById(commentId);
	}

	public CommentLikeResponseDto.CommentLikeInfo addLike(Long commentId, User user) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 댓글이 존재하지 않습니다."));

		return CommentLikeResponseDto.CommentLikeInfo.from(
			commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build()));
	}

	public void removeLike(Long commentId, User user) {
		if (!commentRepository.existsById(commentId)) {
			throw new CustomException(ErrorCode.NOT_FOUND, "해당 댓글이 존재하지 않습니다.");
		}

		commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId())
			.ifPresent(commentLikeRepository::delete);
	}
}
