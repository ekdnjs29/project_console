// 신고 사유 입력 및 AJAX 요청 함수
function reportItem(targetType, targetId) {
    const reason = prompt("신고 사유를 입력하세요:");
    if (!reason) return; // 사유가 없으면 요청하지 않음

    $.ajax({
        url: "/report",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            targetType: targetType, // 'POST' 또는 'COMMENT'
            targetId: targetId,
            reason: reason
        }),
        error: function(error) {
            alert("신고 접수에 실패했습니다.");
            console.error("신고 오류:", error);
        }
    });
}

// 수정 버튼 클릭 시 드롭다운 메뉴 보이기
document.addEventListener("DOMContentLoaded", function() {
    const editButton = document.getElementById("edit-button");
    const deleteButton = document.getElementById("delete-button");
    const completeButton = document.getElementById("complete-button");
    completeButton.setAttribute("type", "button");

    const checkboxes = document.querySelectorAll(".report-checkbox");
    const suspendSelects = document.querySelectorAll(".suspend-select");

    // "수정" 버튼 클릭 시 이벤트 추가
    editButton.addEventListener("click", function() {
        checkboxes.forEach((checkbox, index) => {
            if (checkbox.checked) {
                // 체크된 항목만 드롭다운 표시
                suspendSelects[index].style.display = "block";
            }
        });

        // "수정" 버튼과 "삭제" 버튼 숨기기, "완료" 버튼 표시
        editButton.style.display = "none";
        deleteButton.style.display = "none";
        completeButton.style.display = "inline";
    });

    // "완료" 버튼 클릭 시 이벤트 추가
completeButton.addEventListener("click", function() {
    const selectedReports = [];

    checkboxes.forEach((checkbox, index) => {
        if (checkbox.checked) {
            const reportId = checkbox.dataset.reportId;       // reportId 가져오기
            const targetType = checkbox.dataset.targetType;   // targetType 가져오기
            const targetId = checkbox.dataset.targetId;       // targetId 가져오기
            const reason = checkbox.dataset.reason;           // 신고 사유 가져오기
            const suspendedDays = suspendSelects[index].value; // 선택된 정지 기간 가져오기

            // 모든 필드를 객체에 추가하여 selectedReports에 저장
            selectedReports.push({
                reportId: reportId,
                targetType: targetType,
                targetId: targetId,
                reason: reason,
				suspendedDays: parseInt(suspendSelects[index].value, 10)
            });

            // 드롭다운 숨기기 및 체크박스 해제
            suspendSelects[index].style.display = "none";
            checkbox.checked = false;
        }
    });

    if (selectedReports.length > 0) {
        $.ajax({
            url: "/report/resolve",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(selectedReports),
            success: function(response) {
                location.reload(); // 페이지 새로고침으로 갱신된 데이터 반영
            },
            error: function(error) {
                alert("처리 중 오류가 발생했습니다. 상태 코드: " + error.status);
    console.error("완료 처리 오류:", error.responseText);
            }
        });
    }

    // "수정" 버튼과 "삭제" 버튼 다시 표시, "완료" 버튼 숨기기
    editButton.style.display = "inline";
    deleteButton.style.display = "inline";
    completeButton.style.display = "none";
});

});
