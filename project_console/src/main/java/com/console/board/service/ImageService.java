package com.console.board.service;

import com.console.board.mapper.ImageMapper;
import com.console.board.dtos.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageMapper imageMapper;
    private static final String TEMP_DIR_PATH = System.getProperty("user.dir") + "/src/main/webapp/temp";
    private static final String UPLOAD_DIR_PATH = System.getProperty("user.dir") + "/src/main/webapp/upload";

 // 임시 디렉토리에 이미지 파일 저장 및 URL 반환
 public String saveTemporaryImage(MultipartFile imageFile, HttpServletRequest request) throws IOException {
     // UUID로 고유한 파일 이름 생성 (ASCII 문자만 포함된 파일명)
     String originalFileName = imageFile.getOriginalFilename();
     String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
     String storedFileName = UUID.randomUUID() + extension; // UUID로 파일 이름 변경

     // 저장 경로 설정
     String uploadDir = request.getSession().getServletContext().getRealPath("temp");
     Path destinationPath = Path.of(uploadDir, storedFileName);

     // 디렉토리가 없으면 생성
     Files.createDirectories(destinationPath.getParent());

     // 파일 저장
     imageFile.transferTo(destinationPath.toFile());

     System.out.println("임시 파일 저장 성공 - 파일명: " + storedFileName);
     return "/temp/" + storedFileName;
 }

    // 임시 이미지를 업로드 디렉토리로 이동
    public void moveImageToUploadDir(String tempImagePath) {
        try {
            Path sourcePath = Path.of(tempImagePath);
            Path targetPath = Path.of(tempImagePath.replace("/temp/", "/upload/"));

            Files.createDirectories(targetPath.getParent());
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("파일 이동 성공: " + targetPath);
        } catch (Exception e) {
            System.err.println("파일 이동 실패: " + e.getMessage());
        }
    }

    // 주어진 이미지 URL들을 temp에서 upload 디렉토리로 이동시키고 최종 URL 목록 반환
    public List<String> moveImagesToUpload(List<String> imageUrls, HttpServletRequest request) {
        return imageUrls.stream()
                .filter(url -> url != null && !url.isEmpty() && url.startsWith("/temp/"))
                .map(url -> {
                    String uploadUrl = url.replace("/temp/", "/upload/");
                    String absoluteTempPath = request.getServletContext().getRealPath(url);
                    moveImageToUploadDir(absoluteTempPath);
                    return uploadUrl;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    // 기존 이미지와 새로 추가된 이미지를 처리하여 최종 이미지 URL 목록을 반환
    public List<String> prepareImagesForUpdate(List<String> submittedImageUrls, List<String> existingImageUrls, HttpServletRequest request) {
        List<String> keptImageUrls = submittedImageUrls.stream()
                .filter(existingImageUrls::contains)
                .collect(Collectors.toList());

        List<String> deletedImages = existingImageUrls.stream()
                .filter(url -> !submittedImageUrls.contains(url))
                .collect(Collectors.toList());

        List<String> newImageUrls = submittedImageUrls.stream()
                .filter(url -> url != null && !url.isEmpty() && url.startsWith("/temp/"))
                .map(url -> {
                    String uploadUrl = url.replace("/temp/", "/upload/");
                    String absoluteTempPath = request.getServletContext().getRealPath(url);
                    moveImageToUploadDir(absoluteTempPath);
                    return uploadUrl;
                })
                .distinct()
                .collect(Collectors.toList());

        List<String> finalImageUrls = new ArrayList<>(keptImageUrls);
        finalImageUrls.addAll(newImageUrls);

        // 삭제된 이미지 파일 및 DB에서 제거
        for (String deletedImageUrl : deletedImages) {
            deleteImageByUrl(deletedImageUrl);
            deletePhysicalImage(deletedImageUrl, request);
        }

        return finalImageUrls;
    }

    // 이미지 URL을 데이터베이스에서 삭제
    public void deleteImageByUrl(String imageUrl) {
        imageMapper.deleteImageByUrl(imageUrl);
        System.out.println("이미지 URL 삭제 완료: " + imageUrl);
    }

    // 물리적 이미지 파일 삭제
    public void deletePhysicalImage(String imageUrl, HttpServletRequest request) {
        String absolutePath = request.getServletContext().getRealPath(imageUrl);
        File file = new File(absolutePath);
        if (file.exists() && file.delete()) {
            System.out.println("물리적 파일 삭제 성공: " + absolutePath);
        } else {
            System.out.println("파일이 존재하지 않거나 삭제 실패: " + absolutePath);
        }
    }

    // 특정 postId에 대한 이미지 URL 목록 반환
    public List<String> getImageUrlsByPostId(int postId) {
        return imageMapper.selectImagesByPostId(postId)
                          .stream()
                          .map(ImageDto::getImageUrl)
                          .collect(Collectors.toList());
    }

    // 이미지와 게시글 간의 최종 매핑 저장 및 삭제된 이미지 처리
    public void linkImagesToPost(List<String> imageUrls, List<String> deletedImageUrls, int postId, HttpServletRequest request) {
        List<String> existingImageUrls = getImageUrlsByPostId(postId);

        for (String deletedImageUrl : deletedImageUrls) {
            if (existingImageUrls.contains(deletedImageUrl)) {
                deleteImageByUrl(deletedImageUrl);
                deletePhysicalImage(deletedImageUrl, request);
            }
        }

        Set<String> uniqueImageUrls = new HashSet<>(imageUrls);
        for (String imageUrl : uniqueImageUrls) {
            if (!existingImageUrls.contains(imageUrl)) {
                ImageDto imageDto = new ImageDto();
                imageDto.setPostId(postId);
                imageDto.setImageUrl(imageUrl);
                imageMapper.insertImage(imageDto);
            }
        }
    }

    // 임시 이미지 삭제
    public boolean deleteTempImage(String imageUrl) {
        String filePath = TEMP_DIR_PATH + imageUrl.replace("/temp", "");
        File file = new File(filePath);
        if (file.exists() && file.delete()) {
            System.out.println("임시 파일 삭제 성공: " + filePath);
            return true;
        }
        System.out.println("임시 파일 삭제 실패: " + filePath);
        return false;
    }

    // 게시물과 관련된 모든 이미지 파일 삭제
    public void deletePhysicalImagesByPostId(int postId, HttpServletRequest request) {
        List<ImageDto> images = imageMapper.selectImagesByPostId(postId);
        for (ImageDto image : images) {
            deletePhysicalImage(image.getImageUrl(), request);
        }
    }

    // 오래된 임시 파일 자동 삭제
    public void deleteTemporaryImages() {
        File tempDir = new File(TEMP_DIR_PATH);
        File[] files = tempDir.listFiles();
        long currentTime = System.currentTimeMillis();

        if (files != null) {
            for (File file : files) {
                if (currentTime - file.lastModified() > (60 * 60 * 1000)) {
                    if (file.delete()) {
                        System.out.println("임시 파일 삭제 성공: " + file.getName());
                    } else {
                        System.out.println("임시 파일 삭제 실패: " + file.getName());
                    }
                }
            }
        } else {
            System.out.println("임시 디렉토리에 파일이 없습니다.");
        }
    }
}
