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

    @Transactional
    public CommentDto addComment(int postId, int userId, String content, Integer parentCommentId) {
        CommentDto commentDto = new CommentDto(postId, userId, content, parentCommentId != null ? parentCommentId : null);
        commentMapper.insertComment(commentDto);
        return commentDto;
    }

    @Transactional
    public void deleteComment(int commentId) {
        commentMapper.deleteComment(commentId);
    }

    public List<CommentDto> getCommentsByPostId(int postId) {
        return commentMapper.getCommentsByPostId(postId);
    }
}
