package com.example.runningweb.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *  개발자가 구현한 UserDetails 빈이 하나 밖에 없다면 AuthenticationProvider를 등록할 필요가 없음
 *  DatAuthenticationProvider가 자동으로 UserDetails를 구현한 빈을 등록함.
 *  UserDetails를 2개이상 구현하는 경우 AuthenticationProvider를 구현해야 함.
 */

//@Component
@RequiredArgsConstructor
@Slf4j
public class MemberAuthenticationProvider implements AuthenticationProvider{

    private static final String ERROR_MESSAGE = "아이디 또는 비밀번호가 일차하지 않습니다";
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    /**
     * MemberUserDetail 에서 가져온 멤버의 아이디와 비밀번호가 DB상에 일치하는지 확인한다.
     */

    @PostConstruct
    public void init(){
        log.info("   등록!\n");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //입력 Form에서 넘어오는 ID와 PASSWORD;
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails loadUser = userDetailsService.loadUserByUsername(username);
        //입력으로 오는 아이디가 없으면 NULL;
        if(loadUser == null){
            throw new BadCredentialsException(ERROR_MESSAGE);
        }

        //비밀번호 미일치
        if(!loadUser.getUsername().equals(passwordEncoder.encode(password))){
            throw new BadCredentialsException(ERROR_MESSAGE);
        }

        return new UsernamePasswordAuthenticationToken(username,  null, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
