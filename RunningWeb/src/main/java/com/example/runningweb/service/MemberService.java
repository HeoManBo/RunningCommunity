package com.example.runningweb.service;


import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.dto.UpdateMemberPasswordRequest;
import com.example.runningweb.dto.UpdateMemberRequest;
import com.example.runningweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long register(MemberDto memberDto){
        validateExistingMember(memberDto);

        Member member = createMemberFromDto(memberDto);

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private void validateExistingMember(MemberDto memberDto) {
        //중복 체크
        Member duplicatedCheck = memberRepository.findByUsername(memberDto.getId());
        if(duplicatedCheck != null){
            throw new IllegalArgumentException("중복되는 아이디 입니다.");
        }
    }

    private Member createMemberFromDto(MemberDto memberDto) {
        Member member = Member.builder()
                .userName(memberDto.getId())
                .nickname(memberDto.getNickname())
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();
        return member;
    }


    @Transactional
    public Member updateMember(Long memberId, UpdateMemberRequest updateMemberRequest) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if(findMember.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member member = findMember.get();
        member.updateNickname(updateMemberRequest.getNickname());
        member.updateEmail(updateMemberRequest.getEmail());
        return member;
    }

    @Transactional
    public Member updateMemberPassword(Long memberId, UpdateMemberPasswordRequest updateMemberRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));


        //입력한 비밀번호와 DB 비밀번호가 일치하지 않으면
        if(!passwordEncoder.matches(updateMemberRequest.getCurrentPassword(), member.getPassword())){
            return null; // error 처리
        }

        member.updatePassword(passwordEncoder.encode(updateMemberRequest.getNewPassword()));

        return member;
    }

    public boolean withdraw(String password, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        //입력한 비밀빈호 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            return false;
        }

        //회원가입 탈퇴 진행 --> soft delete로 DB 삭제는 진행 X
        member.withdraw();
        return true;
    }
}
