package com.green.spring_board.repository;

import com.green.spring_board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


// User 데이터를 DB에서 조회, 저장, 수정, 삭제하기 위한 Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // JpaRepository<User, Integer>
    // User    : 어떤 Entity를 관리할 것인지
    // Integer : User의 PK(id) 자료형
    //
    // JpaRepository를 상속받으면
    // findAll(), findById(), save(), delete() 등의
    // 기본적인 DB 작업을 사용할 수 있다.


    // =========================
    // 이메일 존재 여부 확인
    // =========================

    // users 테이블에 해당 이메일이 존재하는지 확인한다.
    //
    // 존재함 -> true
    // 존재하지 않음 -> false
    //
    // 회원가입할 때 이미 사용 중인 이메일인지
    // 중복 확인하기 위해 사용한다.
    boolean existsByEmail(String email);


    // =========================
    // 이메일로 유저 조회
    // =========================

    // users 테이블에서 이메일이 일치하는 User를 찾는다.
    //
    // 해당 이메일의 유저가 있을 수도 있고 없을 수도 있기 때문에
    // Optional<User>로 반환한다.
    //
    // 로그인할 때 사용자가 입력한 이메일로
    // 회원을 찾기 위해 사용한다.
    Optional<User> findByEmail(String email);
}