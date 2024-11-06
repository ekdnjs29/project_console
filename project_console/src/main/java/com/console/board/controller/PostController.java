package com.console.board.controller;

import com.console.board.command.InsertPostCommand;
import com.console.board.command.UpdatePostCommand;
import com.console.board.dtos.PostDto;
import com.console.board.dtos.UserDto;
import com.console.board.service.PostService;
import com.console.board.service.ImageService; 
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ImageService imageService;

    @Autowired
    public PostController(PostService postService, ImageService imageService) {
        this.postService = postService;
        this.imageService = imageService; 
    }
    
 // 게시글 작성 폼 페이지로 이동
    @GetMapping("/write")
    public String showWriteForm(Model model) {
        model.addAttribute("insertPostCommand", new InsertPostCommand());
        model.addAttribute("isWritingForm", true); // 글 작성 폼 여부 설정

        return "post/writeForm"; // 게시글 작성 폼 뷰 페이지
    }

 // 게시글 작성 처리
    @PostMapping("/write")
    public String createPost(@Valid InsertPostCommand insertPostCommand, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "post/writeForm";
        }

        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        insertPostCommand.setUserId(loggedInUser.getUserId());

        int postId = postService.insertPost(insertPostCommand);
        if (postId <= 0) {
            return "post/writeForm";
        }

        // /temp/ URL을 /upload/로 변환하고 중복 제거하여 최종 URL만 저장
        List<String> finalImageUrls = insertPostCommand.getImageUrls().stream()
                .filter(url -> url != null && !url.isEmpty() && url.startsWith("/temp/"))
                .map(url -> url.replace("/temp/", "/upload/"))
                .distinct()
                .toList();

        if (!finalImageUrls.isEmpty()) {
            imageService.linkImagesToPost(finalImageUrls, postId);
        }

        System.out.println("게시글 저장 성공, 홈으로 리다이렉트");
        return "redirect:/";
    }


    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public String getPost(@PathVariable int postId, Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        int userId = loggedInUser != null ? loggedInUser.getUserId() : -1; // 로그인한 사용자 ID (없으면 -1)

        PostDto post = postService.getPostWithLikeStatus(postId, userId);

        if (post == null) {
            return "redirect:/"; // 게시글이 없으면 목록으로 리다이렉트
        }

        boolean isAuthor = loggedInUser != null && loggedInUser.getUserId() == post.getUserId();
        model.addAttribute("post", post);
        model.addAttribute("isAuthor", isAuthor);

        return "post/postDetail";
    }

    // 게시글 수정 폼 페이지로 이동
    @GetMapping("/edit/{postId}")
    public String showEditForm(@PathVariable int postId, Model model) {
        PostDto post = postService.getPost(postId);
        model.addAttribute("updatePostCommand", new UpdatePostCommand(post.getPostId(), post.getTitle(), post.getContent()));
        model.addAttribute("isWritingForm", true); // 글 작성 폼 여부 설정

        return "post/editForm"; // 게시글 수정 폼 뷰 페이지
    }

 // 게시글 수정 처리
    @PostMapping("/edit") // URL 경로에서 postId를 받음
    public String updatePost(@PathVariable int postId, @Valid UpdatePostCommand updatePostCommand, BindingResult result) {
        if (result.hasErrors()) {
            return "post/editForm"; // 유효성 검사 실패 시 다시 수정 폼으로 이동
        }
        updatePostCommand.setPostId(postId); // postId를 UpdatePostCommand에 설정
        postService.updatePost(updatePostCommand);
        return "redirect:/posts/" + postId; // 수정 완료 후 게시글 상세 페이지로 리다이렉트
    }


    // 게시글 삭제
    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable int postId) {
        postService.deletePost(postId);
        return "redirect:/"; // 삭제 완료 후 게시글 목록으로 리다이렉트
    }
}
