package com.example.runningweb.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UpdateMemberPasswordRequest {

    @NotEmpty
    private String currentPassword; //현재 비밀번호

    @NotEmpty
    private String newPassword; // 새로운 비밀번호

    @NotEmpty
    private String newPasswordCheck; // 새로운 비밀번호 확인


}
