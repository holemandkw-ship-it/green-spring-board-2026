package com.green.spring_board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardUpdateRequest {
    //Board 수정의 경우
    //수정하려는 필드값만 요청에 담아보낸다
    //NotBlank를 붙이면 수정(Patch) api 용도와 다르게
    //모든 필드를
    @Size(min = 10,max = 50)
    private String title;


    @Size(min = 10)
    private String content;

}