package com.progmong.api.community.controller;

import com.progmong.api.community.dto.PostDetailResDto;
import com.progmong.api.community.dto.PostListElementResDto;
import com.progmong.api.community.dto.PostWriteDto;
import com.progmong.api.community.service.PostService;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Community", description = "커뮤니티 관련 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@RestController
public class CommunityController {
    private final PostService postService;
     @GetMapping("/post/all")
    @Operation(summary = "게시글 목록 조회", description = "제목,")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 등록 성공"),
    })
    public ResponseEntity<ApiResponse<List<PostListElementResDto>>> all()
    {
        return ApiResponse.success(SuccessStatus.POST_ALL_SUCCESS,postService.findAll());
    }

    @PostMapping("/post/write")
    @Operation(summary = "게시글 등록", description = "제목, 본문을 입력해서 게시글을 등록합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 등록 성공"),
    })
    public ResponseEntity<ApiResponse<PostWriteDto>> postWrite(@RequestBody PostWriteDto postWriteDto,
                                                       @AuthenticationPrincipal SecurityUser securityUser)
    {
        postService.postWrite(securityUser.getId(),postWriteDto);
        return ApiResponse.success(SuccessStatus.POST_WRITE_SUCCESS, postWriteDto);
    }

    @GetMapping("/post/detail/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글id로 조회하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 조회에 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> detail(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

    @GetMapping("/post/modify/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글id로 수정하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 수정이 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> modify(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

    @GetMapping("/post/delete/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글id로 삭제하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 삭제가 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> delete(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

    @GetMapping("/showcase/all")
    @Operation(summary = "자랑하기 목록 조회", description = "제목,")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자랑하기 등록 성공"),
    })
    public ResponseEntity<ApiResponse<List<PostListElementResDto>>> showcaseAll()
    {
        return ApiResponse.success(SuccessStatus.POST_ALL_SUCCESS,postService.findAll());
    }

    @GetMapping("/showcase/detail/{postId}")
    @Operation(summary = "자랑하기 조회", description = "게시글id로 조회하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자랑하기 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "자랑하기 조회에 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> showcaseDetail(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

    @GetMapping("/showcase/modify/{postId}")
    @Operation(summary = "자랑하기 수정", description = "게시글id로 조회하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자랑하기 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "자랑하기 수정이 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> showcaseModify(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

    @GetMapping("/post/delete/{postId}")
    @Operation(summary = "자랑하기 삭제", description = "게시글id로 삭제하시길 바랍니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "자랑하기 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "자랑하기 삭제가 실패했습니다"),
    })
    public ResponseEntity<ApiResponse<PostDetailResDto>> showcaseDelete(@PathVariable Long postId)
    {
        return ApiResponse.success(SuccessStatus.POST_FIND_SUCCESS,postService.findById(postId));
    }

}

//(@RequestBody PostWriteDto postWriteDto, @AuthenticationPrincipal SecurityUser securityUser )