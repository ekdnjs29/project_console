$(document).ready(function() {
	  
	// data-post-id 속성에서 postId 가져오기
	const postId = $("#comment-form").data("post-id");
	    
    // 댓글 작성 버튼 클릭 이벤트
    $("#submit-comment").on("click", function() {
        const content = $("#comment-content").val();

        if (content.trim() === "") {
            alert("댓글 내용을 입력해주세요.");
            return;
        }

        $.ajax({
            url: `/comments/${postId}`,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ content: content }),
            success: function(comment) {
                // 댓글 목록에 새 댓글 추가
                $("#comment-list").append(`
                    <div class="comment-item">
                        <span class="comment-author">${comment.userNickname || '익명'}</span> ·
                        <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>
                        <p class="comment-content">${comment.content}</p>
                    </div>
                `);
                $("#comment-content").val(""); // 입력 필드 초기화
            },
            error: function(error) {
                console.error("댓글 작성 오류:", error);
                alert("댓글 작성에 실패했습니다.");
            }
        });
    });

    // 목록 가져오기
    $.ajax({
        url: `/comments/${postId}/all`,
        type: "GET",
        success: function(comments) {
            comments.forEach(function(comment) {
                $("#comment-list").append(`
                    <div class="comment-item">
                        <span class="comment-author">${comment.userNickname || '익명'}</span> ·
                        <span class="comment-date">${new Date(comment.createdAt).toLocaleString()}</span>
                        <p class="comment-content">${comment.content}</p>
                    </div>
                `);
            });
        },
        error: function(error) {
            console.error("댓글 목록 가져오기 오류:", error);
            alert("댓글 목록을 불러오는 데 실패했습니다.");
        }
    });
});
