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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                   MemberRepository memberRepository) throws Exception {
        //허용 접근 URL
        http.authorizeHttpRequests(
                request ->{
                    request.requestMatchers("/", "/main", "/signup",
                            "/login", "/error", "/logout", "/css/**", "/board/**", "/image/**", "/comment/**" ).permitAll();
                    request.requestMatchers("/board").authenticated(); //작성은 인증해야 함.
                    request.requestMatchers("/member/**").authenticated(); //멤버 수정작업은 인증이 필요함
                    request.requestMatchers("/file/**").authenticated(); //파일 삭제는 로그인한사람만 가능
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
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/resources/**", "/static/**", "/webjars/**");
    }


    @Bean
    PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder();
    }

}
