package com.console.board.service;


import com.console.board.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper likeMapper;

    public boolean toggleLike(int postId, int userId) {
        if (likeMapper.isLikedByUser(postId, userId) > 0) {
            likeMapper.deleteLike(postId, userId);
            return false; // 좋아요 취소됨
        } else {
            likeMapper.insertLike(postId, userId);
            return true; // 좋아요 추가됨
        }
    }

    public int getLikeCount(int postId) {
        return likeMapper.countLikesByPostId(postId);
    }
}

