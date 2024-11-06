package com.console.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    int isLikedByUser(@Param("postId") int postId, @Param("userId") int userId);

    // 좋아요 추가
    int insertLike(@Param("postId") int postId, @Param("userId") int userId);

    // 좋아요 삭제
    int deleteLike(@Param("postId") int postId, @Param("userId") int userId);

    // 특정 게시글의 좋아요 개수 조회
    int countLikesByPostId(@Param("postId") int postId);
}
