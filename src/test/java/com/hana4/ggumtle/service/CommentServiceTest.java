package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
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

import com.hana4.ggumtle.dto.comment.CommentRequestDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.model.entity.comment.Comment;
import com.hana4.ggumtle.model.entity.commentLike.CommentLike;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.post.PostType;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.model.entity.user.UserRole;
import com.hana4.ggumtle.repository.CommentLikeRepository;
import com.hana4.ggumtle.repository.CommentRepository;
import com.hana4.ggumtle.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@Mock
	private CommentLikeRepository commentLikeRepository;

	@Mock
	private PostRepository postRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	void countComment() {
		when(commentRepository.countByPostId(1L)).thenReturn(1);
		assertThat(commentService.countCommentByPostId(1L)).isEqualTo(1);
	}

	@Test
	void isAuthorLike() {
		when(commentLikeRepository.findByCommentIdAndUserId(1L, "1")).thenReturn(Optional.of(new CommentLike()));

		assertThat(commentService.isAuthorLike(1L, "1")).isEqualTo(true);
	}

	@Test
	void countLikeByCommentId() {
		when(commentLikeRepository.countByCommentId(1L)).thenReturn(1);

		assertThat(commentService.countLikeByCommentId(1L)).isEqualTo(1);
	}

	@Test
	void saveComment_성공() {
		User user = new User(); // Replace with an actual User object or find it from the database
		Group group = new Group(); // Replace with an actual Group object or find it from the database

		// Create a Post instance
		Post post = Post.builder()
			.user(user)
			.group(group)
			.snapshot("{\"key\": \"value\"}")  // Example snapshot JSON
			.imageUrls("[\"image1.jpg\", \"image2.jpg\"]")  // Example image URLs
			.content("This is a sample post content.")
			.postType(PostType.POST)  // Or any other PostType value
			.build();

		CommentRequestDto.CommentWrite commentWrite = CommentRequestDto.CommentWrite.builder()
			.content("content").build();
		when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
		when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().post(post).user(user).build());
		commentService.saveComment(post.getId(), commentWrite, user);
	}

	@Test
	void saveComment_예외처리() {
		when(postRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(CustomException.class,
			() -> commentService.saveComment(1L, CommentRequestDto.CommentWrite.builder().build(), new User()));
	}

	@Test
	void getCommentsByPage_성공() {
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

		Post post = Post.builder()
			.user(user)
			.group(new Group())
			.snapshot("{\"key\": \"value\"}")  // Example snapshot JSON
			.imageUrls("[\"image1.jpg\", \"image2.jpg\"]")  // Example image URLs
			.content("This is a sample post content.")
			.postType(PostType.POST)  // Or any other PostType value
			.build();

		Comment comment = Comment.builder().id(1L).user(user).content("content").post(post).build();

		Pageable pageable = PageRequest.of(0, 10);
		List<Comment> comments = List.of(comment);
		Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());
		when(postRepository.existsById(post.getId())).thenReturn(true);
		when(commentLikeRepository.findByCommentIdAndUserId(comment.getId(), user.getId())).thenReturn(
			Optional.of(CommentLike.builder().build()));
		when(commentService.countLikeByCommentId(1L)).thenReturn(1);
		when(commentRepository.findAllByPostId(eq(post.getId()), eq(pageable))).thenReturn(commentPage);

		commentService.getCommentsByPage(post.getId(), pageable, user);
	}

	@Test
	void getCommentsByPage_예외처리() {
		when(postRepository.existsById(1L)).thenReturn(false);

		assertThrows(CustomException.class,
			() -> commentService.getCommentsByPage(1L, PageRequest.of(0, 10), new User()));
	}

	@Test
	void deleteComment_성공() {
		when(commentRepository.existsById(1L)).thenReturn(true);

		commentService.deleteComment(1L);
		verify(commentRepository).deleteById(1L);
	}

	@Test
	void deleteComment_예외처리() {
		when(commentRepository.existsById(1L)).thenReturn(false);

		assertThrows(CustomException.class, () -> commentService.deleteComment(1L));
	}

	@Test
	void addLike_성공() {
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
		Comment comment = Comment.builder().id(1L).build();
		CommentLike commentLike = CommentLike.builder().comment(comment).user(user).build();

		when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
		when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(commentLike);

		commentService.addLike(1L, user);

		verify(commentLikeRepository).save(any(CommentLike.class));
	}

	@Test
	void addLike_예외처리() {
		when(commentRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(CustomException.class, () -> commentService.addLike(1L, new User()));
	}

	@Test
	void removeLike_성공() {
		when(commentRepository.existsById(1L)).thenReturn(true);
		when(commentLikeRepository.findByCommentIdAndUserId(1L, "1")).thenReturn(Optional.of(new CommentLike()));

		commentService.removeLike(1L, User.builder().id("1").build());
		verify(commentLikeRepository).delete(any(CommentLike.class));
	}

	@Test
	void removeLike_예외처리() {
		when(commentRepository.existsById(1L)).thenReturn(false);

		assertThrows(CustomException.class, () -> commentService.removeLike(1L, new User()));
	}
}
