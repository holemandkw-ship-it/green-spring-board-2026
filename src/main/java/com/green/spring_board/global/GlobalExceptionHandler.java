package com.green.spring_board.global;

import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


// 전역 예외 처리기
// 애플리케이션 내 모든 Controller에서 발생하는 예외를
// 한 곳에서 공통으로 처리한다.
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // 요청한 데이터가 없을 때 - 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // 인증 정보가 없거나 적절하지 않을 때 - 401
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(
            UnauthenticatedException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // Validation 실패 - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(
            MethodArgumentNotValidException e
    ) {
        log.error(e.getMessage(), e);

        String resultMessage = "";

        List<FieldError> errors =
                e.getBindingResult().getFieldErrors();

        for (FieldError error : errors) {
            resultMessage = resultMessage
                    + error.getField()
                    + "은(는)"
                    + error.getDefaultMessage()
                    + "\n";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(resultMessage));
    }


    // DB 제약조건 문제 - 400
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataConflict(
            DataIntegrityViolationException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.fail(
                                "중복되거나 저장할 수 없는 데이터입니다."
                        )
                );
    }


    // 데이터 충돌 - 409
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            ResourceConflictException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // 그 외 모든 서버 오류 - 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.fail(
                                "서버에서 오류가 발생했습니다."
                        )
                );
    }


    // 인증은 되었지만 해당 작업 권한이 없을 때 - 403
    @ExceptionHandler(AuthorizationFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
            AuthorizationFailureException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(e.getMessage()));
    }


    // 현재 상태에서는 해당 작업을 수행할 수 없을 때 - 400
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            InvalidStateException e
    ) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }
}