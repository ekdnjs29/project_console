package com.console.board.service;

import com.console.board.dtos.CommentDto;
import com.console.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    // 댓글 추가
    @Transactional
    public CommentDto addComment(int postId, int userId, String content, Integer parentCommentId) {
        CommentDto commentDto = new CommentDto(postId, userId, content, parentCommentId);
        commentMapper.insertComment(commentDto);
        return commentDto;
    }

    // 계층적으로 정렬된 댓글 조회
    public List<CommentDto> getCommentsByPostIdOrdered(int postId) {
        List<CommentDto> comments = commentMapper.getCommentsByPostIdOrdered(postId);
        return comments;
    }

    // 댓글 수정
    @Transactional
    public CommentDto updateComment(int commentId, String content) {
        commentMapper.updateComment(commentId, content);
        return commentMapper.getCommentById(commentId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId) {
        commentMapper.deleteComment(commentId);
    }
}
