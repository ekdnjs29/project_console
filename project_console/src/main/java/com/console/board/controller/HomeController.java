package com.console.board.controller;

import com.console.board.dtos.PostDto;
import com.console.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        return "home"; // 홈 페이지 템플릿으로 이동
    }
}
