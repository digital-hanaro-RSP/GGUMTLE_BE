package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {
	private final PostLikeRepository postLikeRepository;

	public boolean isAuthorLike(Long postId, String userId) {
		return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
	}

	public int countLikeByPostId(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}
}
