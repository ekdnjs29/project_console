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
    
 // 좋아요 여부를 나타내는 필드 추가
    private boolean isLiked;

    // 이미지 URL 리스트 추가
    private List<String> imageUrls;
}
