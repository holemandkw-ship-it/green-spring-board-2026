package com.green.spring_board.service;

import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
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

    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;


    // 전체 조회
    public List<BoardResponse> getAllBoards() {

        List<Board> boards = boardRepository.findAll();

        // List<BoardResponse> 형태의 빈 리스트 생성
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
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }

        return boardResponses;
    }


    // 상세 조회
    public BoardResponse getBoard(int id) {

        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "요청한 게시글을 찾지 못했습니다."
            );
        }

        Board board = optionalBoard.get();

        // 조회수 증가
        board.setHits(board.getHits() + 1);

        boardRepository.save(board);

        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getHits(),
                board.getLikeCount(),
                board.getUser().getId(),
                board.getUser().getNickname(),
                board.getCreatedDatetime(),
                board.getUpdatedDatetime()
        );
    }

    // 내가 작성한 게시글 전체 조회
    public List<BoardResponse> getMyBoards(int userId) {

        List<Board> boards = boardRepository.findByUserId(userId);

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
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }

        return boardResponses;
    }


    // 게시글 삽입
    public int createBoard(
            BoardCreateRequest boardCreateRequest,
            Integer userId
    ) {

        // 로그인한 유저가 실제 존재하는지 확인
        Optional<User> user
                = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UnauthenticatedException(
                    "로그인한 사용자를 찾을 수 없습니다."
            );
        }

        Board board = new Board();

        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        board.setUser(user.get());

        Board savedBoard = boardRepository.save(board);

        return savedBoard.getId();
    }


    // 게시글 수정
    public void updateBoard(
            int id,
            BoardUpdateRequest boardUpdateRequest,
            int userId
    ) {

        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoard.get();

        // 작성자와 요청자가 동일한지 확인
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


    // 게시글 삭제
    public void deleteBoard(int id, int userId) {

        Optional<Board> optionalBoard
                = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        Board board = optionalBoard.get();

        // 작성자와 요청자가 동일한지 확인
        if (board.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "게시글 작업 권한이 없습니다."
            );
        }

        boardRepository.deleteById(id);
    }


    // 좋아요
    public void pressLike(int id, int userId) {

        // 1. 게시글 찾기
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException("존재하지 않는 게시글입니다");
        }

        Board board = optionalBoard.get();

        // 2. 로그인한 유저 찾기
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("존재하지 않는 유저입니다");
        }

        User user = optionalUser.get();

        //1.이 유저와 보드로 동일한 좋아요가 있는지 확인
        Optional<Like> likeOptional =
                likeRepository.findByUserIdAndBoardId(userId,id);
        if(likeOptional.isEmpty()){
            //좋아요 추가
            // 3. 좋아요 객체 만들기
            Like like = new Like();
            // 누가 좋아요했는지
            like.setUser(user);
            // 어떤 게시글에 좋아요했는지
            like.setBoard(board);
            // 4. likes 테이블에 저장
            likeRepository.save(like);

            // 좋아요 개수 +1
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);

        }else{

            //좋아요 삭제
            Like like = likeOptional.get();
            likeRepository.deleteById(like.getId());

            // 좋아요 개수 -1
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
        }


    }

}