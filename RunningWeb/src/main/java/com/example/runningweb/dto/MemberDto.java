package com.example.runningweb.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    @NotEmpty(message = "아이디는 필수입니다.")
    @Size(min = 1, max = 16)
    private String id;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password_check;

    @NotEmpty(message = "닉네임은 필수입니다.")
    @Size(min = 1, max = 8)
    private String nickname;

    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

}
