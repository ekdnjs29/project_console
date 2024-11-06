package com.console.board.controller;

import com.console.board.service.ImageService;
import com.console.board.dtos.PostDto;
import com.console.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/uploadImage")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageService imageService;
    private final PostService postService;

    // 이미지 임시 업로드 및 URL 반환
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile imageFile, HttpServletRequest request) {
        System.out.println("파일 업로드 시도 - 파일명: " + imageFile.getOriginalFilename() + ", 파일 크기: " + imageFile.getSize());

        if (imageFile.isEmpty()) {
            System.out.println("업로드 실패 - 파일이 비어 있습니다.");
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어 있습니다."));
        }

        try {
            // 임시로 이미지를 저장하고 URL을 반환
            String imageUrl = imageService.saveTemporaryImage(imageFile, request);  // 임시 저장 메서드 사용
            System.out.println("파일 업로드 성공 - 이미지 URL: " + imageUrl);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));  // 임시 URL만 반환하여 글쓰기 화면에서 표시
        } catch (IOException e) {
            System.out.println("파일 업로드 중 IOException 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }

}
