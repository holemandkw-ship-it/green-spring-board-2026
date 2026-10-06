package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.ResourceConflictException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.exception.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {

        userService.signup(signupRequest);

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest) {

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(
            HttpServletRequest httpServletRequest
    ){
        // 1. 이 사람의 세션을 가져옴
        /*
        "me"는 회원 전용 서비스다
        이사람의 세션이 없으면 , 새로 만들어주는게 아니라 내쫓아야함
        그래서 세션이 없다고 세션을 만들지 않도록 getSession안에 (false)옵션을 추가한다.
         */
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null){
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        //2 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        MyInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        session.invalidate();

        return ResponseEntity.ok().body(ApiResponse.ok());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> UpdateUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        // 현재 유저를 가져와서, 해당 유저 정보를
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");

        // 사용자가 올린 요청으로 덮어 씌운다
        // 보드 했던것처럼 null이면 수정하지 않기!
        userService.updateUserInfo(userId, userUpdateRequest);

        return ResponseEntity.ok().body(ApiResponse.ok());
    }

    // 유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ) {
        // 기존 세션 가져오기
        HttpSession session = request.getSession(false);

        // 로그인 상태 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 세션에서 현재 로그인한 유저 아이디 가져오기
        int userId = (int) session.getAttribute("userId");

        // DB 삭제
        userService.deleteUser(userId);
        //세션비활성하
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok());
    }

}






























