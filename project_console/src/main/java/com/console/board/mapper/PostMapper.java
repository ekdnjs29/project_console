package com.console.board.mapper;

import com.console.board.dtos.PostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    // 모든 게시글 목록 조회
    List<PostDto> getAllPosts();

    // 특정 게시글 조회
    PostDto getPostById(@Param("postId") int postId);

    // 특정 사용자의 게시글 목록 조회
    List<PostDto> findPostsByUserId(@Param("userId") int userId);

    // 새로운 게시글 추가
    int insertPost(PostDto postDto);

    // 게시글 수정
    int updatePost(PostDto postDto);

    // 게시글 삭제
    int deletePost(@Param("postId") int postId);
}
