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


// User 관련 실제 기능을 처리하는 Service
// Controller에서 요청을 받아 DB 작업이 필요하면 Repository를 사용한다.
@Service
@AllArgsConstructor
public class UserService {

    // users 테이블에 접근하기 위한 Repository
    private final UserRepository userRepository;

    // 비밀번호를 그대로 DB에 저장하지 않고
    // BCrypt 방식으로 해싱하기 위해 사용
    private final PasswordEncoder passwordEncoder
            = new BCryptPasswordEncoder();


    // =========================
    // 회원가입
    // =========================
    public void signup(SignupRequest signupRequest) {

        // 이메일이 사용 중인지 확인
        // users 테이블에 같은 이메일이 존재하면 true
        if (userRepository.existsByEmail(signupRequest.getEmail())) {

            // 이미 사용 중인 이메일이면 409 Conflict 예외 발생
            throw new ResourceConflictException(
                    "Email already exists"
            );
        }

        // 비밀번호 해싱
        // 사용자가 입력한 원본 비밀번호를 BCrypt로 변환한다.
        // 원본 비밀번호 자체를 DB에 저장하지 않는다.
        String hashedPassword = passwordEncoder.encode(
                signupRequest.getPassword()
        );

        // DB save

        // DB에 저장할 새로운 User 객체 생성
        User user = new User();

        // 회원가입 요청에서 이메일 가져오기
        user.setEmail(signupRequest.getEmail());

        // 원본 비밀번호가 아닌 해싱된 비밀번호 저장
        user.setPassword(hashedPassword);

        // 회원가입 요청에서 닉네임 가져오기
        user.setNickname(signupRequest.getNickname());

        // users 테이블에 회원 정보 저장
        userRepository.save(user);
    }


    // =========================
    // 로그인
    // =========================
    public int login(LoginRequest loginRequest) {

        // 이메일 존재하는지 확인
        // 사용자가 입력한 이메일로 users 테이블에서 유저 찾기
        Optional<User> userOptional
                = userRepository.findByEmail(
                loginRequest.getEmail()
        );

        // 해당 이메일을 사용하는 유저가 없으면 예외 발생
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        // Optional 안에 있는 실제 User 객체 꺼내기
        User user = userOptional.get();

        // 비밀번호가 올바른지 확인
        // loginRequest.getPassword()
        // -> 사용자가 로그인할 때 입력한 원본 비밀번호
        //
        // user.getPassword()
        // -> DB에 저장되어 있는 해싱된 비밀번호
        //
        // matches()가 두 비밀번호가 같은 비밀번호인지 확인한다.
        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {

            // 비밀번호가 일치하지 않으면 인증 실패
            throw new UnauthenticatedException(
                    "Wrong password"
            );
        }

        // 로그인 성공
        // 로그인한 유저의 id를 Controller로 반환
        // Controller에서는 이 userId를 세션에 저장한다.
        return user.getId();
    }


    // =========================
    // 내 정보 조회
    // =========================
    public MyInfoResponse getUserInfo(int userId) {

        // Controller가 세션에서 가져온 userId로
        // users 테이블에서 현재 로그인한 유저 조회
        Optional<User> userOptional
                = userRepository.findById(userId);

        // 해당 유저가 DB에 존재하지 않으면 예외 발생
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        // 실제 User 객체 꺼내기
        User user = userOptional.get();

        // DB에서 유저의 이메일과 닉네임 가져오기
        String email = user.getEmail();
        String nickname = user.getNickname();

        // Entity(User)를 그대로 반환하지 않고
        // 응답에 필요한 정보만 담기 위한 DTO 생성
        MyInfoResponse myInfoResponse =
                new MyInfoResponse();

        // DTO에 이메일 저장
        myInfoResponse.setEmail(email);

        // DTO에 닉네임 저장
        myInfoResponse.setNickname(nickname);

        // 완성된 응답 DTO를 Controller로 반환
        return myInfoResponse;
    }


    // =========================
    // 유저 정보 수정
    // =========================
    public void updateUserInfo(
            int userId,
            UserUpdateRequest userUpdateRequest
    ) {

        // 로그인한 유저 가져오기
        // Controller에서 세션의 userId를 전달받는다.
        Optional<User> userOptional
                = userRepository.findById(userId);

        // 해당 유저가 존재하지 않으면 예외 발생
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        User user = userOptional.get();

        // 수정 대상자와 요청자가 동일한지 확인
        // 현재 가져온 유저의 id와
        // 세션에서 전달받은 userId가 같은지 확인
        if (user.getId() != userId) {
            throw new AuthorizationFailureException(
                    "본인의 정보만 수정할 수 있습니다."
            );
        }

        // 이메일 수정
        // 1. null이 아니고
        // 2. 빈 문자열이 아니고
        // 3. 기존 이메일과 다른 경우에만 수정
        if (userUpdateRequest.getEmail() != null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())
        ) {

            user.setEmail(
                    userUpdateRequest.getEmail()
            );
        }

        // 닉네임 수정
        // null이 아니고 빈 문자열도 아닐 때만 수정
        if (userUpdateRequest.getNickname() != null
                && !userUpdateRequest.getNickname().isBlank()
        ) {

            user.setNickname(
                    userUpdateRequest.getNickname()
            );
        }

        // DB 저장
        // 변경된 이메일/닉네임을 users 테이블에 반영
        userRepository.save(user);
    }


    // =========================
    // 회원 탈퇴
    // =========================
    public void deleteUser(int userId) {

        // 로그인한 유저 가져오기
        // 세션에서 전달받은 userId로 유저 조회
        Optional<User> userOptional
                = userRepository.findById(userId);

        // 해당 유저가 존재하지 않으면 예외 발생
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
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

        // 여기서는 DB의 유저만 삭제한다.
        // 세션 종료(session.invalidate())는
        // UserController의 deleteUser()에서 처리한다.
    }
}