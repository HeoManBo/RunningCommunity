package com.example.runningweb.controller;

import com.example.runningweb.dto.CommentDto;
import com.example.runningweb.security.MemberUserDetails;
import com.example.runningweb.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // boardId에 해당하는 댓글 목록을 가져온다.
    // 비동기형 처리로 가져오므로 ResponseBody를 붙이자.
    @GetMapping("/comment/{boardId}")
    @ResponseBody
    public List<CommentDto> commentList(@PathVariable(name = "boardId") Long boardId){
        return commentService.commentDtos(boardId);
    }

    //비동기형으로 댓글 등록
    @ResponseBody
    @PostMapping("/comment/{boardId}")
    public ResponseEntity<Void> makeComment(@PathVariable("boardId") Long boardId,
                                              @RequestBody CommentDto commentDto,
                                              @AuthenticationPrincipal MemberUserDetails memberUserDetails){

        log.info("댓글 내용 : {}", commentDto.getContent());
        checkLoginUser(memberUserDetails);
        log.info("댓글 내용 : {}", commentDto.getContent());

        Long result = commentService.createComment(commentDto, boardId, memberUserDetails.getMember());
        return ResponseEntity.ok().build();
    }

    //댓글 삭제
    //비동기형으로 처리
    @DeleteMapping("/comment/{commentId}")
    @ResponseBody
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @AuthenticationPrincipal MemberUserDetails memberUserDetails){
        checkLoginUser(memberUserDetails);

        commentService.deleteComment(commentId, memberUserDetails.getMember());
        return ResponseEntity.ok().build();
    }

    // 로그인한 유저인지 확인한다.
    private void checkLoginUser(MemberUserDetails memberUserDetails) {
        if(memberUserDetails == null){ // API 호출 방지
            throw new IllegalArgumentException("비회원은 댓글을 달 수 없습니다.");
        }
    }

    @ResponseBody
    @GetMapping("/comment/test")
    public List<CommentDto> getTest(@RequestParam(name = "boardId") Long boardId) {
        return commentService.getCommentsWithHier(boardId);
    }

}
