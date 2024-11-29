package com.console.board.controller;

import com.console.board.command.InsertPostCommand;
import com.console.board.command.UpdatePostCommand;
import com.console.board.dtos.PostDto;
import com.console.board.dtos.UserDto;
import com.console.board.service.PostService;
import com.console.board.service.ImageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public String createPost(@Valid PostDto postDto, InsertPostCommand insertPostCommand, 
    		BindingResult result, HttpSession session, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "post/writeForm";
        }

        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            return "redirect:/user/login";
        }

        insertPostCommand.setUserId(loggedInUser.getUserId());

        // 이미지 이동 및 최종 URL 생성
        List<String> finalImageUrls = imageService.moveImagesToUpload(insertPostCommand.getImageUrls(), request);
        String updatedContent = postDto.getContent().replace("/temp/", "/upload/");
        postDto.setContent(updatedContent);
        insertPostCommand.setContent(updatedContent);

        // 게시글 저장
        int postId = postService.insertPost(insertPostCommand);
        if (postId <= 0) {
            return "post/writeForm";
        }

        // 이미지와 게시글 ID 연결
        imageService.linkImagesToPost(finalImageUrls, new ArrayList<>(), postId, request);

        return "redirect:/";
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public String getPost(@PathVariable int postId, Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        int userId = loggedInUser != null ? loggedInUser.getUserId() : -1;

        PostDto post = postService.getPostWithLikeStatus(postId, userId);
        if (post == null) {
            return "redirect:/"; // 게시글이 없으면 목록으로 리다이렉트
        }

        boolean isAuthor = loggedInUser != null && loggedInUser.getUserId() == post.getUserId();
        model.addAttribute("post", post);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isDetailForm", true); 

        return "post/postDetail";
    }

 // 게시글 수정 폼 페이지로 이동
    @GetMapping("/edit/{postId}")
    public String showEditForm(@PathVariable int postId, Model model, HttpServletRequest request) {
        PostDto post = postService.getPost(postId);
        if (post == null) {
            throw new IllegalStateException("해당 게시글이 존재하지 않습니다: " + postId);
        }

        // 기존 이미지 리스트를 세션에 저장
        List<String> existingImageUrls = imageService.getImageUrlsByPostId(postId);
        request.getSession().setAttribute("existingImageUrls", existingImageUrls);

        model.addAttribute("updatePostCommand", new UpdatePostCommand(post.getPostId(), post.getTitle(), 
        		post.getContent(), existingImageUrls));
        model.addAttribute("isWritingForm", true);

        return "post/editForm";
    }

    // 게시글 수정 처리
    @PostMapping("/edit/{postId}")
    public String updatePost(
            @PathVariable int postId, 
            @Valid UpdatePostCommand updatePostCommand, 
            BindingResult result, 
            HttpServletRequest request) {

        if (result.hasErrors()) {
            return "post/editForm";
        }

        // 세션에서 기존 이미지 목록을 가져와 삭제할 이미지와 새로 추가된 이미지 구분
        @SuppressWarnings("unchecked")
        List<String> existingImageUrls = (List<String>) request.getSession().getAttribute("existingImageUrls");
        if (existingImageUrls == null) {
            throw new IllegalStateException("기존 이미지 정보가 없습니다.");
        }

        // 최종 이미지 목록을 준비하고 삭제할 이미지 식별
        List<String> finalImageUrls = imageService.prepareImagesForUpdate(updatePostCommand.getImageUrls(), existingImageUrls, request);

        // 본문 내용의 이미지 경로를 temp -> upload로 변환
        String updatedContent = updatePostCommand.getContent().replace("/temp/", "/upload/");
        updatePostCommand.setContent(updatedContent);

        // 게시글 수정
        postService.updatePost(updatePostCommand);

        // 최종 이미지와 게시글 ID 연결
        imageService.linkImagesToPost(finalImageUrls, new ArrayList<>(), postId, request);

        // 세션에서 기존 이미지 리스트 제거
        request.getSession().removeAttribute("existingImageUrls");

        return "redirect:/posts/" + postId;
    }



    // 게시글 삭제
    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable int postId, HttpServletRequest request) {
        imageService.deletePhysicalImagesByPostId(postId, request);
        postService.deletePost(postId);
        return "redirect:/";
    }
}
