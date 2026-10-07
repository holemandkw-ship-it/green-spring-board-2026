package com.green.spring_board.exception;


// =========================
// 현재 상태가 올바르지 않을 때 사용하는 예외
// =========================

// 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않다.
// 현재 상태에서는 해당 작업을 수행할 수 없음.
//
// RuntimeException을 상속받아
// InvalidStateException이라는 사용자 정의 예외를 만든다.
//
// 예:
// - 현재 상태에서는 수행할 수 없는 작업을 요청한 경우
public class InvalidStateException extends RuntimeException {

    // 예외를 발생시킬 때 전달한 메시지를 받는다.
    public InvalidStateException(String message) {

        // 부모 클래스인 RuntimeException에게
        // 예외 메시지를 전달한다.
        //
        // 이후 e.getMessage()로 이 메시지를 꺼낼 수 있다.
        super(message);
    }
}