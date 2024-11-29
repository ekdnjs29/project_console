document.addEventListener("DOMContentLoaded", function() {
    const editButton = document.getElementById("edit-button");
    const deleteButton = document.getElementById("delete-button");
    const completeButton = document.getElementById("complete-button");
    const checkboxes = document.querySelectorAll(".member-checkbox");
    const roleTexts = document.querySelectorAll(".role-text");
    const roleSelects = document.querySelectorAll(".role-select");
    const suspendedSelects = document.querySelectorAll(".suspended-select");
    const suspendedTexts = document.querySelectorAll(".suspended-text");

    editButton.addEventListener("click", function() {
        checkboxes.forEach((checkbox, index) => {
            if (checkbox.checked) {
                // roleTexts 값에 따라 roleSelects의 초기 선택값 설정
                const roleValue = roleTexts[index].textContent.trim().toUpperCase();
                roleSelects[index].value = roleValue === "ADMIN" ? "ADMIN" : "USER"; // role이 admin이면 드롭다운 기본값 admin으로 설정

                // Suspended 상태일 때만 드롭다운 표시
                const suspendedValue = suspendedTexts[index].textContent.trim().toUpperCase();
                if (suspendedValue === "SUSPENDED") {
                    suspendedSelects[index].style.display = "inline"; // Suspended 드롭다운 표시
                    suspendedSelects[index].value = "1"; // 기본값 Suspended로 설정
                } else {
                    suspendedSelects[index].value = "0"; // 기본값 Active로 설정
                }

                // Role 텍스트 숨기고 드롭다운 표시
                roleTexts[index].style.display = "none";
                roleSelects[index].style.display = "inline";
                suspendedTexts[index].style.display = "none";
            }
        });
        
        // "수정" 버튼과 "삭제" 버튼 숨기기, "완료" 버튼 표시
        editButton.style.display = "none";
        deleteButton.style.display = "none";
        completeButton.style.display = "inline";
    });

    completeButton.addEventListener("click", function() {
        const selectedMembers = [];

        checkboxes.forEach((checkbox, index) => {
            if (checkbox.checked) {
                const userId = checkbox.dataset.userId;
                const role = roleSelects[index].value;
                const suspended = suspendedSelects[index].value;

                selectedMembers.push({
                    userId: userId,
                    role: role,
                    suspended: suspended
                });

                // Role 텍스트 표시 및 드롭다운 숨기기
                roleTexts[index].style.display = "inline";
                roleSelects[index].style.display = "none";
                suspendedTexts[index].style.display = "inline";
                suspendedSelects[index].style.display = "none";
                checkbox.checked = false; // 체크박스 해제
            }
        });

        if (selectedMembers.length > 0) {
            $.ajax({
                url: "/members/update",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(selectedMembers),
                success: function(response) {
                    location.reload(); // 페이지 새로고침으로 갱신된 데이터 반영
                },
                error: function(error) {
                    alert("처리 중 오류가 발생했습니다.");
                    console.error("완료 처리 오류:", error);
                }
            });
        }

        editButton.style.display = "inline";
        deleteButton.style.display = "inline";
        completeButton.style.display = "none";
    });
});
