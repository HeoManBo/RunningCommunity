package com.example.runningweb.util;

import com.example.runningweb.domain.Member;
import com.example.runningweb.security.MemberUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class Utils {


    // 로그인 중인 회원의 로그인 정보를 추출한다
    public static Member extractLoginMember(Principal principal){
        if(principal instanceof UsernamePasswordAuthenticationToken token){
            if (token.getPrincipal() instanceof MemberUserDetails memberUserDetails) {
                return memberUserDetails.getMember();
            }
        }

        return null;
    }

}
