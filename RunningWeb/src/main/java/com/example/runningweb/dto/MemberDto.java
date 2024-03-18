package com.example.runningweb.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private String id;
    private String password;
    private String nickname;
    private String email;

}
