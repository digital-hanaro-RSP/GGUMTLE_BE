package com.hana4.ggumtle.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hana4.ggumtle.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	void countComment() {
		when(commentRepository.countByPostId(1L)).thenReturn(1);
		assertThat(commentService.countCommentByPostId(1L)).isEqualTo(1);
	}
}
