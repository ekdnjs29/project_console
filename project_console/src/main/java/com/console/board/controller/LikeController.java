package com.console.board.controller;

import com.console.board.dtos.UserDto;
import com.console.board.service.LikeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(@PathVariable int postId, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            System.out.println("좋아요 요청 실패: 사용자가 로그인되지 않음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("좋아요 요청 처리 중 - postId: " + postId + ", userId: " + loggedInUser.getUserId());
        boolean isLiked = likeService.toggleLike(postId, loggedInUser.getUserId());
        return ResponseEntity.ok(isLiked);
    }

}