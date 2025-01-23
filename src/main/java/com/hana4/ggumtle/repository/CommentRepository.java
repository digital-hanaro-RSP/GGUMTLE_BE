package com.hana4.ggumtle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	int countByPostId(Long postId);

	Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
