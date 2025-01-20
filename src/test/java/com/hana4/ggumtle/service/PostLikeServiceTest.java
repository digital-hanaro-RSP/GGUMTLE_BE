package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.model.entity.postLike.PostLike;
import com.hana4.ggumtle.repository.PostLikeRepository;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
	@Mock
	private PostLikeRepository postLikeRepository;

	@InjectMocks
	private PostLikeService postLikeService;

	@Test
	void isAuthorLike() {
		when(postLikeRepository.findByPostIdAndUserId(1L, "1")).thenReturn(Optional.of(new PostLike()));

		assertThat(postLikeService.isAuthorLike(1L, "1")).isEqualTo(true);
	}

	@Test
	void countLikeByPostId() {
		when(postLikeRepository.countByPostId(1L)).thenReturn(1);

		assertThat(postLikeService.countLikeByPostId(1L)).isEqualTo(1);
	}
}
