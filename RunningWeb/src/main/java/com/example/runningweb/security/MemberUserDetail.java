package com.example.runningweb.security;


import com.example.runningweb.domain.Member;
import com.example.runningweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * DB에서 Username에 대응되는 Member를 가져온다.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberUserDetail implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username);

        if(member == null){
            throw new UsernameNotFoundException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        log.info("username = {}, password = {}", member.getUsername(), member.getPassword());
        return new MemberUserDetails(member);
    }
}
