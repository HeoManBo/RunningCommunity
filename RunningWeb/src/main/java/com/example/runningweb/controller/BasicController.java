package com.example.runningweb.controller;

import com.example.runningweb.dto.BoardListDto;
import com.example.runningweb.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BasicController {

    private final BoardService boardService;

    @GetMapping("/")
    public String index(){
        return "redirect:/main";
    }
    @GetMapping("/main")
    public String main(Model model,
                       @RequestParam(required = false, defaultValue = "1", name = "page")
                       Integer page){
        if(page < 0){
            throw new IllegalArgumentException("잘못된 게시판 페이지 조회입니다.");
        }

        List<BoardListDto> boardListDtos = boardService.boardList(page);
        model.addAttribute("boardList", boardListDtos);
        return "main";
    }

/*    @GetMapping("/insertDummyBoard")
    public String dummyBoard(){
        boardService.makeDummyBoard();
        return "redirect:/main";
    }*/

}
