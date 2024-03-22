package com.example.runningweb.controller;

import com.example.runningweb.dto.LoginDto;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("memberDto", new MemberDto());
        return "/signup";
    }

    @PostMapping("/signup")
    public String signUpMember(@Valid @ModelAttribute("memberDto") MemberDto memberDto,
                               BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "signup";
        }

        //비밀번호 확인
        if(!memberDto.getPassword().equals(memberDto.getPassword_check())){
            bindingResult.rejectValue("password_check", "비밀번호와 비밀번호확안이 일차하지 않습니다.");
            return "signup";
        }

        log.info("memberDTO : {}", memberDto);
        try{
            Long saveNumber = memberService.save(memberDto);
        }catch (IllegalArgumentException e){
            bindingResult.rejectValue("id", "아이디는 중복될 수 없습니다.");
            return "signup";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }





}
