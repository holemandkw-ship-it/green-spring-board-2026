package com.green.spring_board.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Email
    @Size(max = 100)
    private String email;

    @Size(min = 1,max = 30)
    private String nickname;

    //@NotNull - null은 허용하지않는다 (빈 문자열 허용)
    //@NotEmpty - 문자열 or 콜렉션이 비어있으면안된다(공백으로 채운 문자열 허용)
    //@NotBlank - not null && not empty 공백으로 채운 문자열 허용 X

    //값 범위 검증
    //@Size(min,max) - 문자열 or 콜렉션의 최소 길이, 최대 길이 검사
    // Size는 길이를 검사할뿐 숫자값의 범위를 검사하는 용도가 아니다
    //@Min,@Max - 숫자값의 최소값,최대값 범위검사
    //@Positive ,@Negative - 숫자값이 양수인지 음수인지 검사
    //@Past, @Future - 날짜 값이 과거인지 미래인지 검사

    //값 형태 검증
    //@Email - 이메일 형식인지 검사
    private int age;
}