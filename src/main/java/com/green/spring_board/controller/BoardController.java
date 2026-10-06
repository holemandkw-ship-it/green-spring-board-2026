package com.green.spring_board.controller;

import java.net.URI;
import java.util.List;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.exception.UserRequestException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/board")
@AllArgsConstructor
public class BoardController {

    private final BoardService boardService;


    // 전체 조회
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards() {

        return ResponseEntity.ok(
                boardService.getAllBoard()
        );
    }


    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardDetail(
            @PathVariable int id) {
        BoardResponse board = boardService.getBoard(id);
        return ResponseEntity.ok(board);
    }


    // 삽입
    @PostMapping
    public ResponseEntity<Void> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
            HttpServletRequest httpServletRequest) {

        // 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        // 게시글 생성
        int newBoardId = boardService.createBoard(boardCreateRequest, userId);
        // 생성된 게시글 주소
        URI location = URI.create("/api/board/" + newBoardId);
        return ResponseEntity.created(location).build();
    }


    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,
            HttpServletRequest httpServletRequest) {

        // 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // ToDo18 : 본인 확인
        boardService.updateBoard(id, boardUpdateRequest);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ){
        // 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);
        // 로그인 여부 확인
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 게시글 삭제
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}