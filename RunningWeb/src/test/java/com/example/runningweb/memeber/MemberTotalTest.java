package com.example.runningweb.memeber;

import com.example.runningweb.domain.Member;
import com.example.runningweb.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class 멤버_통합_테스트 {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Spring security setup
    @BeforeEach
    public void before(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void 가입_성공() throws Exception {
        ResultActions result = mockMvc.perform(post("/signup")
                .param("id", "tester")
                .param("password", "1234")
                .param("password_check", "1234")
                .param("nickname", "tester")
                .param("email", "tester@naver.com")
                .with(csrf()));

        //회원가입에 성공
        result.andExpect(status().is3xxRedirection()) //로그인 페이지로 redirection 하므로
                .andExpect(view().name("redirect:/login")); //회원가입 성공시 login으로 이동
    }

    @Test
    public void 유저이름_생성_중복_예외_확인() throws Exception {
        String username = "tester";
        createMockUser(username);

        //새로 생성하려는 유저이름 ==> error 발생
        ResultActions result = mockMvc.perform(post("/signup")
                .param("id", "tester")
                .param("password", "1234")
                .param("password_check", "1234")
                .param("nickname", "tester")
                .param("email", "tester@naver.com")
                .with(csrf()));

        //회원가입에 실패
        result.andExpect(status().isOk())
                .andExpect(view().name("signup")); //회원가입에 실패해서 회원가입 폼에 남아있음.
    }

    @WithMockUser(username = "TESTER", roles = {"USER", "ADMIN"}, password = "1234")
    public void withMockUserTest() throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        System.out.println((String)authentication.getCredentials());
        System.out.println(authentication.getAuthorities().toString());
    }

    @Test
    public void 로그인_성공() throws Exception {
        String username = "tester";
        //회원 가입
        createMockUser(username);

        //로그인 시도
        String successPassword = "1234";

        mockMvc.perform(formLogin()
                .user(username)
                .password(successPassword))
                .andDo(print())
                .andExpect(authenticated()) //인증 되어야함
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void 로그인_실패_비밀번호실패() throws Exception {
        String username = "tester";
        createMockUser(username);

        //로그인 시도 -> 잘못된 비밀번호
        String failPassword = "12345";

        mockMvc.perform(formLogin()
                .user(username)
                .password(failPassword))
                .andDo(print())
                .andExpect(unauthenticated()) ;//인증 실패해야함
    }

    @Test
    public void 로그인_실패_없는회원() throws Exception {
        String username = "tester";
        createMockUser(username);

        //로그인 시도 -> 잘못된 비밀번호
        String failUsername = "testter"; //잘못된 유저이름
        String password = "1234";

        mockMvc.perform(formLogin()
                        .user(failUsername)
                        .password(password))
                .andDo(print())
                .andExpect(unauthenticated()); //인증 실패해야함
    }

    private void createMockUser(String username) {
        Member member = Member.builder()
                .userName(username)
                .password(passwordEncoder.encode("1234"))
                .nickname("test")
                .email("test@test.com")
                .build();
        memberRepository.save(member);
    }


}
