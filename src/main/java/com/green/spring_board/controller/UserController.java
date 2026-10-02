package com.green.spring_board.controller;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.ResourceConflictException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.exception.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest){
        try{
            userService.signup(signupRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceConflictException e){
            return ResponseEntity.status(409).build();
        } catch (UserRequestException e){
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public  ResponseEntity<Void> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest){
        try {
            int userId = userService.login(loginRequest);
            HttpSession session = httpServletRequest.getSession();
            httpServletRequest.changeSessionId();
            session.setAttribute("userId",userId);
            return ResponseEntity.ok().build();

        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (UnauthenticatedException e){
            return ResponseEntity.status(401).build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }


    }
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getCurrentUser(
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
            return ResponseEntity.status(401).build();
        }
        //2 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        MyInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }

        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PatchMapping
        public ResponseEntity<Void> UpdateUserInfo(
                HttpServletRequest request,
                @RequestBody MyInfoResponse myInfoResponse
        ) {
        //현재 유저를 가져와서, 해당 유저 정보를
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }
        // 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        //사용자가 올린 요청으로 덮어 씌운다
        //보드 했던것처럼 null이면 수정하지 않기!
        userService.updateUserInfo(userId, myInfoResponse);

        return ResponseEntity.ok().build();

    }

    // 유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            HttpServletRequest request
    ) {
        // 기존 세션 가져오기
        HttpSession session = request.getSession(false);

        // 로그인 상태 확인
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }

        // 세션에서 현재 로그인한 유저 아이디 가져오기
        int userId = (int) session.getAttribute("userId");

        // elql tkrwp
        userService.deleteUser(userId);
        //세션비활성하
        session.invalidate();

        return ResponseEntity.noContent().build();
    }


}






























