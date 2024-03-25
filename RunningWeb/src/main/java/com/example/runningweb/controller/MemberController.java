package com.example.runningweb.controller;

import com.example.runningweb.dto.LoginDto;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String login(Model model, HttpServletRequest request){
        model.addAttribute("loginDto", new LoginDto());

        //이전 페이지에 대한 요청을 헤더에 저장해 놓음
        String prevUrl =  (String)request.getHeader("Referer");
        //만약 이전 페이지의 요청이 있고, 이전 페이지가 /login이 아니라면 이동
        // referer 페이지로 이동, 만약 login 실패하고 성공하면 다시 /login으로 redirect 되기때문에 /login 인 경우는 제외함.
        if(prevUrl != null && !prevUrl.contains("/login")){
            request.getSession().setAttribute("prevUrl", prevUrl);
        }


        return "login";
    }





}
