package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


// =========================
// Like Entity
// =========================

// 이 클래스를 JPA가 관리하는 Entity로 지정
// Like 객체와 DB의 likes 테이블을 연결한다.
@Entity

// 실제 DB에서 사용할 테이블 이름
@Table(name = "likes")

// 모든 필드의 Getter / Setter 자동 생성
@Getter
@Setter

// 기본 생성자 자동 생성
@NoArgsConstructor

// 모든 필드를 매개변수로 받는 생성자 자동 생성
@AllArgsConstructor
public class Like {


    // =========================
    // 좋아요 번호(PK)
    // =========================

    // likes 테이블의 Primary Key
    @Id

    // DB의 AUTO_INCREMENT를 이용해서
    // id 값을 자동으로 생성한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // =========================
    // 좋아요를 누른 유저
    // =========================

    // 여러 개의 Like가 하나의 User를 가질 수 있다.
    //
    // 예:
    // 1번 유저가 여러 게시글에 좋아요를 누르면
    // likes 테이블에는 1번 유저의 좋아요 데이터가 여러 개 생길 수 있다.
    //
    // LAZY : 실제 User 정보가 필요한 시점에 조회한다.
    @ManyToOne(fetch = FetchType.LAZY)

    // likes 테이블의 user_id 외래키(FK)와
    // User 객체를 연결한다.
    //
    // nullable = false
    // → 좋아요에는 반드시 누른 유저가 있어야 한다.
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // =========================
    // 좋아요를 누른 게시글
    // =========================

    // 여러 개의 Like가 하나의 Board를 가질 수 있다.
    //
    // 하나의 게시글에는 여러 사용자가
    // 좋아요를 누를 수 있기 때문이다.
    //
    // LAZY : 실제 Board 정보가 필요한 시점에 조회한다.
    @ManyToOne(fetch = FetchType.LAZY)

    // likes 테이블의 board_id 외래키(FK)와
    // Board 객체를 연결한다.
    //
    // nullable = false
    // → 좋아요에는 반드시 대상 게시글이 있어야 한다.
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;


    // =========================
    // 좋아요 생성 날짜/시간
    // =========================

    // likes 테이블의 created_datetime 컬럼과 연결
    //
    // insertable = false
    // → INSERT할 때 JPA가 직접 값을 넣지 않는다.
    //
    // updatable = false
    // → UPDATE할 때 JPA가 값을 수정하지 않는다.
    //
    // 즉 좋아요를 누른 시간은 DB에서 관리한다.
    @Column(
            name = "created_datetime",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createDatetime;
}