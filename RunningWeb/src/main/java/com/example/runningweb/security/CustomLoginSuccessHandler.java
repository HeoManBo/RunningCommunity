package com.example.runningweb.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public CustomLoginSuccessHandler(String defaultUrl){
        //Supplies the default target Url that will be used if no saved request is found in the session
        // saved Request 세션이 없는 경우 default로  redirect할 페이지 --> 메인페이지
        // 기본적으로 '/'로 지정되었기 때문에 따로 설정할 필요는 없어보임.
        setDefaultTargetUrl(defaultUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session != null){ //세션이 있으면
            String prevUrl = (String) session.getAttribute("prevUrl");
            if(prevUrl != null){ //이전 페이지로 돌아갈 페이지가 있으면
                session.removeAttribute("prevUrl"); //세션을 지우고
                getRedirectStrategy().sendRedirect(request, response,  prevUrl); //리다이렉션
            }else{  //그 외는 인증
                super.onAuthenticationSuccess(request,response,authentication);
            }
        }else{ //그 외는 인증
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
