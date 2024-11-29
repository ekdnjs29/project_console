package com.console.board.service;

import com.console.board.command.InsertPostCommand;
import com.console.board.command.UpdatePostCommand;
import com.console.board.dtos.PostDto;
import com.console.board.dtos.UserDto;
import com.console.board.dtos.ImageDto;
import com.console.board.mapper.LikeMapper;
import com.console.board.mapper.CommentMapper;
import com.console.board.mapper.PostMapper;
import com.console.board.mapper.ImageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Safelist;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final ImageMapper imageMapper;

    // 글 추가
    @Transactional
    public int insertPost(InsertPostCommand insertPostCommand) {
        PostDto postDto = new PostDto();
        postDto.setUserId(insertPostCommand.getUserId());
        postDto.setTitle(insertPostCommand.getTitle());
        postDto.setContent(insertPostCommand.getContent());
        postDto.setCommentCount(0);
        postDto.setLikeCount(0);
        postDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 데이터베이스에 게시글 삽입
        postMapper.insertPost(postDto);

        return postDto.getPostId(); // 생성된 postId 반환
    }
    
    // 게시글 저장 후 생성된 postId 반환
    @Transactional
    public int savePost(PostDto postDto) {
        postDto.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 현재 시간 설정
        postMapper.insertPost(postDto);
        return postDto.getPostId(); // 생성된 postId 반환
    }
    
    // 좋아요 상태를 포함하여 게시글 조회
    public PostDto getPostWithLikeStatus(int postId, int userId) {
        PostDto post = postMapper.getPostById(postId);
        
        if (post != null) {
            // 작성자의 닉네임 설정
            UserDto user = userService.getUserById(post.getUserId());
            if (user != null) {
                post.setUserNickname(user.getNickname());
            }
            
            // 사용자가 게시글에 좋아요를 눌렀는지 확인
            post.setLiked(likeMapper.isLikedByUser(postId, userId) > 0);
            
            // 게시글의 총 좋아요 수 설정
            int likeCount = likeMapper.countLikesByPostId(postId);
            post.setLikeCount(likeCount);
        }
        return post;
    }

    // 특정 사용자의 게시글 목록 가져오기
    public List<PostDto> getPostsByUserId(int userId) {
        List<PostDto> posts = postMapper.findPostsByUserId(userId);

        // 각 게시글에 첫 번째 이미지 URL, 좋아요 수, 댓글 수 설정
        for (PostDto post : posts) {
            // 첫 번째 이미지 설정
            List<ImageDto> images = imageMapper.selectImagesByPostId(post.getPostId());
            if (!images.isEmpty()) {
                String firstImageUrl = images.get(0).getImageUrl();
                post.setFirstImageUrl(firstImageUrl);
            } else {
                post.setFirstImageUrl(null);
            }

            // 좋아요 수 설정
            int likeCount = likeMapper.countLikesByPostId(post.getPostId());
            post.setLikeCount(likeCount);

            // 댓글 수 설정
            int commentCount = commentMapper.getCommentCount(post.getPostId());
            post.setCommentCount(commentCount);

            // HTML 태그와 코드모드 내용을 제거 후 cleanContent에 저장
            post.setCleanContent(filterCodeModeContent(post.getContent()));
        }
        return posts;
    }

    // 글 상세 조회
    public PostDto getPost(int postId, int userId) {
        PostDto post = postMapper.getPostById(postId);

        if (post != null) {
            UserDto user = userService.getUserById(post.getUserId());
            if (user != null) {
                post.setUserNickname(user.getNickname());
            }
            // 좋아요 여부 확인
            post.setLiked(likeMapper.isLikedByUser(postId, userId) > 0);
        }

        return post;
    }
    
    public PostDto getPost(int postId) {
        PostDto post = postMapper.getPostById(postId);
        return post;
    }

    // 게시글 목록 가져오기
    public List<PostDto> getAllPosts() {
        List<PostDto> posts = postMapper.getAllPosts();  

        // 각 게시글에 첫 번째 이미지 URL, 좋아요 수, 댓글 수 설정
        for (PostDto post : posts) {
            // 첫 번째 이미지 설정
            List<ImageDto> images = imageMapper.selectImagesByPostId(post.getPostId());
            if (!images.isEmpty()) {
                String firstImageUrl = images.get(0).getImageUrl();
                post.setFirstImageUrl(firstImageUrl);
            } else {
                post.setFirstImageUrl(null);
            }

            // 좋아요 수 설정
            int likeCount = likeMapper.countLikesByPostId(post.getPostId());
            post.setLikeCount(likeCount);

            // 댓글 수 설정
            int commentCount = commentMapper.getCommentCount(post.getPostId());
            post.setCommentCount(commentCount);

            // HTML 태그와 코드모드 내용을 제거 후 cleanContent에 저장
            post.setCleanContent(filterCodeModeContent(post.getContent()));
        }
        return posts;
    }

    // 코드모드 내용을 필터링하는 메서드
    private String filterCodeModeContent(String content) {
        Document doc = Jsoup.parse(content);
        Elements codeElements = doc.select("code"); // 코드모드 태그를 선택
        for (Element codeElement : codeElements) {
            codeElement.remove(); // 코드모드 내용을 제거
        }
        return Jsoup.clean(doc.body().html(), Safelist.none()); // HTML 태그도 제거하여 cleanContent에 저장
    }

    // 글 수정
    public boolean updatePost(UpdatePostCommand updatePostCommand) {
        PostDto postDto = new PostDto();
        postDto.setPostId(updatePostCommand.getPostId());
        postDto.setTitle(updatePostCommand.getTitle());
        postDto.setContent(updatePostCommand.getContent());

        return postMapper.updatePost(postDto) > 0;
    }

    // 글 삭제
    public boolean deletePost(int postId) {
        return postMapper.deletePost(postId) > 0;
    }
}
