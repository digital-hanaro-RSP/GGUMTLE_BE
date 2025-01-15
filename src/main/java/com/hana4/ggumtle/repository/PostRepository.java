package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.ggumtle.model.entity.post.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
