package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.ResourceConflictException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder
            = new BCryptPasswordEncoder();


    // 회원가입
    public void signup(SignupRequest signupRequest) {

        // 이메일이 사용 중인지 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceConflictException("Email already exists");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(
                signupRequest.getPassword()
        );

        // DB save
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setNickname(signupRequest.getNickname());

        userRepository.save(user);
    }


    // 로그인
    public int login(LoginRequest loginRequest) {

        // 이메일 존재하는지 확인
        Optional<User> userOptional
                = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        // 비밀번호가 올바른지 확인
        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {
            throw new UnauthenticatedException("Wrong password");
        }

        // 로그인 성공
        return user.getId();
    }


    // 내 정보 조회
    public MyInfoResponse getUserInfo(int userId) {

        Optional<User> userOptional
                = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        // DB에서 유저의 이메일과 닉네임 가져오기
        String email = user.getEmail();
        String nickname = user.getNickname();

        MyInfoResponse myInfoResponse = new MyInfoResponse();

        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);

        return myInfoResponse;
    }


    // 유저 정보 수정
    public void updateUserInfo(
            int userId,
            UserUpdateRequest userUpdateRequest
    ) {

        // 로그인한 유저 가져오기
        Optional<User> userOptional
                = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        // 수정 대상자와 요청자가 동일한지 확인
        if (user.getId() != userId) {
            throw new AuthorizationFailureException(
                    "본인의 정보만 수정할 수 있습니다."
            );
        }

        // 이메일 수정
        if (userUpdateRequest.getEmail() != null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())
        ) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        // 닉네임 수정
        if (userUpdateRequest.getNickname() != null
                && !userUpdateRequest.getNickname().isBlank()
        ) {
            user.setNickname(userUpdateRequest.getNickname());
        }

        // DB 저장
        userRepository.save(user);
    }


    // 회원 탈퇴
    public void deleteUser(int userId) {

        // 로그인한 유저 가져오기
        Optional<User> userOptional
                = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        // 탈퇴 대상자와 요청자가 동일한지 확인
        if (user.getId() != userId) {
            throw new AuthorizationFailureException(
                    "본인만 탈퇴할 수 있습니다."
            );
        }

        // DB에서 유저 삭제
        userRepository.delete(user);
    }
}