package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="comments")
public class Comment {
    //프라이머리키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //댓글내용
    @Column(nullable = false)
    private String content;
    //생성일
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdDatetime;
    //수정일
    @Column(
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedDatetime;
    //외래키설정 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    //외래키 설정 보드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private boolean isDeleted;


}
