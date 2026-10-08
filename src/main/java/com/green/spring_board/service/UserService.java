package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.ResourceConflictException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


// User 관련 실제 기능을 처리하는 Service
// Controller에서 요청을 받아 DB 작업이 필요하면 Repository를 사용한다.
@Service
@AllArgsConstructor
public class UserService {

    // users 테이블에 접근하기 위한 Repository
    private final UserRepository userRepository;

    // 비밀번호를 BCrypt 방식으로 해싱하기 위해 사용
    private final PasswordEncoder passwordEncoder
            = new BCryptPasswordEncoder();


    // =========================
    // 회원가입
    // =========================
    public void signup(SignupRequest signupRequest) {

        // 같은 이메일이 이미 존재하는지 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceConflictException(
                    "Email already exists"
            );
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(
                signupRequest.getPassword()
        );

        // 저장할 User 객체 생성
        User user = new User();

        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setNickname(signupRequest.getNickname());
        user.setState(UserState.ACTIVE);
        // DB 저장
        userRepository.save(user);
    }


    // =========================
    // 로그인
    // =========================
    public int login(LoginRequest loginRequest) {

        // 이메일로 유저 찾기
        Optional<User> userOptional =
                userRepository.findByEmail(
                        loginRequest.getEmail()
                );

        // 유저가 존재하지 않음
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        User user = userOptional.get();

        if(user.getState() == UserState.QUITTED){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }

        // 입력한 비밀번호와
        // DB에 저장된 해싱 비밀번호 비교
        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {
            throw new UnauthenticatedException(
                    "Wrong password"
            );
        }

        // 로그인 성공 -> userId 반환
        return user.getId();
    }


    // =========================
    // 내 정보 조회
    // =========================
    public MyInfoResponse getUserInfo(int userId) {

        // userId로 유저 조회
        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        User user = userOptional.get();

        if(user.getState() == UserState.QUITTED){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }

        String email = user.getEmail();
        String nickname = user.getNickname();

        // 응답 DTO 생성
        MyInfoResponse myInfoResponse =
                new MyInfoResponse();

        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);

        return myInfoResponse;
    }


    // =========================
    // 유저 정보 수정
    // =========================
    public void updateUserInfo(
            int userId,
            UserUpdateRequest userUpdateRequest
    ) {

        // 현재 로그인한 유저 조회
        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        User user = userOptional.get();

        if(user.getState() == UserState.QUITTED){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }

        // 이메일 수정
        // null이 아니고
        // 빈 문자열이 아니고
        // 기존 이메일과 다를 경우
        if (userUpdateRequest.getEmail() != null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())
        ) {

            user.setEmail(
                    userUpdateRequest.getEmail()
            );
        }


        // 닉네임 수정
        if (userUpdateRequest.getNickname() != null
                && !userUpdateRequest.getNickname().isBlank()
        ) {

            user.setNickname(
                    userUpdateRequest.getNickname()
            );
        }


        // 변경 내용 DB 저장
        userRepository.save(user);
    }


    // =========================
    // 회원 탈퇴
    // =========================
    public void deleteUser(int userId) {

        // 현재 로그인한 유저 조회
        Optional<User> userOptional =
                userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        User user = userOptional.get();

        user.setState(UserState.QUITTED);
        userRepository.save(user);
    }
}