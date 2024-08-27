package com.example.runningweb.controller;

import com.example.runningweb.service.MailService;
import com.example.runningweb.service.MemberService;
import com.example.runningweb.util.RandomEmailCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RestMemberController {

    private final MemberService memberService;
    private final String AUTH_CODE_PREFIX = "AUTH_";
    private final String FIND_ID_KEY = "FIND_ID_";

    private final MailService mailService;


    @GetMapping("/findId/check")
    public Map<String, String> checkCodeForFindId(@RequestParam("email") String email,
                                                  @RequestParam("code") String code){
        boolean isCorrect = memberService.checkCode(FIND_ID_KEY, code, email);
        String id = "잘못된 인증번호입니다.";
        Map<String, String> result = new HashMap<>();
        if (isCorrect) { // 회원가입 되어있으면
            id = memberService.findIdByMail(email);
            result.put("nickname", "찾으시는 아이디는 " + id);
            return result;
        }
        result.put("nickname", id);
        return result;
    }

    @PostMapping("/findId")
    public void sendEmailForFindId(@RequestParam("email") String email) {
        memberService.sendCodeToEmail(FIND_ID_KEY, email);
    }

    @PostMapping("/mail")
    public void sendEmailForSingUp(@RequestParam("email") String email){
        memberService.sendCodeToEmail(AUTH_CODE_PREFIX, email);
    }

    @GetMapping("/mail")
    @ResponseBody
    public Map<String, Boolean> checkEmailVerificationCode(@RequestParam("email") String email,
                                                           @RequestParam("code") String code){
        boolean isCorrectCode = memberService.checkCode(AUTH_CODE_PREFIX, code, email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("verified", isCorrectCode);
        return result;
    }

    @GetMapping("/checkEmail")
    public boolean checkEmail(@RequestParam("email") String email) {
        String idByMail = memberService.findIdByMail(email);
        if (idByMail.equals("미 가입한 이메일입니다.")) {
            return false;
        }
        return true;
    }

    @PostMapping("/findPassword")
    public void reissuePassword(@RequestParam("email") String email) {
        String tmpPassword = RandomEmailCodeGenerator.generateCode(RandomEmailCodeGenerator.PASSWORD_LENGTH);
        memberService.updateTmpPassword(email, tmpPassword);
        log.info("임시 비밀번호 = {}, 재발급 이메일 = {}", tmpPassword, email);
        //mailService.sendMail("RunningApp 임시 비밀번호 입니다.", tmpPassword, email);
    }

}
