package com.progmong.api.community.service;

import com.progmong.api.community.dto.*;
import com.progmong.api.community.entity.Post;
import com.progmong.api.community.entity.PostCategory;
import com.progmong.api.community.repository.CommentRepository;
import com.progmong.api.community.repository.PostRepository;
import com.progmong.api.community.util.HtmlSanitizer;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;



    @Transactional
    public PostDetailResDto postWrite(Long userId, PostWriteDto postWriteDto) {
        // 저장된 Token을 기준으로
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        Post post = Post.builder()
                .title(postWriteDto.getTitle())
                .content(postWriteDto.getContent())
                .category(postWriteDto.getPostCategory())
                .user(user)
                .build();

        postRepository.save(post);

        log.error(post.getContent());


        return PostDetailResDto.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt()).build();
    }

    @Transactional
    public List<PostListElementResDto> findAll() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostListElementResDto> result = new ArrayList<>();

        for (Post post : posts) {
            result.add(PostListElementResDto.builder()
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getNickname())
                    .postId(post.getId())
                    .title(post.getTitle())
                    .likeCount(post.getLikeCount())
                    .createdAt(post.getCreatedAt())
                    .build());
        }

        return result;
    }

    @Transactional
    public List<PostListElementResDto> findByCategory(PostCategory postCategory){
        List<Post> posts = postRepository.findAllByCategoryOrderByCreatedAtDesc(postCategory);

        List<PostListElementResDto> result = new ArrayList<>();

        for (Post post : posts) {
            result.add(PostListElementResDto.builder()
                    .userId(post.getUser().getId())
                    .nickname(post.getUser().getNickname())
                    .postId(post.getId())
                    .title(post.getTitle())
                    .likeCount(post.getLikeCount())
                    .createdAt(post.getCreatedAt())
                    .build());
        }

        return result;
    }

    @Transactional
    public PostDetailResDto findById(Long userId, Long postId) {
        // postId로 검색
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));

        // 게시자가 아닌 경우 조회 수 count
        if (userId != null && !Objects.equals(userId, post.getUser().getId())) {
            post.setViewCount(post.getViewCount() + 1);
        }

        // html 상에서 js 공격을 막기 위한 코드
        String safeHtml = HtmlSanitizer.sanitize(post.getContent());

        log.error(safeHtml);

        PostDetailResDto result = PostDetailResDto.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .content(safeHtml)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
        return result;
    }

    @Transactional
    public Boolean isWriter(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));

        return post.getUser().getId().equals(userId);

    }

    @Transactional
    public Boolean modify(Long userId, PostModifyDto postModifyDto) {

        // 저장된 Token을 기준으로
        Post post = postRepository.findById(postModifyDto.getPostId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));

        if (!Objects.equals(userId, post.getUser().getId())) {
            log.error("작성자가 아님!");
            throw new BadRequestException(ErrorStatus.BAD_REQUEST.getMessage());
        }

        log.error("수정 시작");
        post.setTitle(postModifyDto.getTitle());
        post.setContent(postModifyDto.getContent());
        return true;

    }

    @Transactional
    public Boolean delete(Long userId, Long postId) {

        // 저장된 Token을 기준으로
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));

        if (!Objects.equals(userId, post.getUser().getId())) {
            log.error("작성자가 아님!");
            throw new BadRequestException(ErrorStatus.BAD_REQUEST.getMessage());
        }

        postRepository.delete(post);
        return true;
    }

    @Transactional
    public CommunityActResDto getActivity(Long userId) {
        Long postCount = postRepository.countByUserId(userId);
        Long commentCount = commentRepository.countByUserId(userId);

        return CommunityActResDto.builder().postCount(postCount).commentCount(commentCount).build();

    }


}
