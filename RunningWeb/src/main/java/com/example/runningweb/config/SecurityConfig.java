package com.example.runningweb.config;

import com.example.runningweb.repository.MemberRepository;
import com.example.runningweb.security.CustomLoginSuccessHandler;
import com.example.runningweb.security.MemberAuthenticationProvider;
import com.example.runningweb.security.MemberUserDetail;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    private static final String[] permitAllList = {
//            "/webjars/**", "/favicon.ico", "/chat/**", "/pub/**","/sub/**",  "/ws-stomp",
//            "/main", "/signup", "/login", "/error**", "/logout", "/css/**",
//            "/board/**", "/image/**", "/comment/**", "/resources**", "/static**",
//            "/", "/chat/**", "/ws-stomp/**"
//    };

    private static final String[] permitAllList = {
            "/webjars/**", "/favicon.ico", "/main", "/signup", "/login", "/error**", "/logout", "/css/**",
            "/board/**", "/image/**", "/comment/**", "/resources**", "/static**", "/",
    };

    private static final String[] authenticationList = {
            "/board", "/member/**", "/file/**", "/insertDummyBoard",
            "/chat/**", "/pub/**","/sub/**",  "/ws-stomp", "/ws-stomp/**"
    };

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {


        // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.
        http.headers(
                headerConfig -> headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        http.authorizeHttpRequests(
                request ->{
                    request.requestMatchers(permitAllList).permitAll()
                        .requestMatchers(authenticationList).authenticated();
                }
        );

        http.formLogin(config -> {
            config.defaultSuccessUrl("/main"); //성공시 rediect 페이지
            config.loginPage("/login"); //로그인 홈페이지로 연결
            config.loginProcessingUrl("/login"); // post login 전달 (스프링 시큐리티가 내부적으로 처리)
            config.successHandler(new CustomLoginSuccessHandler("/")); // custom 로그인 성공시 handler 저장
        }).logout(
                config -> {
                    config.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); //모든 logout 메소드에 대해 처리
                    config.logoutSuccessUrl("/main"); //성공시
                    config.invalidateHttpSession(true); // 캐시 삭제
                }
        );

        return http.build();
    }



    @Bean
    PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder();
    }

}
