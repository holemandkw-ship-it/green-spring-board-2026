package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.exception.UnauthenticatedException;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


// REST API 요청을 처리하는 Controller
@RestController

// 이 Controller의 기본 주소
// 모든 API 앞에 /api/board가 붙는다.
@RequestMapping("/api/board")

// 생성자를 자동으로 만들어 BoardService를 주입받는다.
@AllArgsConstructor
public class BoardController {

    // 실제 게시글 기능 처리는 Service에 맡긴다.
    private final BoardService boardService;


    // =========================
    // 전체 조회
    // GET /api/board
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoards(
            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        // false : 기존 세션이 없으면 새로 만들지 않고 null 반환
        HttpSession session = httpServletRequest.getSession(false);

        // 전체 조회는 로그인하지 않아도 가능하다.
        // 로그인하지 않은 사용자는 -1로 구분한다.
        int userId = -1;

        // 로그인했다면 세션에 저장된 실제 userId를 가져온다.
        if (session != null && session.getAttribute("userId") != null) {
            userId = (int) session.getAttribute("userId");
        }

        // userId를 Service에 전달
        // Service에서는 userId를 이용해서
        // 각 게시글의 likedByMe 값을 계산한다.
        return ResponseEntity.ok(
                ApiResponse.ok(
                        boardService.getAllBoards(userId)
                )
        );
    }


    // =========================
    // 상세 조회
    // GET /api/board/{id}
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(

            // URL의 게시글 번호를 가져온다.
            // 예: /api/board/3 -> id = 3
            @PathVariable int id,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 상세 조회도 로그인하지 않아도 가능하다.
        // 로그인하지 않은 사용자는 -1로 구분한다.
        int userId = -1;

        // 로그인했으면 실제 userId
        if (session != null && session.getAttribute("userId") != null) {
            userId = (int) session.getAttribute("userId");
        }

        // 게시글 id + 현재 사용자 id를 Service에 전달
        // id     : 어떤 게시글을 조회할지
        // userId : 내가 이 게시글에 좋아요를 눌렀는지 확인하기 위해 사용
        BoardResponse board =
                boardService.getBoard(id, userId);

        return ResponseEntity.ok(
                ApiResponse.ok(board)
        );
    }


    // =========================
    // 내가 작성한 게시글 전체 조회
    // GET /api/board/my-boards
    // =========================
    @GetMapping("/my-boards")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoards(
            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // ★ 강사님 수정 부분
        // "내가 작성한 게시글"은 로그인한 사용자가 누구인지 알아야 한다.
        // 따라서 전체/상세 조회와 다르게 -1을 사용하지 않고
        // 로그인하지 않았다면 바로 401 예외를 발생시킨다.
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 로그인한 사용자의 userId 가져오기
        int userId = (int) session.getAttribute("userId");

        // 현재 userId가 작성한 게시글들을 Service에서 조회
        List<BoardResponse> boards =
                boardService.getMyBoards(userId);

        // ★ 기존 코드의 response를 boards로 수정
        return ResponseEntity.ok(
                ApiResponse.ok(boards)
        );
    }


    // =========================
    // 삽입
    // POST /api/board
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(

            // 요청 JSON을 BoardCreateRequest 객체로 변환
            // @Valid : DTO에 설정한 유효성 검사 실행
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 게시글 작성은 로그인이 반드시 필요하다.
        // 세션이 없거나 세션에 userId가 없으면 401 예외
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 세션에서 로그인한 유저 id 가져오기
        int userId = (int) session.getAttribute("userId");

        // 작성할 게시글 정보 + 작성자 userId를 Service에 전달
        // 저장 후 새로 생성된 게시글 id를 반환받는다.
        int newBoardId =
                boardService.createBoard(
                        boardCreateRequest,
                        userId
                );

        // 새로 생성된 게시글의 주소 생성
        // 예: /api/board/13
        URI location =
                URI.create("/api/board/" + newBoardId);

        // 게시글 생성 성공 -> 201 Created
        return ResponseEntity.created(location)
                .body(ApiResponse.ok());
    }


    // =========================
    // 수정
    // PATCH /api/board/{id}
    // =========================
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(

            // 수정할 게시글 id
            @PathVariable int id,

            // 수정할 제목/내용을 요청 JSON에서 받는다.
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 게시글 수정은 로그인이 반드시 필요
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 현재 로그인한 유저 id
        int userId = (int) session.getAttribute("userId");

        // 수정할 게시글 id
        // 수정할 내용
        // 현재 로그인한 userId
        // 세 가지를 Service에 전달
        boardService.updateBoard(
                id,
                boardUpdateRequest,
                userId
        );

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 삭제
    // DELETE /api/board/{id}
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(

            // 삭제할 게시글 id
            @PathVariable int id,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 게시글 삭제는 로그인이 반드시 필요
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 현재 로그인한 유저 id
        int userId = (int) session.getAttribute("userId");

        // 삭제할 게시글 id + 로그인한 userId를 Service에 전달
        // Service에서 게시글 작성자와 현재 사용자가 같은지도 확인한다.
        boardService.deleteBoard(id, userId);

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 좋아요
    // POST /api/board/like/{id}
    // =========================
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard(

            // 좋아요를 누를 게시글 id
            @PathVariable int id,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 좋아요는 로그인이 반드시 필요
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 누가 좋아요를 눌렀는지 확인하기 위해
        // 세션에서 로그인한 userId를 가져온다.
        int userId = (int) session.getAttribute("userId");

        // 게시글 id + 로그인한 userId를 Service에 전달
        // 기존 좋아요가 없으면 추가
        // 기존 좋아요가 있으면 삭제
        boardService.pressLike(id, userId);

        return ResponseEntity.ok(
                ApiResponse.ok()
        );
    }


    // =========================
    // 이 게시글에 좋아요 누른 유저 조회
    // GET /api/board/like/{id}
    // =========================
    @GetMapping("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDetails(

            // 어떤 게시글의 좋아요 정보를 조회할지
            @PathVariable int id,

            HttpServletRequest httpServletRequest
    ) {

        // 현재 세션 가져오기
        HttpSession session = httpServletRequest.getSession(false);

        // 좋아요 상세 조회는 로그인 필요
        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 해당 게시글에 좋아요를 누른 유저들의 닉네임을
        // Service에서 조회해서 LikeDetailResponse로 받는다.
        LikeDetailResponse response =
                boardService.getLikeDetail(id);

        return ResponseEntity.ok(
                ApiResponse.ok(response)
        );
    }
}