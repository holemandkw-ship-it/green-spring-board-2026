package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {

        userService.signup(signupRequest);

        return ResponseEntity.ok(ApiResponse.ok());
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest
    ) {

        // 이메일/비밀번호 확인 후 로그인한 유저 ID 가져오기
        int userId = userService.login(loginRequest);

        // 세션 생성
        HttpSession session = httpServletRequest.getSession();

        // 세션 ID 변경
        httpServletRequest.changeSessionId();

        // 로그인한 유저 ID를 세션에 저장
        session.setAttribute("userId", userId);

        return ResponseEntity.ok(ApiResponse.ok());
    }


    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(
            HttpServletRequest httpServletRequest
    ) {

        // 기존 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 세션에서 로그인한 유저 ID 가져오기
        int userId = (int) session.getAttribute("userId");

        MyInfoResponse response =
                userService.getUserInfo(userId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok());
    }


    // 유저 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @PathVariable int id,
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {

        // 기존 세션 가져오기
        HttpSession session = request.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 요청자 = 현재 로그인한 유저
        int userId = (int) session.getAttribute("userId");

        // id = 수정 대상
        // userId = 요청자
        userService.updateUserInfo(
                id,
                userUpdateRequest,
                userId
        );

        return ResponseEntity.ok(ApiResponse.ok());
    }


    // 유저 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable int id,
            HttpServletRequest request
    ) {
        // 기존 세션 가져오기
        HttpSession session = request.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 요청자 = 현재 로그인한 유저
        int userId = (int) session.getAttribute("userId");

        // id = 탈퇴 대상
        // userId = 요청자
        userService.deleteUser(id, userId);

        // 탈퇴 성공 후 세션 비활성화
        session.invalidate();
        
        return ResponseEntity.ok(ApiResponse.ok());
    }
}