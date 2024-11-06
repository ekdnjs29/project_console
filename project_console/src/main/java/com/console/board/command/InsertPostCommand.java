package com.console.board.command;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InsertPostCommand {

    private int userId;  // 작성자 ID

    @NotBlank(message = "제목을 입력하세요.")
    private String title; // 게시글 제목

    @NotBlank(message = "내용을 입력하세요.")
    private String content; // 게시글 내용
    
    private List<String> imageUrls; // 이미지 URL 리스트 추가

 // 모든 필드를 초기화하는 생성자
    public InsertPostCommand(int userId, String title, String content, List<String> imageUrls) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls; // 이미지 URL 초기화
    }
    
}
