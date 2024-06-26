package com.example.runningweb.controller;


import com.example.runningweb.dto.BoardDto;
import com.example.runningweb.dto.BoardUpdateRequest;
import com.example.runningweb.dto.BoardViewDto;
import com.example.runningweb.dto.CommentDto;
import com.example.runningweb.security.MemberUserDetail;
import com.example.runningweb.security.MemberUserDetails;
import com.example.runningweb.service.BoardService;
import com.example.runningweb.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileUtil fileUtil;

    // 게시판 폼
    @GetMapping("/board")
    public String board(Model model){
        model.addAttribute("boardDto", new BoardDto());
        return "board";
    }

    // 게시판 등록
    @PostMapping("/board")
    public String registerBoard(@ModelAttribute BoardDto boardDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal MemberUserDetails userDetails){
        if(userDetails == null){ //로그인 안되있는 사람 방지
            return "redirect:/login";
        }

        log.info("LoginUser Info = {}", userDetails.getMember());
        try {
            Long boardId = boardService.createBoard(boardDto, userDetails.getMember());
            redirectAttributes.addAttribute("boardId", boardId);
            return "redirect:/board/{boardId}";
        } catch (Exception e){
            bindingResult.reject("filUploadError", "서버 오류입니다. 다시 시도해 주세요.");
            log.info("error = {} ", e.getMessage());
            return "main";
        }

    }

    // 게시판 조회
    @GetMapping("/board/{boardId}")
    public String viewBoard(@PathVariable("boardId") Long boardId, Model model,
                            @AuthenticationPrincipal MemberUserDetails userDetails) {
        if(userDetails == null){ //비 로그인 상태
            model.addAttribute("loginId", -1);
        }else{ //로그인 상태
            model.addAttribute("loginId", userDetails.getMember().getId());
        }

        BoardViewDto boardViewDto = boardService.findByBoardId(boardId);
        model.addAttribute("boardViewDto", boardViewDto);
        model.addAttribute("commentDto", new CommentDto());
        return "board-view";
    }

    //이미지 보여주기
    @GetMapping("/image/{imageName}")
    @ResponseBody
    public Resource getImage(@PathVariable(name = "imageName") String fileName) throws MalformedURLException {
        log.info("showing image Name : {}", fileName);
        return new UrlResource("file:" + fileUtil.getFullPath(fileName));
    }


    //게시판 수정 폼
    @GetMapping("/board/modify/{boardId}")
    public String modifyBoardForm(@PathVariable(name = "boardId") Long boardId,
                              @AuthenticationPrincipal MemberUserDetails loginInfo,
                              Model model){

        BoardViewDto boardviewDto = boardService.findByBoardId(boardId);

        if(loginInfo == null){ //로그인 안되있는 사람 방지
            return "redirect:/login";
        }

        //본인이 작성한 것이 맞는지 확인
        if(!boardviewDto.getUsername().equals(loginInfo.getUsername())){
            throw new IllegalArgumentException("해당 게시글에 대한 수정 권한이 없습니다.");
        }

        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest();
        boardUpdateRequest.setBoardViewDto(boardviewDto);
        model.addAttribute("boardUpdateRequest", boardUpdateRequest);
        return "modifyBoard";
    }

    //게시판 수정
    @PostMapping("/board/modify/{boardId}")
    public String modifyBoard(@PathVariable(name = "boardId") Long boardId,
                              @AuthenticationPrincipal MemberUserDetails loginInfo,
                                @ModelAttribute BoardUpdateRequest boardUpdateRequest){
        if(loginInfo == null){
            return "redirect:/login";
        }
        //본인이 작성한 것이 맞는지 확인
        if(!boardUpdateRequest.getBoardViewDto().getUsername().equals(loginInfo.getUsername())){
            throw new IllegalArgumentException("해당 게시글에 대한 수정 권한이 없습니다.");
        }

        //업데이트 처리
        boardService.updateBoard(boardUpdateRequest);

        return "redirect:/board/" + boardId; //게시글 다시 조회
    }

    //게시판 삭제 --> JS로
    // 댓글 삭제시 1:N delete 쿼리가 나가는거 같음.... 확인해보기
    @DeleteMapping("/board/modify/{boardId}")
    @ResponseBody
    public ResponseEntity<String> deleteBoard(@PathVariable(name = "boardId") Long boardId,
                                      @AuthenticationPrincipal MemberUserDetails loginInfo){
        try{
            boardService.deleteBoard(boardId, loginInfo.getUsername());
        }catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest()
                    .body(ex.getMessage());
        }

        return ResponseEntity.ok().body("정상적으로 삭제되었습니다.");
    }



}
