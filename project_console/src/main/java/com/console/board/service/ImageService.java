package com.console.board.service;

import com.console.board.mapper.ImageMapper;
import com.console.board.dtos.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMapper imageMapper;

    // 임시 이미지 저장
    public String saveTemporaryImage(MultipartFile imageFile, HttpServletRequest request) throws IOException {
        // 임시 업로드 경로 설정
        String uploadDir = request.getSession().getServletContext().getRealPath("temp");  // 임시 폴더로 변경
        String storedFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        File destinationFile = new File(uploadDir, storedFileName);

        System.out.println("임시 파일 저장 경로: " + destinationFile.getAbsolutePath());

        // 임시 업로드 디렉토리 생성 여부 확인 및 생성
        if (!destinationFile.getParentFile().exists()) {
            boolean dirCreated = destinationFile.getParentFile().mkdirs();
            System.out.println("임시 업로드 디렉토리 생성 여부: " + dirCreated);
        }

        // 파일을 임시로 저장
        imageFile.transferTo(destinationFile);
        System.out.println("임시 파일 저장 성공 - 파일명: " + storedFileName);

        // 클라이언트에서 접근할 수 있는 임시 URL 반환
        return "/temp/" + storedFileName;
    }

    // 최종 이미지-게시글 매핑 저장
    public void linkImagesToPost(List<String> imageUrls, int postId) {
        System.out.println("게시글 ID " + postId + "와 연결된 이미지 저장 시작");

        Set<String> uniqueImageUrls = new HashSet<>(imageUrls);

        for (String imageUrl : uniqueImageUrls) {
            if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith("/upload")) {
                ImageDto imageDto = new ImageDto();
                imageDto.setPostId(postId);
                imageDto.setImageUrl(imageUrl);
                System.out.println("최종 이미지 URL: " + imageUrl + " 를 DB에 저장합니다.");
                imageMapper.insertImage(imageDto);
            } else {
                System.out.println("임시 URL이므로 저장하지 않습니다: " + imageUrl);
            }
        }
        System.out.println("이미지와 게시글 ID 연결 완료");
    }


    
    // 오래된 임시 파일 삭제 (예시)
    public void deleteTemporaryImages() {
        File tempDir = new File("temp 경로 설정");
        File[] files = tempDir.listFiles();
        long currentTime = System.currentTimeMillis();

        if (files != null) {
            for (File file : files) {
                // 파일이 일정 시간 이상 존재했을 경우 삭제
                if (currentTime - file.lastModified() > (60 * 60 * 1000)) { // 예: 1시간 후 삭제
                    if (file.delete()) {
                        System.out.println("임시 파일 삭제 성공: " + file.getName());
                    } else {
                        System.out.println("임시 파일 삭제 실패: " + file.getName());
                    }
                }
            }
        }
    }
}
