package com.example.runningweb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String title, String text, String toEmail) {
        SimpleMailMessage mail = createMail(title, text, toEmail);
        try {
            mailSender.send(mail);
        } catch (MailException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);
            throw new IllegalArgumentException("메일 전송에 실패했습니다 다시 시도해 주세요");
        }
    }

    private SimpleMailMessage createMail(String title, String text, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(text);
        message.setSubject(title);
        message.setTo(toEmail);
        return message;
    }

}
