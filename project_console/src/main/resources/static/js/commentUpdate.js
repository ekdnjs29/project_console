// 수정 모드 활성화 함수
window.enableEditMode = function(commentId) {
    const commentItem = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    console.log("commentItem HTML:", commentItem ? commentItem.innerHTML : "commentItem not found");

    if (!commentItem) {
        console.error(`Comment item with ID ${commentId} not found.`);
        return;
    }

    const contentElement = commentItem.querySelector(".comment-content");
    const editContentElement = commentItem.querySelector(".comment-edit-content");
    const saveButton = commentItem.querySelector(".comment-save-button");
    const cancelButton = commentItem.querySelector(".comment-cancel-button");
    const buttonContainer = commentItem.querySelector(".comment-button-container");

	const dropdownMenu = commentItem.querySelector(".comment-dropdown");
    const replyButton = commentItem.querySelector(".reply-button");

    console.log("contentElement:", contentElement);
    console.log("editContentElement:", editContentElement);
    console.log("saveButton:", saveButton);
    console.log("cancelButton:", cancelButton);

    if (!editContentElement) {
        console.error(`Edit content element for comment ID ${commentId} not found.`);
        alert("수정할 수 없습니다. 댓글 수정 필드가 누락되었습니다.");
        return;
    }

    editContentElement.value = contentElement.innerText;
    contentElement.style.display = "none";
    editContentElement.style.display = "block";

    if (saveButton && cancelButton) {
        saveButton.style.display = "inline";
        cancelButton.style.display = "inline";
    } else {
        console.error("Save or Cancel button not found for comment ID:", commentId);
    }
    // 드롭다운 메뉴와 답글 버튼 숨기기
    if (dropdownMenu) {
        dropdownMenu.style.display = "none";
    }
    if (replyButton) {
        replyButton.style.display = "none";
    }
     buttonContainer.style.display = "flex"; // 수정 버튼 컨테이너 표시

};

// 댓글 수정 저장 함수
function saveEdit(commentId) {
    const commentItem = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);
    const editContentElement = commentItem.querySelector(".comment-edit-content");

    if (!editContentElement) {
        console.error("Edit content element not found.");
        alert("수정할 수 없습니다. 댓글 수정 필드가 누락되었습니다.");
        return;
    }

    const updatedContent = editContentElement.value.trim();

    if (updatedContent === "") {
        alert("댓글 내용을 입력해주세요.");
        return;
    }

    // 서버에 수정된 댓글을 저장하는 요청 보내기
    $.ajax({
        url: `/comments/${commentId}`,
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify({ content: updatedContent }),
        success: function(response) {
            // 댓글 내용을 서버 응답으로 업데이트
            const contentElement = commentItem.querySelector(".comment-content");
            contentElement.innerText = response.content;

            // 수정 모드 종료
            cancelEdit(commentId);
        },
        error: function(error) {
            console.error("댓글 수정 오류:", error);
            alert("댓글 수정에 실패했습니다.");
        }
    });
}

// 수정 모드 취소 함수
window.cancelEdit = function(commentId) {
    const commentItem = document.querySelector(`.comment-item[data-comment-id="${commentId}"]`);

    if (!commentItem) {
        console.error(`Comment item with ID ${commentId} not found.`);
        return;
    }

    const contentElement = commentItem.querySelector(".comment-content");
    const editContentElement = commentItem.querySelector(".comment-edit-content");
    const saveButton = commentItem.querySelector(".comment-save-button");
    const cancelButton = commentItem.querySelector(".comment-cancel-button");
	const dropdownMenu = commentItem.querySelector(".comment-dropdown");
    const replyButton = commentItem.querySelector(".reply-button");
    const buttonContainer = commentItem.querySelector(".comment-button-container");

    if (contentElement && editContentElement && saveButton && cancelButton) {
        // 수정 모드 취소: 원래 내용 보이기, 수정 필드 숨기기
        editContentElement.style.display = "none";
        contentElement.style.display = "block";
        saveButton.style.display = "none";
        cancelButton.style.display = "none";
    } else {
        console.error("One or more elements not found for comment ID:", commentId);
    }
    // 드롭다운 메뉴와 답글 버튼 다시 표시
    if (dropdownMenu) {
        dropdownMenu.style.display = "block";
    }
    if (replyButton) {
        replyButton.style.display = "inline";
    }
        buttonContainer.style.display = "none"; // 수정 버튼 컨테이너 숨기기

};
