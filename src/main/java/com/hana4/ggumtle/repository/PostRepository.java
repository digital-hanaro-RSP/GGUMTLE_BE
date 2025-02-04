package com.hana4.ggumtle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.post.Post;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findAllByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

	@Query(value = "SELECT p FROM Post p LEFT JOIN PostLike pl "
		+ "ON p = pl.post GROUP BY p ORDER BY COALESCE(COUNT(pl), 0) DESC, p.createdAt DESC",
		countQuery = "SELECT COUNT(DISTINCT p) FROM Post p")
	Page<Post> findAllPostsWithLikeCount(Pageable pageable);

	@Query(value = "SELECT p FROM Post p " +
		"LEFT JOIN PostLike pl ON p = pl.post " +
		"JOIN p.group g " +
		"WHERE g.category = :groupCategory " +
		"GROUP BY p " +
		"ORDER BY COUNT(pl) DESC, p.createdAt DESC",
		countQuery = "SELECT COUNT(DISTINCT p) FROM Post p JOIN p.group g WHERE g.category = :groupCategory")
	Page<Post> findAllPostsGroupedByGroupCategory(Pageable pageable,
		@Param("groupCategory") GroupCategory groupCategory);

	@Query(value = "SELECT p FROM Post p " +
		"LEFT JOIN PostLike pl ON p = pl.post " +
		"WHERE p.content LIKE %:search% " +
		"GROUP BY p " +
		"ORDER BY COUNT(pl) DESC, p.createdAt DESC",
		countQuery = "SELECT COUNT(DISTINCT p) FROM Post p WHERE p.content LIKE %:search%")
	Page<Post> findAllPostsWithSearchParam(Pageable pageable, @Param("search") String search);

	@Query(value = "SELECT p FROM Post p " +
		"LEFT JOIN PostLike pl ON p = pl.post " +
		"JOIN p.group g " +
		"WHERE g.category = :groupCategory " +
		"AND (p.content LIKE %:search% OR p.snapshot LIKE %:search%) " +
		"GROUP BY p " +
		"ORDER BY COUNT(pl) DESC, p.createdAt DESC",
		countQuery = "SELECT COUNT(DISTINCT p) FROM Post p " +
			"JOIN p.group g " +
			"WHERE g.category = :groupCategory " +
			"AND (p.content LIKE %:search% OR p.snapshot LIKE %:search%)")
	Page<Post> findAllPostsGroupedByGroupCategoryWithSearchParam(Pageable pageable, GroupCategory groupCategory,
		String search);
}
