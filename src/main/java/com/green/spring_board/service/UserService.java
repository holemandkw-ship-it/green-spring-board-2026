package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.entity.User;
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

    public int login(LoginRequest loginRequest){
        //1이메일 존재하는건지 확인
        Optional<User> userOptional
                = userRepository.findByEmail(loginRequest.getEmail());
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();
        //2비밀번호가 올바른지 확인
        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            throw new UnauthenticatedException("Wrong password");
        }
        //3로그인성공
        return user.getId();
    }

    public MyInfoResponse getUserInfo(int userId){
        Optional<User> userOptional= userRepository.findById(userId);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        //4 DB에서 이유저의 닉네임과 이메일을 받아옴
        String email = user.getEmail();
        String nickname = user.getNickname();

        //5 돌려줌
        MyInfoResponse myInfoResponse =new MyInfoResponse();
        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);

        return myInfoResponse;

    }

    public void updateUserInfo(int userId, UserUpdateRequest userUpdateRequest) {
        //로그인한 유저 가져오기
        Optional<User> userOptional= userRepository.findById(userId);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        //수정할건 이메일 닉네임
        //이메일 수정
        if(userUpdateRequest.getEmail()!=null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())
        ){
            user.setEmail(userUpdateRequest.getEmail());
        }
        //닉네임 수정
        if(userUpdateRequest.getNickname()!=null
                && !userUpdateRequest.getNickname().isBlank()
        ){
            user.setNickname(userUpdateRequest.getNickname());
        }
        //db에 저장
        userRepository.save(user);


    }

    public void deleteUser(int userId) {
        // userId로 삭제할 유저 찾기
        Optional<User> userOptional = userRepository.findById(userId);
        // 유저가 없으면 예외 발생
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        // Optional에서 실제 User 꺼내기
        User user = userOptional.get();
        // DB에서 유저 삭제
        userRepository.delete(user);
    }

}





























