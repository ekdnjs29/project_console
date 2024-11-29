package com.console.board.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePostCommand {

    @NotNull(message = "게시글 ID가 필요합니다.")
    private int postId;  // 게시글 ID

    @NotBlank(message = "제목을 입력하세요.")
    private String title; // 게시글 제목

    @NotBlank(message = "내용을 입력하세요.")
    private String content; // 게시글 내용

    private List<String> imageUrls; // 이미지 URL 목록

    // 모든 필드를 초기화하는 생성자
    public UpdatePostCommand(int postId, String title, String content, List<String> imageUrls) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }
}
