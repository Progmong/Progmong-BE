package com.progmong.api.community.dto;

import com.progmong.api.community.entity.PostCategory;
import com.progmong.common.config.security.SecurityUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

// 게시글 쓰기
@Getter @Setter @ToString @NoArgsConstructor
public class PostWriteDto {
    private String title;
    private String content;
    private PostCategory postCategory;
}

//(@RequestBody PostWriteDto postWriteDto, @AuthenticationPrincipal SecurityUser securityUser )
