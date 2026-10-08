package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


// =========================
// Board Entity
// =========================

// 이 클래스를 JPA가 관리하는 Entity로 지정
// Board 객체와 DB의 boards 테이블을 연결한다.
@Entity

// 실제 DB에서 사용할 테이블 이름
@Table(name = "boards")

// 모든 필드의 Getter / Setter 자동 생성
@Getter
@Setter

// 모든 필드를 매개변수로 받는 생성자 자동 생성
@AllArgsConstructor

// 기본 생성자 자동 생성
@NoArgsConstructor
public class Board {


    // =========================
    // 게시글 번호(PK)
    // =========================

    // boards 테이블의 Primary Key
    @Id

    // DB의 AUTO_INCREMENT를 이용해서
    // id 값을 자동으로 생성한다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // =========================
    // 게시글 제목
    // =========================

    // nullable = false
    // → 제목은 NULL일 수 없다.
    @Column(nullable = false)
    private String title;


    // =========================
    // 게시글 내용
    // =========================

    // nullable = false
    // → 내용은 NULL일 수 없다.
    @Column(nullable = false)
    private String content;


    // =========================
    // 조회수
    // =========================

    // 게시글 조회수를 저장한다.
    //
    // 상세 조회할 때
    // board.setHits(board.getHits() + 1)
    // 을 통해 조회수를 증가시킨다.
    @Column(nullable = false)
    private int hits;


    // =========================
    // 게시글 생성 날짜/시간
    // =========================

    // 게시글이 처음 생성된 시간을 저장한다.
    //
    // insertable = false
    // → INSERT할 때 JPA가 직접 값을 넣지 않는다.
    //
    // updatable = false
    // → UPDATE할 때 JPA가 값을 수정하지 않는다.
    //
    // 즉 생성 시간은 DB에서 관리한다.
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdDatetime;


    // =========================
    // 게시글 수정 날짜/시간
    // =========================

    // 게시글의 마지막 수정 시간을 저장한다.
    //
    // insertable = false
    // → INSERT할 때 JPA가 직접 값을 넣지 않는다.
    //
    // updatable = false
    // → UPDATE할 때 JPA가 직접 값을 수정하지 않는다.
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedDatetime;


    // =========================
    // 게시글 작성자
    // =========================

    // 여러 개의 Board가 하나의 User를 가질 수 있다.
    //
    // 예:
    // 한 명의 유저가 여러 게시글을 작성할 수 있다.
    //
    // Board N : User 1
    //
    // LAZY : 실제 User 정보가 필요한 시점에 조회한다.
    @ManyToOne(fetch = FetchType.LAZY)

    // boards 테이블의 user_id 외래키(FK)와
    // User 객체를 연결한다.
    @JoinColumn(name = "user_id")
    private User user;


    // =========================
    // 좋아요 개수
    // =========================

    // 해당 게시글이 받은 총 좋아요 개수를 저장한다.
    //
    // 좋아요 추가
    // → likeCount + 1
    //
    // 좋아요 취소
    // → likeCount - 1
    //
    // 주의:
    // "내가 좋아요를 눌렀는지"를 저장하는 값이 아니다.
    // 이 게시글이 받은 전체 좋아요 개수이다.
    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private boolean isDeleted;
}