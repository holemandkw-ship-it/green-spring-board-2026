package com.green.spring_board.repository;

import com.green.spring_board.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


// Like 데이터를 DB에서 조회, 저장, 삭제하기 위한 Repository
@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {

    // JpaRepository<Like, Integer>
    // Like    : 어떤 Entity를 관리할 것인지
    // Integer : Like의 PK(id) 자료형
    //
    // JpaRepository를 상속받으면
    // save(), findById(), deleteById() 등의
    // 기본적인 DB 작업을 사용할 수 있다.


    // =========================
    // 특정 유저가 특정 게시글에 누른 좋아요 조회
    // =========================

    // SELECT *
    // FROM likes
    // WHERE user_id = 3 AND board_id = 9;
    //
    // userId + boardId가 일치하는 Like를 가져온다.
    // 좋아요가 존재할 수도 있고 없을 수도 있기 때문에 Optional<Like>로 반환한다.
    //
    // 좋아요 버튼을 눌렀을 때
    // 이미 좋아요가 있으면 삭제하고,
    // 없으면 새로 추가하기 위해 사용한다.
    Optional<Like> findByUserIdAndBoardId(
            int userId,
            int boardId
    );


    // =========================
    // 특정 유저가 특정 게시글에 좋아요를 눌렀는지 확인
    // =========================

    // userId + boardId가 일치하는 좋아요가 존재하는지만 확인한다.
    //
    // 존재함 -> true
    // 존재하지 않음 -> false
    //
    // 게시글을 조회할 때
    // 현재 로그인한 사용자의 likedByMe 값을 구하기 위해 사용한다.
    boolean existsByUserIdAndBoardId(
            int userId,
            int boardId
    );


    // =========================
    // 특정 게시글의 모든 좋아요 조회
    // =========================

    // boardId를 기준으로 해당 게시글에 달린
    // 모든 Like 데이터를 List로 가져온다.
    //
    // 이 게시글에 좋아요를 누른 사용자들의
    // 닉네임을 조회할 때 사용한다.
    List<Like> findByBoardId(int boardId);
}