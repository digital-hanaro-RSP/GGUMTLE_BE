package com.hana4.ggumtle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.postLike.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
	int countByPostId(Long postId);

	List<PostLike> findByPostId(Long postId);

	Optional<PostLike> findByPostIdAndUserId(Long postId, String userId);
}
