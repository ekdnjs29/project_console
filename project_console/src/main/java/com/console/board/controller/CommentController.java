package com.console.board.controller;

import com.console.board.dtos.CommentDto;
import com.console.board.dtos.UserDto;
import com.console.board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDto> addComment(
        @PathVariable int postId,
        @RequestBody Map<String, String> payload, 
        HttpSession session) {

        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String content = payload.get("content");
        CommentDto savedComment = commentService.addComment(postId, loggedInUser.getUserId(), content, null);

        // 닉네임과 생성 날짜 설정
        savedComment.setUserNickname(loggedInUser.getNickname());
        savedComment.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 저장 시각 설정

        return ResponseEntity.ok(savedComment); // 저장된 댓글 반환
    }



    
    @GetMapping("/{postId}/all")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable int postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
