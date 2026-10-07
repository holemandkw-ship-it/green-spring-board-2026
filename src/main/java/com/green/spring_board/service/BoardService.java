package com.green.spring_board.service;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.repository.BoardRepository;
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


    // 전체 조회
    public List<BoardResponse> getAllBoard() {
        List<Board> boards = boardRepository.findAll();

        List<BoardResponse> boardResponses = new ArrayList<>();

        for (Board board : boards) {
            boardResponses.add(
                    new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getHits(),
                            board.getUser().getId(),
                            board.getUser().getNickname(),
                            board.getCreatedDatetime(),
                            board.getUpdatedDatetime()
                    )
            );
        }
        return boardResponses;
        //List<Board> -> List<BoardResponse> 형태로 변환

        //1.List<BoardResponse> 형태의 빈 리스트 생성
        //2.Board 개수만큼 반복하며 new BoardResponse생성
        //3.1번에서 만든 리스트에 추가
    }


    // 상세 조회
    public BoardResponse getBoard(int id) {

        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            //요청한 게시글을 찾지 못한 경우
            throw new ResourceNotFoundException("요청한 게시글을 찾지 못했습니다.");
        }

        Board board = optionalBoard.get();

        board.setHits(board.getHits() + 1);
        boardRepository.save(board);
        return new BoardResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getHits(),
                        board.getUser().getId(),
                        board.getUser().getNickname(),
                        board.getCreatedDatetime(),
                        board.getUpdatedDatetime()
                );

    }


    // 삽입
    public int createBoard(BoardCreateRequest boardCreateRequest,Integer userId) {

        //userId 유효성 체크(해당 userID의 유저가 정상적으로 존재하는지)
        //TDTD;; 이후 삭제/탈퇴 유저에 대한 검증도 추가필요
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new UnauthenticatedException("로그인한 사용자를 찾을 수 없습니다.");
        }

        Board board = new Board();
        board.setTitle(boardCreateRequest.getTitle());
        board.setContent(boardCreateRequest.getContent());
        board.setUser(user.get());

        Board savedBoard = boardRepository.save(board);

        return savedBoard.getId();
    }


    // 수정
    public void updateBoard(int id, BoardUpdateRequest boardUpdateRequest,int userId) {

        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            //게시글을 못찾은 경우
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }

        Board board = optionalBoard.get();

        //작성자와 요청자 동일 여부 확인
        if(board.getUser().getId() != userId){
            throw new AuthorizationFailureException("게시글 작업 권한이 없습니다");
        }

        if (boardUpdateRequest.getTitle() != null
                && !boardUpdateRequest.getTitle().isBlank()) {
            board.setTitle(boardUpdateRequest.getTitle());
        }

        if (boardUpdateRequest.getContent() != null
                && !boardUpdateRequest.getContent().isBlank()) {
            board.setContent(boardUpdateRequest.getContent());
        }

        boardRepository.save(board);
    }


    //삭제
    public void deleteBoard(int id,int userId) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            throw new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
        }
        Board board = optionalBoard.get();
        if(board.getUser().getId() !=userId){
            throw new AuthorizationFailureException("게시글 작업 권한이 없습니다");
        }
        boardRepository.deleteById(id);
    }
}