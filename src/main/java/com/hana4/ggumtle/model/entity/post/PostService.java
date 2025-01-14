package com.hana4.ggumtle.model.entity.post;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.global.error.CustomException;
import com.hana4.ggumtle.global.error.ErrorCode;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupRepository;
import com.hana4.ggumtle.model.entity.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final GroupRepository groupRepository;

	public PostResponseDto.PostInfo save(Long groupId, User user, PostRequestDto.Write postRequestDto) {
		Group group = groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)));
	}
}
