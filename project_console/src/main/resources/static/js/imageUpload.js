// 이미지 URL을 hidden 필드에 추가하는 함수
function addImageUrl(imageUrl) {
    const imageUrlsInput = document.getElementById('imageUrlsInput');
    const currentUrls = new Set(imageUrlsInput.value.split(',')); // 기존 URL을 Set으로 가져오기 (중복 방지)
    currentUrls.add(imageUrl); // 새로운 URL 추가
    imageUrlsInput.value = Array.from(currentUrls).join(','); // 콤마로 구분된 문자열로 저장
}

// 이미지 업로드 관련 이벤트 리스너
document.addEventListener("paste", function (event) {
    const items = (event.clipboardData || event.originalEvent.clipboardData).items;
    for (const item of items) {
        if (item.kind === "file" && item.type.startsWith("image/")) {
			event.preventDefault(); // 기본 붙여넣기 방지

            const file = item.getAsFile();
            uploadImage(file); // 파일 업로드 함수 호출
        }
    }
});

// 이미지 업로드 함수
function uploadImage(file) {
    const formData = new FormData();
    formData.append("image", file);

    fetch("/uploadImage", {
        method: "POST",
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        if (data.imageUrl) {
            addImageUrl(data.imageUrl); // URL 추가
            
            // 이미지 태그를 contentInput div에 직접 추가
            const contentInput = document.getElementById("contentInput");
            const imgElement = `<img src="${data.imageUrl}" alt="첨부 이미지" style="max-width:100%;">`;
            contentInput.innerHTML += imgElement;
            contentInput.focus(); // 포커스를 유지
        } else {
            alert("이미지 업로드 중 오류 발생: " + data.error);
        }
    })
    .catch(error => {
        console.error("이미지 업로드 실패:", error);
        alert("이미지 업로드 중 오류가 발생했습니다.");
    });
}

// 폼 제출 함수
function submitPostForm() {
    const contentInput = document.getElementById("contentInput");
    const contentHiddenInput = document.getElementById("contentHiddenInput");
    contentHiddenInput.value = contentInput.innerHTML; // HTML 콘텐츠 저장

    // 디버그: 설정된 내용 확인
    console.log("제출할 내용:", contentHiddenInput.value);

    document.getElementById("postForm").submit(); // 폼 제출
}
