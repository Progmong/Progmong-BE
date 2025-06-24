package com.progmong.api.tag.service;


import com.progmong.api.tag.dto.InterestTagResponseDto;
import com.progmong.api.tag.entity.InterestTag;
import com.progmong.api.tag.entity.Tag;
import com.progmong.api.tag.repository.InterestTagRepository;
import com.progmong.api.tag.repository.TagRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestTagService {

    private final InterestTagRepository interestTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    // 관심 태그 갱신 (PUT)
    @Transactional
    public void updateInterestTags(Long userId, List<Long> updatedTagIds) {
        // 1. 현재 사용자의 관심 태그 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        List<InterestTag> currentInterestTags = interestTagRepository.findByUserId(userId);
        Set<Long> currentTagIds = currentInterestTags.stream()
                .map(interestTag -> interestTag.getTag().getId())
                .collect(Collectors.toSet());

        Set<Long> updatedTagIdSet = new HashSet<>(updatedTagIds);

        // 2. 삭제할 태그 ID: 현재는 있는데 선택 안된 태그
        List<Long> tagsToDelete = currentTagIds.stream()
                .filter(tagId -> !updatedTagIdSet.contains(tagId))
                .toList();

        if (!tagsToDelete.isEmpty()) {
            interestTagRepository.deleteByUserIdAndTagIdIn(userId, tagsToDelete);
        }

        // 3. 추가할 태그 ID: 새롭게 선택된 태그
        List<Long> tagsToAdd = updatedTagIdSet.stream()
                .filter(tagId -> !currentTagIds.contains(tagId))
                .toList();

        for (Long tagId : tagsToAdd) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag not found"));

            InterestTag newInterestTag = InterestTag.builder()
                    .user(user)
                    .tag(tag)
                    .build();

            interestTagRepository.save(newInterestTag);
        }
    }

    // ✅ 관심 태그 조회 (GET)
    @Transactional(readOnly = true)
    public List<InterestTagResponseDto> getUserInterestTags(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        return user.getInterestTags().stream()
                .map(interestTag -> {
                    Tag tag = interestTag.getTag();
                    return new InterestTagResponseDto(tag.getId(), tag.getName());
                })
                .toList();
    }
}
