package com.progmong.api.community.controller;

import com.progmong.api.community.dto.*;
import com.progmong.api.community.entity.PostCategory;
import com.progmong.api.community.service.PostService;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Community", description = "글쓰기 관련 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@RestController
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping("/post/all")
    @Operation(summary = "게시글 목록 조회", description = "제목,")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
    })
    public ResponseEntity<ApiResponse<List<PostListElementResDto>>> all() {
        return ApiResponse.success(SuccessStatus.POST_ALL_SUCCESS, postService.findAll());
    }

    @GetMapping("{category}/post/all")
    @Operation(summary = "게시글 목록 조회", description = "제목,")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리별 게시글 조회 성공"),
    })
    public ResponseEntity<ApiResponse<List<PostListElementResDto>>> categoryAll(@PathVariable PostCategory category) {
        return ApiResponse.success(SuccessStatus.POST_ALL_SUCCESS, postService.findByCategory(category));
    }

    @PostMapping("/post/write")
    @Operation(summary = "게시글 등록", description = "제목, 본문을 입력해서 게시글을 등록합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 등록 성공"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> postWrite(@RequestBody PostWriteDto postWriteDto,
                                                                   @AuthenticationPrincipal SecurityUser securityUser) {
        PostDetailResDto postDetailResDto = postService.postWrite(securityUser.getId(), postWriteDto);
        log.error(postDetailResDto.toString());
        return ApiResponse.success(SuccessStatus.POST_WRITE_SUCCESS, postDetailResDto);
    }

    @GetMapping("/post/detail/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글id로 조회하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 조회에 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> detail(@AuthenticationPrincipal SecurityUser securityUser, @PathVariable Long postId) {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS, postService.findById(securityUser.getId(), postId));
    }

    @GetMapping("/post/writer/{postId}")
    @Operation(summary = "작성자 인지 확인", description = "게시글id로 조회하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 작성자 찾기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 작성자 조회에 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<Boolean>> isWriter(@PathVariable Long postId,
                                                         @AuthenticationPrincipal SecurityUser securityUser) {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS, postService.isWriter(postId, securityUser.getId()));
    }

    @PostMapping("/post/modify")
    @Operation(summary = "게시글 수정", description = "게시글id로 수정하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 수정이 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<Boolean>> modify(@RequestBody PostModifyDto postModifyDto, @AuthenticationPrincipal SecurityUser securityUser) {
        return ApiResponse.success(SuccessStatus.POST_MODIFY_SUCCESS, postService
                .modify(securityUser.getId(), postModifyDto));
    }

    @GetMapping("/post/delete/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글id로 삭제하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 삭제가 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Long postId, @AuthenticationPrincipal SecurityUser securityUser) {
        return ApiResponse.success(SuccessStatus.POST_DELETED, postService.delete(postId, securityUser.getId()));
    }


    @GetMapping("/post/activity")
    @Operation(summary = "커뮤니티 활동 조회", description = "회원의 게시글 수와 댓글 수를 알 수 있습니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "커뮤니티 활동 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "커뮤니티 활동 조회가 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<CommunityActResDto>> delete(@AuthenticationPrincipal SecurityUser securityUser) {
        return ApiResponse.success(SuccessStatus.COMMUNIY_ACTIVE_SUCCESS, postService.getActivity(securityUser.getId()));
    }

    @GetMapping("/showcase/all")
    @Operation(summary = "자랑하기 목록 조회", description = "제목,")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자랑하기 등록 성공"),
    })
    public ResponseEntity<ApiResponse<List<PostListElementResDto>>> showcaseAll() {
        return ApiResponse.success(SuccessStatus.POST_ALL_SUCCESS, postService.findAll());
    }

}

//(@RequestBody PostWriteDto postWriteDto, @AuthenticationPrincipal SecurityUser securityUser )
