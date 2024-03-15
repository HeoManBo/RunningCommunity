package com.example.runningweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //기본 Security 설정 -> 기본은 모두 off
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //일단 모든 URL 접근 가능하게 설정
        http.authorizeHttpRequests(
                request ->
                        request.requestMatchers("/**").permitAll()
        );

        return http.build();
    }

}
