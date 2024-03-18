package com.example.runningweb.controller;

import com.example.runningweb.dto.LoginDto;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("memberDTO", new MemberDto());
        return "/signup";
    }

    @PostMapping("/signup")
    public String signUpMember(@ModelAttribute MemberDto memberDto){
        log.info("memberDTO : {}", memberDto);
        Long saveNumber = memberService.save(memberDto);

        return "login";
    }

//    @GetMapping("/login")
//    public String login(Model model){
//        model.addAttribute("LoginDto", new LoginDto());
//        return "/login";
//    }





}
