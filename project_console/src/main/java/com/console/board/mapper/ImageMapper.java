package com.console.board.mapper;

import com.console.board.dtos.ImageDto;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper {
    // 이미지 정보를 DB에 삽입
    int insertImage(ImageDto imageDto);

    // 특정 게시글의 모든 이미지 조회
    List<ImageDto> selectImagesByPostId(int postId);
}
