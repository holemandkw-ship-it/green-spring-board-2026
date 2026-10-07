package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

// User 관련 API의 기본 주소
// 모든 API 앞에 /api/user가 붙는다.
@RequestMapping("/api/user")

// 생성자를 자동으로 만들어 필요한 객체를 주입받는다.
@AllArgsConstructor
public class UserController {

    // 회원가입, 로그인, 내 정보 조회/수정/삭제 기능 처리
    private final UserService userService;

    private final UserRepository userRepository;
    private final BoardService boardService;


    // =========================
    // 회원가입
    // POST /api/user/signup
    // =========================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            // 요청 JSON을 SignupRequest 객체로 변환
            // @Valid : DTO에 설정한 유효성 검사 실행
            @Valid @RequestBody SignupRequest signupRequest
    ) {

        // 회원가입 처리는 UserService에 맡긴다.
        userService.signup(signupRequest);

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 로그인
    // POST /api/user/login
    // =========================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            // 로그인 요청으로 email, password 등을 받는다.
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest
    ) {

        // 1. UserService에서 이메일/비밀번호 확인
        // 로그인 성공 시 해당 유저의 id를 반환받는다.
        int userId = userService.login(loginRequest);

        // 2. 세션 가져오기
        // getSession() : 세션이 없으면 새로운 세션을 생성한다.
        HttpSession session = httpServletRequest.getSession();

        // 3. 로그인 성공 후 세션 ID 변경
        // 기존 세션 ID를 그대로 사용하지 않고 새로운 세션 ID로 변경
        httpServletRequest.changeSessionId();

        // 4. 세션에 로그인한 유저의 id 저장
        // 이후 다른 Controller에서 "userId"를 꺼내
        // 현재 로그인한 사용자가 누구인지 확인한다.
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 로그아웃
    // POST /api/user/logout
    // =========================
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {

        // 현재 세션 가져오기
        // false : 세션이 없으면 새로 생성하지 않고 null 반환
        HttpSession session = request.getSession(false);

        // 세션이 없거나 userId가 없으면 로그인하지 않은 상태
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException(
                    "로그인이 필요합니다."
            );
        }

        // 현재 세션을 무효화
        // 세션에 저장되어 있던 userId도 사라진다.
        session.invalidate();

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 내 정보 조회
    // GET /api/user/me
    // =========================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(
            HttpServletRequest httpServletRequest
    ) {

        // 1. 이 사람의 세션을 가져옴
        HttpSession session =
                httpServletRequest.getSession(false);

        // 세션이 없거나 userId가 없으면 로그인하지 않은 상태
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException(
                    "로그인이 필요합니다."
            );
        }

        // 2. 세션에서 유저 아이디 뽑아옴
        int userId =
                (int) session.getAttribute("userId");

        // 3. userId를 UserService에 전달해서
        // 해당 유저의 정보를 조회한다.
        MyInfoResponse response =
                userService.getUserInfo(userId);

        // 조회한 유저 정보를 응답으로 반환
        return ResponseEntity.ok()
                .body(ApiResponse.ok(response));
    }


    // =========================
    // 내 정보 수정
    // PATCH /api/user
    // =========================
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            HttpServletRequest request,

            // 수정할 유저 정보를 요청 JSON으로 받는다.
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session =
                request.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException(
                    "로그인이 필요합니다."
            );
        }

        // 세션에서 현재 로그인한 userId 가져오기
        int userId =
                (int) session.getAttribute("userId");

        // 로그인한 유저의 id + 수정할 정보를 Service에 전달
        userService.updateUserInfo(
                userId,
                userUpdateRequest
        );

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 유저 탈퇴 기능
    // DELETE /api/user
    // =========================
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ) {

        // 현재 세션 가져오기
        HttpSession session =
                request.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException(
                    "로그인이 필요합니다."
            );
        }

        // 세션에서 현재 로그인한 userId 가져오기
        int userId =
                (int) session.getAttribute("userId");

        // 1. DB 삭제
        // 현재 로그인한 유저를 users 테이블에서 삭제
        userService.deleteUser(userId);

        // 2. 세션 비활성화
        // 탈퇴했으므로 로그인 상태도 같이 종료
        session.invalidate();

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }
}