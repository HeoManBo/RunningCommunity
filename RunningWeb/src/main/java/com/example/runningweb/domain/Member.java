package com.example.runningweb.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_name", length = 16)
    private String username;

    private String password;

    @Column(name = "nickname", length = 16)
    private String nickname;

    @Column(name = "email", length = 50)
    private String email;


    @Builder
    public Member(String userName, String password, String nickname, String email) {
        this.username = userName;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
}
