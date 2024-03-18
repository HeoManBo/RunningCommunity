package com.example.runningweb.config;

import com.example.runningweb.repository.MemberRepository;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                   MemberRepository memberRepository) throws Exception {
        //허용 접근 URL
        http.authorizeHttpRequests(
                request ->{
                    request.requestMatchers("/", "/main", "/signup", "/login", "/error").permitAll();
                }
        );

        http.formLogin(config -> {
            config.defaultSuccessUrl("/main");
        }).logout(
                config -> {
                    config.logoutSuccessUrl("/main");
                }
        );


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    PasswordEncoder bcrypt(){
        return new BCryptPasswordEncoder();
    }

}
