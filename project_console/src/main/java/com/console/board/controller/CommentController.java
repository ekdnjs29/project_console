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

    // 댓글 및 답글 추가
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
        Integer parentCommentId = null; // 초기화

        // parentCommentId가 존재하고 빈 문자열이 아닐 때만 변환
        if (payload.containsKey("parentCommentId") && !payload.get("parentCommentId").isEmpty()) {
            parentCommentId = Integer.parseInt(payload.get("parentCommentId"));
        }

        CommentDto savedComment = commentService.addComment(postId, loggedInUser.getUserId(), content, parentCommentId);

        // 닉네임과 작성 날짜 설정
        savedComment.setUserNickname(loggedInUser.getNickname());
        savedComment.setCreatedAt(new Timestamp(System.currentTimeMillis())); 

        return ResponseEntity.ok(savedComment); 
    }

    // 계층적으로 정렬된 댓글 조회
    @GetMapping("/{postId}/all")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable int postId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByPostIdOrdered(postId);

            // 배열 형태인지 추가 확인
            if (!(comments instanceof List)) {
                System.err.println("배열 형태가 아님: " + comments.getClass().getName());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("응답 형식 오류");
            }

            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            System.err.println("댓글 조회 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 조회 중 오류 발생");
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
        @PathVariable int commentId,
        @RequestBody Map<String, String> payload) {
        
        String content = payload.get("content");
        CommentDto updatedComment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("댓글 삭제 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생");
        }
    }
}
