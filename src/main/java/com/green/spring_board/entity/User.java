package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


// =========================
// User Entity
// =========================

// 이 클래스를 JPA가 관리하는 Entity로 지정
// User 객체와 DB의 users 테이블을 연결한다.
@Entity

// 실제 DB에서 사용할 테이블 이름
@Table(name = "users")

// 모든 필드를 매개변수로 받는 생성자를 자동 생성
@AllArgsConstructor

// 기본 생성자를 자동 생성
@NoArgsConstructor

// 모든 필드의 Getter 자동 생성
@Getter

// 모든 필드의 Setter 자동 생성
@Setter
public class User {


    // =========================
    // 유저 번호(PK)
    // =========================

    // 이 필드를 테이블의 Primary Key로 사용
    @Id

    // DB의 AUTO_INCREMENT를 이용해서
    // id 값을 자동으로 생성한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // =========================
    // 닉네임
    // =========================

    // nullable = false
    // DB에서 NULL 값을 허용하지 않는다.
    @Column(nullable = false)
    private String nickname;


    // =========================
    // 이메일
    // =========================

    // nullable = false
    // → NULL 허용 안 함
    //
    // unique = true
    // → 같은 이메일을 중복해서 저장할 수 없음
    @Column(nullable = false, unique = true)
    private String email;


    // =========================
    // 비밀번호
    // =========================

    // NULL 값을 허용하지 않는다.
    //
    // 회원가입할 때 원본 비밀번호를 저장하는 것이 아니라
    // UserService에서 BCrypt로 해싱한 비밀번호를 저장한다.
    @Column(nullable = false)
    private String password;


    // =========================
    // 생성 날짜/시간
    // =========================

    // DB에서 회원이 생성된 시간을 관리한다.
    //
    // insertable = false
    // → JPA가 INSERT 할 때 이 값을 직접 넣지 않는다.
    //
    // updatable = false
    // → JPA가 UPDATE 할 때 이 값을 수정하지 않는다.
    //
    // 즉 생성 시간은 DB에서 관리하도록 한다.
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdDatetime;


    // =========================
    // 수정 날짜/시간
    // =========================

    // DB에서 마지막 수정 시간을 관리한다.
    //
    // insertable = false
    // → INSERT할 때 JPA가 값을 넣지 않음
    //
    // updatable = false
    // → UPDATE할 때 JPA가 값을 직접 수정하지 않음
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedDatetime;
}