package com.hana4.ggumtle.model.entity.post;

import org.springframework.stereotype.Service;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupRepository;
import com.hana4.ggumtle.model.entity.user.User;
import com.hana4.ggumtle.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;

	public PostResponseDto.PostInfo save(String userId, Long groupId, PostRequestDto.Write postRequestDto) {
		Group group = groupRepository.findById(groupId).get();
		User user = userRepository.findById(userId).get();
		return PostResponseDto.PostInfo.from(postRepository.save(postRequestDto.toEntity(user, group)));
	}
}
