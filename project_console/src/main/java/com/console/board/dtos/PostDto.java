package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("PostDto")
public class PostDto {
    private int postId;
    private int userId;
    private String userNickname; // 작성자 닉네임 추가
    private String title;
    private String content;
    private int commentCount;
    private int likeCount;
    private Timestamp createdAt;
    private String firstImageUrl; // 첫 번째 이미지 URL을 위한 필드 추가
    private String cleanContent; // HTML 태그가 제거된 내용 필드 추가

    
 // 좋아요 여부를 나타내는 필드 추가
    private boolean isLiked;

    // 이미지 URL 리스트 추가
    private List<String> imageUrls;
}
