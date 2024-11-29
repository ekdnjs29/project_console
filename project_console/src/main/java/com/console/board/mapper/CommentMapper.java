package com.console.board.mapper;

import com.console.board.dtos.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 추가
    int insertComment(CommentDto commentDto);

    // 특정 게시글의 댓글 목록 조회
    List<CommentDto> getCommentsByPostId(@Param("postId") int postId);
    
    // 댓글 수정 
    int updateComment(@Param("commentId") int commentId, @Param("content") String content);

    // 특정 댓글 조회 (수정 후 반환용)
    CommentDto getCommentById(@Param("commentId") int commentId);
    
    // 댓글 삭제
    int deleteComment(@Param("commentId") int commentId);
    
    // 특정 게시글의 댓글 수 조회
    int getCommentCount(@Param("postId") int postId);
    
 // 특정 게시글의 댓글 목록을 계층적으로 조회 (parent_comment_id 순서로 정렬)
    List<CommentDto> getCommentsByPostIdOrdered(@Param("postId") int postId);


    // 특정 사용자의 역할과 정지 상태 업데이트
    int updateUserRoleAndSuspended(@Param("userId") int userId,
                                   @Param("role") String role,
                                   @Param("suspended") int suspended);
    
}
