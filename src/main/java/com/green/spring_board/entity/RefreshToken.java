package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    // 기본키 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 사용자와 연결 (FK)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 리프레시 토큰
    @Column(name = "token", nullable = false, length = 1024)
    private String token;

    // 토큰 만료 일시
    @Column(name = "expiration_datetime", nullable = false)
    private LocalDateTime expirationDateTime;
}