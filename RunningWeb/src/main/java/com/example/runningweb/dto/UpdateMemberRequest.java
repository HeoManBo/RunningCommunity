package com.example.runningweb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


// 멤버 업데이트시 필요한 것
// 닉네임, email 수정이 필요 없으면 그냥 폼에 있는거 그대로 보내도 됨.

@Data
@NoArgsConstructor
public class UpdateMemberRequest {

    @NotNull
    @Length(min = 1, max = 8)
    private String nickname;

    @NotNull
    @Email
    private String email;


}
