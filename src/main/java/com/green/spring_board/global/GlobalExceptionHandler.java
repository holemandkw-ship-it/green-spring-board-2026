package com.green.spring_board.global;

import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.InvalidRequestStateException;
import com.green.spring_board.exception.ResourceConflictException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

// 전역 예외 처리기
// 애플리케이션 내 모든 Controller에서 발생(throw)하는 예외를 한 곳에서 공통으로 처리한다.
// 발생한 예외에 맞는 HTTP 상태 코드와 JSON 응답으로 변환하여 클라이언트에게 반환한다.
// Controller마다 반복해서 작성하던 try-catch 예외 처리 코드를 줄일 수 있다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    //@ExceptionHandler(예외 클래스)
    //public 반환형 메서드명(예외 클래스){
    // 어노테이션에 해당하는 예외가 발생했을 때 일괄적으로 처리할 작업내용 }


    // 요청한 데이터, 있어야 할 데이터가 없을 때 공통 처리 - 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException e
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // 인증 정보가 없거나 적절하지 않을 때 공통 처리 - 401
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(
            UnauthenticatedException e
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // Validation 실패 - 400
    // Validator 등 입력 검증 과정에서 문제 발생 시 공통 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(
            MethodArgumentNotValidException e
    ){
        String resultMessage = "";
        List<FieldError> errors = e.getBindingResult().getFieldErrors();

        for (FieldError error : errors) {
            resultMessage = resultMessage
                    + error.getField()
                    + "은(는) "
                    + error.getDefaultMessage()
                    + "\n";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(resultMessage));
    }


    // DB 제약조건 충돌 - 409
    // 고유값이 중복되어 저장에 실패하거나,
    // 존재하지 않는 외래키 이용해 데이터 생성 시도 등 문제 상황 공통 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataConflict(
            DataIntegrityViolationException e
    ){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail("중복되거나 저장할 수 없는 데이터입니다."));
    }


    // 데이터 충돌 - 409
    // 세션 로그인 방식 이메일 중복 방지
    // TODO :: JWT 로그인 구현 후 불필요
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceConflict(
            ResourceConflictException e
    ){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 그 외 모든 서버 에러 - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버에서 오류가 발생했습니다."));
    }

    // 권한 없음 - 403
    @ExceptionHandler(AuthorizationFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationFailure(
            AuthorizationFailureException e
    ){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // 잘못된 요청 상태 - 400
    @ExceptionHandler(InvalidRequestStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequestState(
            InvalidRequestStateException e
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

}