package com.hana4.ggumtle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hana4.ggumtle.model.entity.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("SELECT COUNT(*) FROM Comment WHERE id = :postId")
	int countByPostId(Long postId);

	Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
