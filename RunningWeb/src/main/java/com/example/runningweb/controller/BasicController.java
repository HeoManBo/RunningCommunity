package com.example.runningweb.controller;

import com.example.runningweb.dto.BoardListDto;
import com.example.runningweb.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String main(Model model){
        List<BoardListDto> boardListDtos = boardService.boardList();
        model.addAttribute("boardList", boardListDtos);
        return "main";
    }

}
