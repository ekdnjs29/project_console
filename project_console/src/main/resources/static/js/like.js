function toggleLike(postId) {
        fetch(`/likes/${postId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            const likeIcon = document.getElementById("like-icon");
            const likeCountElement = document.getElementById("like-count");
            let likeCount = parseInt(likeCountElement.textContent);

            if (data) {
                likeCount += 1;
                likeIcon.src = "/images/like2.png"; // 좋아요 눌렀을 때 이미지
            } else {
                likeCount -= 1;
                likeIcon.src = "/images/like1.png"; // 좋아요 취소 시 이미지
            }

            likeCountElement.textContent = likeCount;
        })
        .catch(error => console.error('오류 발생:', error));
    }