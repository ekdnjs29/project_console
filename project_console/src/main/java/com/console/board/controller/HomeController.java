package com.console.board.controller;

import com.console.board.dtos.PostDto;
import com.console.board.dtos.UserDto;
import com.console.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

    private final PostService postService;

    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<PostDto> posts = postService.getAllPosts();
        model.addAttribute("postList", posts);
        return "home";
    }

    // 게시글 필터링 메서드 추가
    @GetMapping("/posts/filter")
    public String filterPosts(@RequestParam("filter") String filter, Model model, HttpSession session) {
        List<PostDto> posts;

        if ("myPosts".equals(filter)) {
            UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
            if (loggedInUser != null) {
                posts = postService.getPostsByUserId(loggedInUser.getUserId());
            } else {
                posts = List.of(); // 로그인되지 않은 경우 빈 리스트 반환
            }
        } else {
            posts = postService.getAllPosts();
        }

        model.addAttribute("postList", posts);
        return "partials/postList"; // 필터링된 게시글 부분만 반환
    }

}
