package com.green.spring_board.service;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.dto.LikeDetailResponse;
import com.green.spring_board.entity.Like;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.entity.Board;
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

        // Board Entity를 BoardResponse DTO로 바꿔서 담을 리스트
        List<BoardResponse> boardResponses = new ArrayList<>();

        for (Board board : boards) {

            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),

                            // 비로그인(-1)이면 false
                            // 로그인 상태라면 내가 좋아요를 눌렀는지 확인
                            (userId == -1)
                                    ? false
                                    : likeRepository.existsByUserIdAndBoardId(
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
    // 상세 조회
    // =========================
    public BoardResponse getBoard(int id, int userId) {

        Optional<Board> optionalBoard =
                boardRepository.findById(id);

        // 게시글이 없으면 404
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "요청한 게시글을 찾지 못했습니다."
            );
        }

        Board board = optionalBoard.get();

        // 강사 코드에 있는 부분
        User user = board.getUser();
        System.out.println(user.getNickname());

        // 조회수 +1
        board.setHits(board.getHits() + 1);

        // 변경된 조회수 저장
        boardRepository.save(board);

        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getLikeCount(),

                (userId == -1)
                        ? false
                        : likeRepository.existsByUserIdAndBoardId(
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
    // 내가 작성한 게시글 조회
    // =========================
    public List<BoardResponse> getMyBoards(int userId) {

        // 현재 로그인한 userId가 작성한 게시글 조회
        List<Board> boards =
                boardRepository.findByUserId(userId);

        List<BoardResponse> boardResponses =
                new ArrayList<>();

        for (Board board : boards) {

            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getLikeCount(),

                            // 로그인 필수 기능이므로 -1 검사 필요 없음
                            likeRepository.existsByUserIdAndBoardId(
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
    // 게시글 작성
    // =========================
    public int createBoard(
            BoardCreateRequest boardCreateRequest,
            Integer userId
    ) {

        // 로그인한 userId의 User가 실제 존재하는지 확인
        Optional<User> user =
                userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UnauthenticatedException(
                    "로그인한 사용자를 찾을 수 없습니다."
            );
        }

        // 새로운 게시글 생성
        Board board = new Board();

        board.setTitle(
                boardCreateRequest.getTitle()
        );

        board.setContent(
                boardCreateRequest.getContent()
        );

        // 게시글 작성자 연결
        board.setUser(user.get());

        // DB 저장
        Board savedBoard =
                boardRepository.save(board);

        // 새 게시글 번호 반환
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

        Optional<Board> optionalBoards =
                boardRepository.findById(id);

        if (optionalBoards.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoards.get();

        // 작성자와 현재 로그인 사용자가 같은지 확인
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "게시글 작업 권한이 없습니다."
            );
        }

        // 제목 수정
        if (boardUpdateRequest.getTitle() != null
                && !boardUpdateRequest.getTitle().isBlank()) {

            board.setTitle(
                    boardUpdateRequest.getTitle()
            );
        }

        // 내용 수정
        if (boardUpdateRequest.getContent() != null
                && !boardUpdateRequest.getContent().isBlank()) {

            board.setContent(
                    boardUpdateRequest.getContent()
            );
        }

        boardRepository.save(board);
    }


    // =========================
    // 게시글 삭제
    // =========================
    public void deleteBoard(int id, int userId) {

        Optional<Board> optionalBoard =
                boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoard.get();

        // 자신의 게시글만 삭제 가능
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "게시글 작업 권한이 없습니다."
            );
        }

        boardRepository.deleteById(id);
    }


    // =========================
    // 좋아요 추가 / 취소
    // =========================
    public void pressLike(int id, int userId) {

        // 게시글 찾기
        Optional<Board> optionalBoard =
                boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "존재하지 않는 게시글입니다."
            );
        }

        Board board = optionalBoard.get();


        // 로그인한 유저 찾기
        Optional<User> optionalUser =
                userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(
                    "존재하지 않는 유저입니다."
            );
        }

        User user = optionalUser.get();


        // 현재 사용자가 이 게시글에 이미 좋아요를 눌렀는지 확인
        Optional<Like> likeOptional =
                likeRepository.findByUserIdAndBoardId(
                        userId,
                        id
                );


        if (likeOptional.isEmpty()) {

            // =========================
            // 좋아요가 없으면 추가
            // =========================

            Like like = new Like();

            like.setUser(user);
            like.setBoard(board);

            likeRepository.save(like);

            // 게시글 좋아요 개수 +1
            board.setLikeCount(
                    board.getLikeCount() + 1
            );

            boardRepository.save(board);

        } else {

            // =========================
            // 이미 좋아요가 있으면 취소
            // =========================

            Like like = likeOptional.get();

            likeRepository.deleteById(
                    like.getId()
            );

            // 게시글 좋아요 개수 -1
            board.setLikeCount(
                    board.getLikeCount() - 1
            );

            boardRepository.save(board);
        }
    }


    // =========================
    // 좋아요 누른 사용자 조회
    // =========================
    public LikeDetailResponse getLikeDetail(int id) {

        // 해당 게시글에 달린 Like 전부 조회
        List<Like> likes =
                likeRepository.findByBoardId(id);

        LikeDetailResponse likeDetailResponse =
                new LikeDetailResponse();

        // 닉네임을 담을 리스트
        List<String> nicknames =
                new ArrayList<>();

        // Like → User → nickname
        for (Like like : likes) {

            nicknames.add(
                    like.getUser().getNickname()
            );
        }

        likeDetailResponse.setLikedUserNames(
                nicknames
        );

        return likeDetailResponse;
    }
}