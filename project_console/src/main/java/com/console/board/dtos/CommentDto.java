package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("CommentDto")
public class CommentDto {
    private int commentId;
    private int postId;
    private int userId;
    private String content;
    private Integer parentCommentId;
    private boolean reported;
    private Timestamp createdAt;
    private String userNickname; // 작성자 닉네임 필드 추가


	public CommentDto(int postId, int userId, String content, Integer parentCommentId) {
	    this.postId = postId;
	    this.userId = userId;
	    this.content = content;
	    this.parentCommentId = parentCommentId;
	}
}