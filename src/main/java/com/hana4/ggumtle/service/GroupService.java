package com.hana4.ggumtle.service;

import java.util.List;

import com.hana4.ggumtle.model.dto.CreateGroupRequestDto;
import com.hana4.ggumtle.model.dto.GroupResponseDto;

public interface GroupService {
		GroupResponseDto createGroup(CreateGroupRequestDto request) throws Exception;

		List<GroupResponseDto> getAllGroups();
}
