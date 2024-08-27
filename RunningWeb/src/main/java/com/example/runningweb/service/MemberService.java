package com.example.runningweb.service;


import com.example.runningweb.domain.Member;
import com.example.runningweb.dto.MemberDto;
import com.example.runningweb.dto.UpdateMemberPasswordRequest;
import com.example.runningweb.dto.UpdateMemberRequest;
import com.example.runningweb.repository.MemberRepository;
import com.example.runningweb.util.RandomEmailCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    private final String SIGNUP_TITLE = "Running App 회원가입 인증 번호입니다.";
    private final String AUTH_CODE_PREFIX = "AUTH_";

    public Long register(MemberDto memberDto) {
        validateExistingMember(memberDto);

        Member member = createMemberFromDto(memberDto);

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private void validateExistingMember(MemberDto memberDto) {
        //중복 체크
        Member duplicatedCheck = memberRepository.findByUsername(memberDto.getId());
        if (duplicatedCheck != null) {
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
        if (findMember.isEmpty()) {
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
        if (!passwordEncoder.matches(updateMemberRequest.getCurrentPassword(), member.getPassword())) {
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

    public void sendCodeToEmail(String key, String email) {
        String code = RandomEmailCodeGenerator.generateCode(RandomEmailCodeGenerator.CODE_LENGTH);
        //mailService.sendMail(SIGNUP_TITLE, code, email);
        log.info("이메일 = {}, 임시번호 = {}", email, code);
        redisTemplate.delete(key + email); //기존 인증번호 삭제
        redisTemplate.opsForValue().set(key + email, code, Duration.ofMinutes(30)); // 30분 지속
    }

    public boolean checkCode(String key, String sendedCode, String email) {
        String savedCode = redisTemplate.opsForValue().get(key + email);
        if (savedCode == null) {
            return false;
        }

        if (!savedCode.equals(sendedCode)) {
            return false; //잘못된 비번
        }

        return true;
    }


    @Transactional(readOnly = true)
    public String findIdByMail(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isEmpty()) {
            return "미 가입한 이메일입니다.";
        }

        return findMember.get().getUsername();
    }

    @Transactional
    public void updateTmpPassword(String email, String tmpPassword) {
        Member member = memberRepository.findByEmail(email).orElseThrow();
        member.updatePassword(passwordEncoder.encode(tmpPassword));
    }
}
