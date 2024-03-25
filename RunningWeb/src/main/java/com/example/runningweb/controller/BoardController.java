package com.example.runningweb.controller;


import com.example.runningweb.dto.BoardDto;
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

    @GetMapping("/board")
    public String board(Model model){
        model.addAttribute("boardDto", new BoardDto());
        return "board";
    }

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


    @GetMapping("/image/{imageName}")
    @ResponseBody
    public Resource getImage(@PathVariable(name = "imageName") String fileName) throws MalformedURLException {
        log.info("showing image Name : {}", fileName);
        return new UrlResource("file:" + fileUtil.getFullPath(fileName));
    }

}
