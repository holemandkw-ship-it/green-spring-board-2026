package com.green.spring_board.service;

import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.dto.LikeDetailResponse;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Like;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.LikeRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {

    // 게시글 DB 작업
    private BoardRepository boardRepository;

    // 유저 DB 작업
    private UserRepository userRepository;

    // 좋아요 DB 작업
    private LikeRepository likeRepository;


    // =========================
    // 전체 조회
    // =========================
    public List<BoardResponse> getAllBoards(int userId) {

        // boards 테이블의 모든 게시글 조회
        List<Board> boards = boardRepository.findAll();

        // List<BoardResponse> 형태의 빈 리스트 생성
        // Entity(Board)를 그대로 반환하지 않고 응답용 DTO로 변환해서 담는다.
        List<BoardResponse> boardResponses = new ArrayList<>();

        // Board 개수만큼 반복
        for (Board board : boards) {

            // Board -> BoardResponse 변환 후 리스트에 추가
            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),

                            // 내가 이 게시글에 좋아요를 눌렀는지 확인
                            // userId == -1 : 로그인하지 않은 사용자 -> false
                            // 로그인 상태 : likes 테이블에서
                            // 현재 userId + 현재 boardId 조합이 존재하는지 확인
                            (userId == -1) ? false : likeRepository
                                    .existsByUserIdAndBoardId(
                                            userId,
                                            board.getId()
                                    ),

                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }

        // 변환된 게시글 목록 반환
        return boardResponses;
    }


    // =========================
    // 상세 조회
    // =========================
    public BoardResponse getBoard(int id, int userId) {

        // URL로 전달받은 게시글 id로 게시글 찾기
        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        // 게시글이 존재하지 않으면 404 예외 발생
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "요청한 게시글을 찾지 못했습니다."
            );
        }

        // Optional 안에 있는 실제 Board 객체 꺼내기
        Board board = optionalBoard.get();

        // 조회수 증가
        board.setHits(board.getHits() + 1);

        // 증가한 조회수를 DB에 저장
        boardRepository.save(board);

        // Board -> BoardResponse 변환 후 반환
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getLikeCount(),

                // 로그인하지 않았으면 false
                // 로그인했다면 내가 이 게시글에 좋아요를 눌렀는지 확인
                (userId == -1) ? false : likeRepository
                        .existsByUserIdAndBoardId(
                                userId,
                                board.getId()
                        ),

                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getCreatedDatetime(),
                board.getUpdatedDatetime()
        );
    }


    // =========================
    // 내가 작성한 게시글 전체 조회
    // =========================
    public List<BoardResponse> getMyBoards(int userId) {

        // boards 테이블에서 현재 userId가 작성한 게시글만 조회
        List<Board> boards = boardRepository.findByUserId(userId);

        // 응답용 BoardResponse를 담을 빈 리스트
        List<BoardResponse> boardResponses = new ArrayList<>();

        // Board -> BoardResponse 변환
        for (Board board : boards) {

            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),

                            // 내가 이 게시글에 좋아요를 눌렀는지 확인
                            // 로그인하지 않았으면 false
                            // 로그인했다면 likes 테이블에
                            // userId + boardId 조합이 존재하는지 확인
                            (userId == -1) ? false : likeRepository
                                    .existsByUserIdAndBoardId(
                                            userId,
                                            board.getId()
                                    ),

                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }

        return boardResponses;
    }


    // =========================
    // 게시글 삽입
    // =========================
    public int createBoard(
            BoardCreateRequest boardCreateRequest,
            Integer userId
    ) {

        // 로그인한 유저가 실제 존재하는지 확인
        Optional<User> user
                = userRepository.findById(userId);

        // 해당 유저가 DB에 존재하지 않으면 예외 발생
        if (user.isEmpty()) {
            throw new UnauthenticatedException(
                    "로그인한 사용자를 찾을 수 없습니다."
            );
        }

        // DB에 저장할 새로운 Board 객체 생성
        Board board = new Board();

        // 요청 DTO에서 제목과 내용을 꺼내 Board에 저장
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());

        // 로그인한 유저를 게시글 작성자로 설정
        board.setUser(user.get());

        // boards 테이블에 게시글 저장
        Board savedBoard = boardRepository.save(board);

        // 저장된 게시글의 id 반환
        return savedBoard.getId();
    }


    // =========================
    // 게시글 수정
    // =========================
    public void updateBoard(
            int id,
            BoardUpdateRequest boardUpdateRequest,
            int userId
    ) {

        // 수정할 게시글 찾기
        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        // 게시글이 존재하지 않으면 404 예외 발생
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoard.get();

        // 작성자와 요청자가 동일한지 확인
        // 게시글 작성자 id와 현재 로그인한 userId가 다르면 수정 불가
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "게시글 작업 권한이 없습니다."
            );
        }

        // 제목 수정
        // null이 아니고 빈 문자열도 아닐 때만 수정
        if (boardUpdateRequest.getTitle() != null
                && !boardUpdateRequest.getTitle().isBlank()) {

            board.setTitle(
                    boardUpdateRequest.getTitle()
            );
        }

        // 내용 수정
        // null이 아니고 빈 문자열도 아닐 때만 수정
        if (boardUpdateRequest.getContent() != null
                && !boardUpdateRequest.getContent().isBlank()) {

            board.setContent(
                    boardUpdateRequest.getContent()
            );
        }

        // 수정된 게시글 DB에 저장
        boardRepository.save(board);
    }


    // =========================
    // 게시글 삭제
    // =========================
    public void deleteBoard(int id, int userId) {

        // 삭제할 게시글 찾기
        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        // 게시글이 존재하지 않으면 404 예외 발생
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoard.get();

        // 작성자와 요청자가 동일한지 확인
        // 자신이 작성한 게시글만 삭제 가능
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "게시글 작업 권한이 없습니다."
            );
        }

        // 게시글 id를 이용해서 DB에서 삭제
        boardRepository.deleteById(id);
    }


    // =========================
    // 좋아요
    // =========================
    public void pressLike(int id, int userId) {

        // 1. 게시글 찾기
        // id = 좋아요를 누른 게시글 번호
        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        // 게시글이 존재하지 않으면 예외 발생
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "존재하지 않는 게시글입니다"
            );
        }

        Board board = optionalBoard.get();

        // 2. 로그인한 유저 찾기
        Optional<User> optionalUser
                = userRepository.findById(userId);

        // 로그인한 유저가 DB에 존재하지 않으면 예외 발생
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(
                    "존재하지 않는 유저입니다"
            );
        }

        User user = optionalUser.get();

        // 1. 이 유저와 보드로 동일한 좋아요가 있는지 확인
        // likes 테이블에서 userId + boardId 조합을 찾는다.
        Optional<Like> likeOptional =
                likeRepository.findByUserIdAndBoardId(
                        userId,
                        id
                );

        // 기존 좋아요가 없다면
        if (likeOptional.isEmpty()) {

            // 좋아요 추가

            // 3. 좋아요 객체 만들기
            Like like = new Like();

            // 누가 좋아요했는지
            like.setUser(user);

            // 어떤 게시글에 좋아요했는지
            like.setBoard(board);

            // 4. likes 테이블에 저장
            likeRepository.save(like);

            // 좋아요 개수 +1
            board.setLikeCount(
                    board.getLikeCount() + 1
            );

            // 증가한 좋아요 개수를 boards 테이블에 저장
            boardRepository.save(board);

        } else {

            // 기존 좋아요가 있다면 좋아요 취소

            // 좋아요 삭제
            Like like = likeOptional.get();

            // likes 테이블에서 해당 좋아요 삭제
            likeRepository.deleteById(like.getId());

            // 좋아요 개수 -1
            board.setLikeCount(
                    board.getLikeCount() - 1
            );

            // 감소한 좋아요 개수를 boards 테이블에 저장
            boardRepository.save(board);
        }
    }


    // =========================
    // 이 게시글에 좋아요 누른 유저 조회
    // =========================
    public LikeDetailResponse getLikeDetail(int id) {

        // 1. 이 게시글에 좋아요 누른 유저정보들을
        // Like 테이블에서 싹 가져옴
        List<Like> likes =
                likeRepository.findByBoardId(id);

        // 2. 걔네 닉네임 하나하나 뽑아서
        // LikeDetailResponse 에 집어넣음
        LikeDetailResponse likeDetailResponse =
                new LikeDetailResponse();

        // 유저 닉네임을 담을 빈 리스트
        List<String> nicknames = new ArrayList<>();

        // 해당 게시글의 Like들을 하나씩 반복
        for (Like like : likes) {

            // Like -> User -> nickname을 꺼내서 리스트에 추가
            nicknames.add(
                    like.getUser().getNickname()
            );
        }

        // 완성된 닉네임 리스트를 응답 DTO에 저장
        likeDetailResponse.setLikedUserNames(nicknames);

        // 3. 끝
        return likeDetailResponse;
    }
}