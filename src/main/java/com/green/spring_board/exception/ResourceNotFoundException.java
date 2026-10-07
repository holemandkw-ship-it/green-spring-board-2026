package com.green.spring_board.exception;


// =========================
// 요청한 데이터를 찾을 수 없을 때 사용하는 예외
// =========================

// DB에서 요청한 데이터가 존재하지 않을 때 사용하는 사용자 정의 예외
//
// RuntimeException을 상속받아
// ResourceNotFoundException이라는 새로운 예외를 만든다.
//
// 예:
// - 요청한 게시글이 존재하지 않는 경우
// - 요청한 유저가 존재하지 않는 경우
// - 로그인할 때 입력한 이메일의 유저가 존재하지 않는 경우
public class ResourceNotFoundException extends RuntimeException {

    // 예외를 발생시킬 때 전달한 메시지를 받는다.
    public ResourceNotFoundException(String message) {

        // 부모 클래스인 RuntimeException에게
        // 예외 메시지를 전달한다.
        //
        // 이후 e.getMessage()로 이 메시지를 꺼낼 수 있다.
        super(message);
    }
}