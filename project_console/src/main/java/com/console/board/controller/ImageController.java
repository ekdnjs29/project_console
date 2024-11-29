package com.console.board.controller;

import com.console.board.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 이미지 임시 업로드 및 URL 반환
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile imageFile, HttpServletRequest request) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어 있습니다."));
        }

        try {
            String imageUrl = imageService.saveTemporaryImage(imageFile, request);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }

 // 임시 이미지 삭제
    @PostMapping("/deleteTempImage")
    public ResponseEntity<Void> deleteTempImage(@RequestBody Map<String, String> requestData) {
        String imageUrl = requestData.get("imageUrl");
        boolean isDeleted = imageService.deleteTempImage(imageUrl);

        return isDeleted ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
