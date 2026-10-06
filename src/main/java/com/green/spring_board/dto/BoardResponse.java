package com.green.spring_board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardResponse {
    int id ;
    String title;
    String content;
    int hits;
    Integer authorId;
    String authorNickname;
    LocalDateTime createdDatetime;
    LocalDateTime updatedDatetime;
}
