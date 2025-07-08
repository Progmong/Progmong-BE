package com.progmong.api.community.controller;

import com.progmong.api.community.dto.CommentReqDto;
import com.progmong.api.community.dto.CommentResDto;
import com.progmong.api.community.service.CommentService;
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

@Tag(name = "Comment", description = "댓글 관련 API 입니다.")
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/post/{postId}/comments")
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글(postId)의 모든 댓글을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    })
    public ResponseEntity<ApiResponse<List<CommentResDto>>> list(
            @PathVariable Long postId, @AuthenticationPrincipal SecurityUser securityUser) {

        return ApiResponse.success(
                SuccessStatus.COMMENT_ALL_SUCCESS,
                commentService.findAllByPostId(postId, securityUser.getId())
        );
    }

    @PostMapping
    @Operation(summary = "댓글 등록", description = "게시글(postId)에 댓글을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 등록 성공")
    })
    public ResponseEntity<ApiResponse<CommentResDto>> write(
            @PathVariable Long postId,
            @RequestBody CommentReqDto commentReqDto,
            @AuthenticationPrincipal SecurityUser securityUser) {

        CommentResDto res = commentService.write(
                postId,
                securityUser.getId(),
                commentReqDto.getContent()
        );
        return ApiResponse.success(SuccessStatus.COMMENT_WRITE_SUCCESS, res);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글(commentId)을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "댓글 수정 실패")
    })
    public ResponseEntity<ApiResponse<Boolean>> modify(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentReqDto commentReqDto,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean result = commentService.modify(
                postId,
                commentId,
                securityUser.getId(),
                commentReqDto.getContent()
        );
        return ApiResponse.success(SuccessStatus.COMMENT_MODIFY_SUCCESS, result);
    }


    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글(commentId)을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "댓글 삭제 실패")
    })
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean result = commentService.delete(
                postId,
                commentId,
                securityUser.getId()
        );
        return ApiResponse.success(SuccessStatus.COMMENT_DELETE_SUCCESS, result);
    }
}
