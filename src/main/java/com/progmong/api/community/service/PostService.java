package com.progmong.api.community.service;

import com.progmong.api.community.dto.PostDetailResDto;
import com.progmong.api.community.dto.PostListElementResDto;
import com.progmong.api.community.dto.PostWriteDto;
import com.progmong.api.community.entity.Post;
import com.progmong.api.community.repository.PostRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void postWrite(Long userId, PostWriteDto postWriteDto){
        // 저장된 Token을 기준으로
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        Post post = Post.builder()
                .title(postWriteDto.getTitle())
                .content(postWriteDto.getContent())
                .category(postWriteDto.getPostCategory())
                .user(user)
                .build();

        postRepository.save(post);
    }

    @Transactional
    public List<PostListElementResDto> findAll(){
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostListElementResDto> result = new ArrayList<>();

        for(Post post : posts){
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
    public PostDetailResDto findById(Long postId){
        // id로 포스트 검색
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundException(ErrorStatus.POST_NOT_FOUND.getMessage()));

        PostDetailResDto result = PostDetailResDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .build();

        log.error(result.toString());
        return result;
    }
}
