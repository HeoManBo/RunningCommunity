package com.example.runningweb.service;


import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long save(MemberDto memberDto){
        Member member = Member.builder()
                .userName(memberDto.getId())
                .nickname(memberDto.getNickname())
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }



}
