package com.progmong.api.community.service;

import com.progmong.api.community.dto.CommentResDto;
import com.progmong.api.community.entity.Comment;
import com.progmong.api.community.entity.Post;
import com.progmong.api.community.repository.CommentRepository;
import com.progmong.api.community.repository.PostRepository;
import com.progmong.api.pet.dto.UserPetFullDto;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.pet.repository.PetRepository;
import com.progmong.api.pet.repository.UserPetRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserPetRepository userPetRepository;


    @Transactional(readOnly = true)
    public List<CommentResDto> findAllByPostId(Long postId, Long userId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(c -> toDto(c, c.getUser().getId().equals(userId)))
                .collect(Collectors.toList());
    }


    public CommentResDto write(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

        Comment saved = commentRepository.save(comment);
        return toDto(saved, true);
    }

    public boolean modify(Long postId, Long commentId, Long userId, String content) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND.getMessage()));

        // 게시글 ID 불일치 예외
        if (!comment.getPost().getId().equals(postId))
            throw new BadRequestException(ErrorStatus.POST_COMMENT_MISMATCH.getMessage());

        // 작성자 권한 체크
        if (!comment.getUser().getId().equals(userId))
            throw new BadRequestException(ErrorStatus.NO_AUTH_COMMENT.getMessage());

        comment.changeContent(content);      // 엔티티에 setter 대신 도메인 메서드 권장
        return true;
    }

    public boolean delete(Long postId, Long commentId, Long userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.COMMENT_NOT_FOUND.getMessage()));

        if (!comment.getPost().getId().equals(postId))
            throw new BadRequestException(ErrorStatus.POST_COMMENT_MISMATCH.getMessage());

        if (!comment.getUser().getId().equals(userId))
            throw new BadRequestException(ErrorStatus.NO_AUTH_COMMENT.getMessage());

        commentRepository.delete(comment);
        return true;
    }

    public UserPetFullDto getUserPetFullInfo(Long userId) {
        Optional<UserPet> userPet = userPetRepository.findByUserId(userId);
        if (userPet.isEmpty()) {
            return new UserPetFullDto(); // 빈 DTO 반환
        }
        return new UserPetFullDto(userPet.get());
    }

    // DTO로 변환
    private CommentResDto toDto(Comment c) {
        return toDto(c, false);
    }

    // DTO로 변환 ( 댓글 작성자 )
    private CommentResDto toDto(Comment c, boolean isWriter) {

        UserPetFullDto userPetFullDto = getUserPetFullInfo(c.getUser().getId());
        return CommentResDto.builder()
                .id(c.getId())
                .postId(c.getPost().getId())
                .userId(c.getUser().getId())
                .authorName(c.getUser().getNickname())
                .userPet(userPetFullDto)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .isWriter(isWriter)
                .build();
    }
}
