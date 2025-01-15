package com.hana4.ggumtle.service;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupRepository;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
	private final PostRepository postRepository;
	private final GroupRepository groupRepository;

	public PostResponseDto.PostInfo save(Long groupId, User user, PostRequestDto.Write postRequestDto) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)));
	}
}
