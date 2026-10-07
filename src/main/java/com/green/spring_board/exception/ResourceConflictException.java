package com.green.spring_board.exception;


// =========================
// 데이터 충돌 예외
// =========================

// 요청한 작업이 기존 데이터와 충돌할 때 사용하는 사용자 정의 예외
//
// RuntimeException을 상속받아
// ResourceConflictException이라는 새로운 예외를 만든다.
//
// 예:
// - 회원가입할 때 이미 사용 중인 이메일인 경우
public class ResourceConflictException extends RuntimeException {

    // 예외를 발생시킬 때 전달한 메시지를 받는다.
    public ResourceConflictException(String message) {

        // 부모 클래스인 RuntimeException에게
        // 예외 메시지를 전달한다.
        //
        // 이후 e.getMessage()로 이 메시지를 꺼낼 수 있다.
        super(message);
    }
}