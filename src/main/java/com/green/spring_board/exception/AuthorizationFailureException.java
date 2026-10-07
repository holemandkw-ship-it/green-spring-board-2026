package com.green.spring_board.exception;


// =========================
// 권한이 없을 때 사용하는 예외
// =========================

// 누구인지는 알지만 (인증은 되었지만)
// 해당 작업을 허용하지 않음 (403)
//
// RuntimeException을 상속받아
// AuthorizationFailureException이라는 사용자 정의 예외를 만든다.
//
// 인증(Authentication)
// → 로그인해서 "누구인지"는 확인됨
//
// 인가(Authorization)
// → 하지만 해당 작업을 할 권한이 있는지는 별개의 문제
//
// 예:
// - 다른 사람이 작성한 게시글을 수정하려는 경우
// - 다른 사람이 작성한 게시글을 삭제하려는 경우
public class AuthorizationFailureException extends RuntimeException {

    // 예외를 발생시킬 때 전달한 메시지를 받는다.
    public AuthorizationFailureException(String message) {

        // 부모 클래스인 RuntimeException에게
        // 예외 메시지를 전달한다.
        //
        // 이후 e.getMessage()로 이 메시지를 꺼낼 수 있다.
        super(message);
    }
}