package com.seven.marketclip.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "email_validation")
@Getter
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "user_email")
    private String userEmail;

    @Column(name = "email_token")
    private String emailToken;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Builder
    public Email(String userEmail, String emailToken) {
        this.userEmail = userEmail;
        this.emailToken = emailToken;
        this.expireDate = LocalDateTime.now().plusMinutes(10);
    }

    public Email(EmailDTO emailDTO) {
        this.userEmail = emailDTO.getEmail();
        this.emailToken = emailDTO.getEmailToken();
    }

    // checkExpired가 true면 만료된 것
    public boolean checkExpired(LocalDateTime now){
        return this.expireDate.isBefore(now);
    }

    public void update(LocalDateTime localDateTime, String emailToken){
        this.emailToken = emailToken;
        this.expireDate = localDateTime.plusMinutes(10);
    }

}
