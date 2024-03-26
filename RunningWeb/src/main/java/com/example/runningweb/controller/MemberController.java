package com.example.runningweb.controller;

import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.*;
import com.example.runningweb.security.MemberUserDetails;
import com.example.runningweb.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
   // private final AuthenticationManager manager;

    //회원가입 폼
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "/signup";
    }

    //회원가입 진행
    @PostMapping("/signup")
    public String signUpMember(@Valid @ModelAttribute("memberDto") MemberDto memberDto,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        //비밀번호 확인
        if (!memberDto.getPassword().equals(memberDto.getPassword_check())) {
            bindingResult.rejectValue("password_check", "비밀번호와 비밀번호확안이 일차하지 않습니다.");
            return "signup";
        }

        log.info("memberDTO : {}", memberDto);
        try {
            Long saveNumber = memberService.register(memberDto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("id", "아이디는 중복될 수 없습니다.");
            return "signup";
        }

        return "redirect:/login";
    }

    //로그인 폼
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("loginDto", new LoginDto());

        //이전 페이지에 대한 요청을 헤더에 저장해 놓음
        String prevUrl = (String) request.getHeader("Referer");
        //만약 이전 페이지의 요청이 있고, 이전 페이지가 /login이 아니라면 이동
        // referer 페이지로 이동, 만약 login 실패하고 성공하면 다시 /login으로 redirect 되기때문에 /login 인 경우는 제외함.
        if (prevUrl != null && !prevUrl.contains("/login")) {
            request.getSession().setAttribute("prevUrl", prevUrl);
        }

        return "login";
    }

    // Update 회원 폼
    @GetMapping("/member/modify/{memberId}")
    public String updateMember(
            @PathVariable(name = "memberId") Long memberId,
            Model model,
            @AuthenticationPrincipal MemberUserDetails memberUserDetails) {

        log.info("memberuserdetails nickname = {} , {}", memberUserDetails.getMember().getNickname()
        ,memberUserDetails.getMember().getEmail());

        UpdateMemberRequest request = new UpdateMemberRequest();
        request.setNickname(memberUserDetails.getMember().getNickname());
        request.setEmail(memberUserDetails.getMember().getEmail());

        model.addAttribute("updateMemberRequest", request);
        return "update";
    }

    // Update : 회원가입 수정
    @PostMapping("/member/modify/{memberId}")
    @PreAuthorize("#memberId.equals(authentication.principal.member.id)")
    public String updateMember(
            @PathVariable(name = "memberId") Long memberId,
            @ModelAttribute @Valid UpdateMemberRequest updateMemberRequest,
            BindingResult bindingResult) {

        log.info("dto info : {}", updateMemberRequest.toString());

        if (bindingResult.hasErrors()) {
            return "update";
        }

        Member updatedMember = memberService.updateMember(memberId, updateMemberRequest);

        // Security Context에 저장되어있는 로그인 정보 수정
        // Security Context에 반영
        // principal에는 MemberUserDetails가 저장되어있으므로 캐스팅 가능
        MemberUserDetails principal = (MemberUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.setMember(updatedMember); //멤버 바꿔치기

        //manager.authenticate()

        return "redirect:/main";
    }

    // 비밀번호 수정 폼
    @GetMapping("/member/modify/password")
    public String updatePasswordForm(Model model){
        model.addAttribute("updateMemberPasswordRequest", new UpdateMemberPasswordRequest());
        return "updatePassword";
    }

    // 비밀번호 수정
    @PostMapping("/member/modify/password/{memberId}")
    @PreAuthorize("#memberId.equals(authentication.principal.member.id)")
    public String updatePassword(@PathVariable(name = "memberId") Long memberId,
                                 @ModelAttribute @Valid UpdateMemberPasswordRequest updateMemberRequest,
                                 BindingResult bindingResult){

        log.info("현재 비밀번호 : {} 새비 : {} 새비체크 : {}", updateMemberRequest.getCurrentPassword(),
                updateMemberRequest.getNewPassword(), updateMemberRequest.getNewPasswordCheck());

        //변경할 비밀번호 확인
        if(!updateMemberRequest.getNewPassword().equals(updateMemberRequest.getNewPasswordCheck())){
            bindingResult.rejectValue("newPassword", "notEqual", "새로운 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            log.info("잘못된 비밀번호 일치");
            return "updatePassword";
        }

        Member member = memberService.updateMemberPassword(memberId, updateMemberRequest);
        if(member == null){ // null 이면 현재 비밀번호가 일치하지 않음.
            bindingResult.rejectValue("currentPassword", "notEqual", "기존 비밀번호가 일치하지 않습니다.");
            log.info("잘못된 현재 비밀빈호 입력");
            return "updatePassword";
        }

        // 개인정보 수정으로 다시 이동
        return "redirect:/member/modify/" + memberId;
    }

    //회원 탈퇴폼
    @GetMapping("/member/withdraw")
    public String memberWithdrawForm(Model model) {
        model.addAttribute("withdrawMemberRequest", new WithdrawMemberRequest());
        return "withdraw";
    }

    //회원탈퇴
    @PostMapping("/member/withdraw")
    public String memberWithdraw(@ModelAttribute WithdrawMemberRequest withdrawMemberRequest,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal MemberUserDetails userDetails) {

        boolean isDeleted = memberService.withdraw(withdrawMemberRequest.getPassword(),
                userDetails.getMember().getId());

        if (!isDeleted) { //비밀번호가 일치하지 않는 경우
            bindingResult.rejectValue("password", "notEqual", "패스워드가 일치하지 않습니다.");
            return "withdraw";
        }

        //성공시 logout진행
        return "redirect:/logout";
    }
}
