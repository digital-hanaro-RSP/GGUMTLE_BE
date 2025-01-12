package com.hana4.ggumtle.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GroupResponseDto extends BaseDto{
    private Long id;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private int memberCount;
}
