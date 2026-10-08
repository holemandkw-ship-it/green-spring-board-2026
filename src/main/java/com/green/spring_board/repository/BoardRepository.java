package com.green.spring_board.repository;

import com.green.spring_board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;


// Board 데이터를 DB에서 조회, 저장, 수정, 삭제하기 위한 Repository
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // JpaRepository<Board, Integer>
    // Board   : 어떤 Entity를 관리할 것인지
    // Integer : Board의 PK(id) 자료형
    //
    // JpaRepository를 상속받으면 기본적으로
    // findAll(), findById(), save(), deleteById() 등을 사용할 수 있다.


    // =========================
    // 특정 유저가 작성한 게시글 전체 조회
    // =========================

    // userId를 이용해서 해당 유저가 작성한 Board들을 조회한다.
    //
    // 메서드 이름을 보고 Spring Data JPA가 자동으로 쿼리를 만들어준다.
    //
    // Board에는 User 객체가 있고
    // User에는 id가 있기 때문에
    //
    // Board -> User -> id
    //
    // 를 기준으로 게시글을 찾는다.
    List<Board> findByUserIdAndIsDeletedFalse(int userId);
    Page<Board> findByIsDeletedFalse(Pageable pageable);
}