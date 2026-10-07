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

// log.error() 등의 로그 기능을 사용할 수 있게 해준다.
@Slf4j
public class GlobalExceptionHandler {


    // =========================
    // 요청한 데이터가 없을 때 - 404
    // =========================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            ResourceNotFoundException e
    ) {

        // 발생한 예외 메시지와 예외 정보를 서버 로그에 기록
        log.error(e.getMessage(), e);

        // 클라이언트에게 404 Not Found와
        // 예외에 들어있는 메시지를 반환
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.fail(e.getMessage())
                );
    }


    // =========================
    // 인증 정보가 없거나 적절하지 않을 때 - 401
    // =========================
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(
            UnauthenticatedException e
    ) {

        log.error(e.getMessage(), e);

        // 로그인하지 않았거나 인증에 실패한 경우
        // 401 Unauthorized 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiResponse.fail(e.getMessage())
                );
    }


    // =========================
    // Validation 실패 - 400
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(
            MethodArgumentNotValidException e
    ) {

        log.error(e.getMessage(), e);

        // Validation 오류 메시지들을 합쳐서 저장할 문자열
        String resultMessage = "";

        // @Valid 검사에서 발생한
        // 필드별 오류들을 모두 가져온다.
        List<FieldError> errors =
                e.getBindingResult().getFieldErrors();

        // 발생한 Validation 오류를 하나씩 반복
        for (FieldError error : errors) {

            // 어떤 필드에서 어떤 문제가 발생했는지
            // 하나의 문자열로 합친다.
            resultMessage = resultMessage
                    + error.getField()
                    + "은(는)"
                    + error.getDefaultMessage()
                    + "\n";
        }

        // 잘못된 요청이므로 400 Bad Request 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.fail(resultMessage)
                );
    }


    // =========================
    // DB 제약조건 문제 - 400
    // =========================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataConflict(
            DataIntegrityViolationException e
    ) {

        log.error(e.getMessage(), e);

        // UNIQUE, NOT NULL 등
        // DB 제약조건을 위반했을 때 처리
        //
        // 예:
        // 이미 존재하는 이메일 저장
        // 동일한 user_id + board_id 좋아요 중복 저장
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.fail(
                                "중복되거나 저장할 수 없는 데이터입니다."
                        )
                );
    }


    // =========================
    // 데이터 충돌 - 409
    // =========================
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(
            ResourceConflictException e
    ) {

        log.error(e.getMessage(), e);

        // 요청 자체는 가능하지만
        // 현재 존재하는 데이터와 충돌하는 경우
        //
        // 예:
        // 회원가입할 때 이미 사용 중인 이메일
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ApiResponse.fail(e.getMessage())
                );
    }


    // =========================
    // 그 외 모든 서버 오류 - 500
    // =========================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e
    ) {

        log.error(e.getMessage(), e);

        // 위에서 따로 처리하지 않은 Exception이 발생하면
        // 마지막으로 여기에서 처리한다.
        //
        // 사용자에게 내부 오류 내용을 그대로 보여주지 않고
        // 공통 메시지를 반환한다.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.fail(
                                "서버에서 오류가 발생했습니다."
                        )
                );
    }


    // =========================
    // 인증은 되었지만 해당 작업 권한이 없을 때 - 403
    // =========================
    @ExceptionHandler(AuthorizationFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
            AuthorizationFailureException e
    ) {

        log.error(e.getMessage(), e);

        // 로그인은 되어 있지만 해당 작업을 할 권한이 없는 경우
        //
        // 예:
        // 다른 사람이 작성한 게시글 수정/삭제
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ApiResponse.fail(e.getMessage())
                );
    }


    // =========================
    // 현재 상태에서는 해당 작업을 수행할 수 없을 때 - 400
    // =========================
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            InvalidStateException e
    ) {

        log.error(e.getMessage(), e);

        // 요청은 들어왔지만
        // 현재 데이터 상태에서는 작업할 수 없는 경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.fail(e.getMessage())
                );
    }
}