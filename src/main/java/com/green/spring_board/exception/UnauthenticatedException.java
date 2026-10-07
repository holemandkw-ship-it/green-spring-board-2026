package com.green.spring_board.exception;


// =========================
// 인증 실패 예외
// =========================

// 로그인하지 않았거나 인증 정보가 올바르지 않을 때 사용하는 예외
//
// RuntimeException을 상속받아
// UnauthenticatedException이라는 사용자 정의 예외를 만든다.
//
// 예:
// - 로그인이 필요한 기능을 로그인하지 않고 요청한 경우
// - 로그인 시 비밀번호가 일치하지 않는 경우
public class UnauthenticatedException extends RuntimeException {

    // 예외를 발생시킬 때 전달한 메시지를 받는다.
    public UnauthenticatedException(String message) {

        // 부모 클래스인 RuntimeException에게
        // 예외 메시지를 전달한다.
        //
        // 이후 e.getMessage()로 이 메시지를 꺼낼 수 있다.
        super(message);
    }
}