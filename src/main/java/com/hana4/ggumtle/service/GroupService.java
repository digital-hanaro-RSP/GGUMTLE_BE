package com.hana4.ggumtle.service;

import java.util.List;

import com.hana4.ggumtle.dto.CreateGroupRequestDto;
import com.hana4.ggumtle.dto.GroupResponseDto;
import com.hana4.ggumtle.dto.JoinGroupRequestDto;
import com.hana4.ggumtle.dto.JoinGroupSuccessResponseDto;

public interface GroupService {
	GroupResponseDto createGroup(CreateGroupRequestDto request) throws Exception;

	List<GroupResponseDto> getAllGroups();

	JoinGroupSuccessResponseDto joinGroup(Long groupId, JoinGroupRequestDto request);
}
