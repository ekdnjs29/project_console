$(document).ready(function() {
    const postId = $("#comment-form").data("post-id");
    const sessionUserId = typeof window.sessionUserId !== "undefined" ? window.sessionUserId : null;
	console.log("Loaded postId:", postId);

    // 댓글 작성 버튼 클릭 이벤트
    $("#submit-comment").on("click", function() {
        const content = $("#comment-content").val().trim();
        const parentCommentId = $("#parent-comment-id").val() 

        if (content === "") {
            alert("댓글 내용을 입력해주세요.");
            return;
        }
        

        $.ajax({
            url: `/comments/${postId}`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ content: content, parentCommentId: parentCommentId }),
            success: function(comment) {
                const commentHtml = generateCommentHtml(comment);

                if (comment.parentCommentId) {
                    $(`#replies-${comment.parentCommentId}`).append(commentHtml);
                } else {
                    $("#comment-list").append(commentHtml);
                }

                $("#comment-content").val("");
                $("#parent-comment-id").val("");
            },
            error: function(error) {
                console.error("댓글 작성 오류:", error);
                alert("댓글 작성에 실패했습니다.");
            }
        });
    });

    // 댓글 목록 가져오기
    $.ajax({
        url: `/comments/${postId}/all`,
        type: "GET",
        success: function(response) {
            if (!Array.isArray(response)) {
                console.error("응답이 배열이 아닙니다:", response);
                alert("댓글 목록을 불러오는 데 실패했습니다.");
                return;
            }

            // 댓글 목록을 화면에 표시
            response.forEach(function(comment) {
                const commentHtml = generateCommentHtml(comment);
                if (comment.parentCommentId) {
                    $(`#replies-${comment.parentCommentId}`).append(commentHtml);
                } else {
                    $("#comment-list").append(commentHtml);
                }
            });

			// 댓글 목록이 로드된 후 댓글 수 업데이트
            updateCommentCount();
            // 댓글 목록이 로드된 후 스크롤 이동
            scrollToComment();
        },
        error: function(error) {
            console.error("댓글 목록 가져오기 오류:", error);
            alert("댓글 목록을 불러오는 데 실패했습니다.");
        }
    });
    
    // 댓글 수 업데이트 함수
    function updateCommentCount() {
        const commentCount = $("#comment-list .comment-item").length; // 댓글 요소 개수 확인
        $(".comment-count span").text(commentCount); // 댓글 수 업데이트
    }

   // 특정 댓글로 스크롤 이동하는 함수
function scrollToComment() {

    const urlParams = new URLSearchParams(window.location.search);
    const commentId = urlParams.get('commentId');
    
   	console.log("스크롤 함수 호출 ", commentId);


	if (commentId) {
    const targetComment = document.getElementById("comment-" + commentId);
    if (targetComment) {
        targetComment.scrollIntoView({ behavior: "smooth", block: "center" });
        
        // 댓글 내용 부분만 강조
        const commentContent = targetComment.querySelector(".comment-content");
        if (commentContent) {
            commentContent.style.backgroundColor = "#ffffe0"; // 강조 색상
        }
    }
}


}


    // 댓글 HTML 생성 함수
    function generateCommentHtml(comment) {
    return `
        <div class="comment-item ${comment.parentCommentId ? 'reply' : ''}" 
             data-comment-id="${comment.commentId}" 
             id="comment-${comment.commentId}">
            <span class="comment-author">${comment.userNickname || '익명'}</span> 
            <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>

            ${sessionUserId !== comment.userId ? `
                <a href="javascript:void(0);" onclick="reportItem('COMMENT', ${comment.commentId})">신고</a>
            ` : `
                <div class="comment-dropdown">
                    <a class="dropdown-button" href="javascript:void(0);" onclick="toggleDropdown('comment-option-menu-${comment.commentId}')">︙</a>
                    <div id="comment-option-menu-${comment.commentId}" class="dropdown-menu">
                        <button type="button" onclick="editComment(${comment.commentId})">수정하기</button>
                        <button type="button" onclick="deleteComment(${comment.commentId})">삭제하기</button>
                    </div>
                </div>
            `}

            <p class="comment-content">${comment.content}</p>
            <textarea class="comment-edit-content" style="display:none;">${comment.content}</textarea>
            
            <div class="comment-button-container" style="display: none;">
                <button type="button" class="comment-cancel-button" onclick="cancelEdit(${comment.commentId})">취소</button>
                <button type="button" class="comment-save-button" onclick="saveEdit(${comment.commentId})">수정</button>
            </div>
            
            <button class="reply-button" type="button" onclick="replyToComment(${comment.commentId}, '${comment.userNickname}')">답글</button>

            <div class="reply-container" id="replies-${comment.commentId}"></div>
        </div>
    `;
}


    // 댓글 삭제 함수 정의
    window.deleteComment = function(commentId) {
        $.ajax({
            url: `/comments/${commentId}`,
            type: "DELETE",
            success: function() {
                $(`[data-comment-id="${commentId}"]`).remove();
            },
            error: function(error) {
                console.error("댓글 삭제 오류:", error);
                alert("댓글 삭제에 실패했습니다.");
            }
        });
    };

    // 댓글 수정 함수 정의
    window.editComment = function(commentId) {
        enableEditMode(commentId);
    };

    // 답글 버튼 클릭 시, 입력 필드에 @작성자ID 추가
    window.replyToComment = function(parentCommentId, authorNickname) {
        const commentInput = $("#comment-content");
        commentInput.val(`@${authorNickname} `);
        $("#parent-comment-id").val(parentCommentId);
        commentInput.focus();
    };
});
