package com.console.board.mapper;

import com.console.board.dtos.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 추가
    int insertComment(CommentDto commentDto);

    // 댓글 삭제
    int deleteComment(@Param("commentId") int commentId);

    // 특정 게시글의 댓글 목록 조회
    List<CommentDto> getCommentsByPostId(@Param("postId") int postId);
}
