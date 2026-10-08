package com.green.spring_board.service;

import com.green.spring_board.dto.CommentCreateRequest;
import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.dto.CommentUpdateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.entity.User;
import com.green.spring_board.exception.AuthorizationFailureException;
import com.green.spring_board.exception.ResourceNotFoundException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.CommentRepository;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    //생성
    public void createComment(
            CommentCreateRequest commentCreateRequest,
            int userId,
            int boardId
    ) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Board> boardOptional = boardRepository.findById(boardId);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        if (boardOptional.isEmpty()) {
            throw new ResourceNotFoundException("Board not found");
        }

        User user = userOptional.get();
        Board board = boardOptional.get();

        Comment comment = new Comment();
        comment.setContent(commentCreateRequest.getContent());
        comment.setUser(user);
        comment.setBoard(board);
        commentRepository.save(comment);
    }

    // 댓글 조회
    public List<CommentResponse> readComments(int boardId) {
        if(!boardRepository.existsById(boardId)){
            throw new ResourceNotFoundException("Board not found");
        }
        List<Comment> comments = commentRepository.findByBoardIdAndIsDeletedFalse(boardId);
        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {

            CommentResponse commentResponse = new CommentResponse();

            commentResponse.setCommentId(comment.getId());
            commentResponse.setNickname(comment.getUser().getNickname());
            commentResponse.setContent(comment.getContent());
            commentResponse.setCommentDate(comment.getCreatedDatetime());

            commentResponses.add(commentResponse);
        }

        return commentResponses;
    }

    //수정
    public void updateComment(
            int commentId,
            int userId,
            CommentUpdateRequest commentUpdateRequest
    ){
        //댓글 조회
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        //댓글 있는지 확인
        if (commentOptional.isEmpty()) {
            throw new ResourceNotFoundException("comment not found");
        }
        Comment comment = commentOptional.get();

        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("삭제된 댓글입니다.");
        }
        //댓글 작성자 확인
        if (comment.getUser().getId() != userId) {
            throw new AuthorizationFailureException(
                    "댓글 수정 권한이 없습니다."
            );
        }
        // 내용 수정
        if (commentUpdateRequest.getContent() != null
                && !commentUpdateRequest.getContent().isBlank()) {

            comment.setContent(
                    commentUpdateRequest.getContent()
            );
        }
        //디비저장
        commentRepository.save(comment);

    }

    
    //삭제
    public void deleteComment(int id, int userId) {

        Optional<Comment> optionalComment =
                commentRepository.findById(id);

        if (optionalComment.isEmpty()) {
            throw new ResourceNotFoundException("댓글을 찾을 수 없습니다.");
        }
        Comment comment = optionalComment.get();

        if (comment.getUser().getId() != userId) {
            throw new AuthorizationFailureException("댓글 삭제 권한이 없습니다.");
        }

        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("이미 삭제된 댓글입니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

}
























