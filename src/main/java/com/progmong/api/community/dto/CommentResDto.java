package com.progmong.api.community.dto;

import com.progmong.api.pet.dto.UserPetFullDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String authorName;
    private UserPetFullDto userPet;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isWriter;
}
