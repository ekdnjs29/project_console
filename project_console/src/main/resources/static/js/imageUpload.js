// 이미지 붙여넣기 이벤트 리스너
document.addEventListener("paste", function (event) {
    const items = (event.clipboardData || event.originalEvent.clipboardData).items;
    for (const item of items) {
        if (item.kind === "file" && item.type.startsWith("image/")) {
            event.preventDefault();
            const file = item.getAsFile();
            uploadImage(file); // 붙여넣기된 파일 업로드 및 미리보기 표시
        }
    }
});

// 이미지 URL을 hidden 필드에 추가하는 함수
function addImageUrl(imageUrl) {
    const imageUrlsInput = document.getElementById('imageUrlsInput');
    if (!imageUrlsInput) {
        console.error("imageUrlsInput 요소를 찾을 수 없습니다.");
        return;
    }
    const currentUrls = new Set(imageUrlsInput.value.split(',').filter(Boolean));
    currentUrls.add(imageUrl);
    imageUrlsInput.value = Array.from(currentUrls).join(',');
    console.log("이미지 추가 후 hidden 필드 값:", imageUrlsInput.value);
}

// 이미지 URL을 hidden 필드에서 제거하는 함수
function removeImageUrl(imageUrl) {
    const imageUrlsInput = document.getElementById('imageUrlsInput');
    if (!imageUrlsInput) {
        console.error("imageUrlsInput 요소를 찾을 수 없습니다.");
        return;
    }
    const currentUrls = new Set(imageUrlsInput.value.split(',').filter(Boolean));
    currentUrls.delete(imageUrl);
    imageUrlsInput.value = Array.from(currentUrls).join(',');
    console.log("이미지 삭제 후 hidden 필드 값:", imageUrlsInput.value);
}

// 새로 추가된 이미지 업로드 함수 (파일 이름을 인코딩하여 서버로 전송)
function uploadImage(file) {
    const formData = new FormData();
    // 파일 이름을 URL-safe 형식으로 인코딩
    const encodedFileName = encodeURIComponent(file.name);
    formData.append("image", file, encodedFileName); // 인코딩된 파일 이름으로 추가

    fetch("/images/uploadImage", {
        method: "POST",
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        if (data.imageUrl) {
            addImageUrl(data.imageUrl); // hidden 필드에 이미지 URL 추가
            addImageToContent(data.imageUrl); // 업로드 완료 후 미리보기 표시
        } else {
            alert("이미지 업로드 중 오류 발생: " + data.error);
        }
    })
    .catch(error => {
        console.error("이미지 업로드 실패:", error);
        alert("이미지 업로드 중 오류가 발생했습니다.");
    });
}

// 내용 입력 영역에 새로 추가된 이미지를 직접 추가하는 함수
function addImageToContent(imageUrl) {
    const contentInput = document.getElementById("contentInput");
    if (contentInput) {
        const imgContainer = document.createElement("div");
        imgContainer.classList.add("image-container");

        const imgElement = document.createElement("img");
        imgElement.src = imageUrl;
        imgElement.alt = "첨부 이미지";
        imgElement.dataset.url = imageUrl;

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.classList.add("delete-button");
        deleteButton.innerText = "삭제";

        // 새로 추가된 이미지의 삭제 버튼 클릭 이벤트 처리
        deleteButton.addEventListener('click', function() {
            deleteImage(imageUrl, imgContainer); // 서버 및 UI에서 이미지 삭제
        });

        imgContainer.appendChild(imgElement);
        imgContainer.appendChild(deleteButton);
        contentInput.appendChild(imgContainer);
        contentInput.focus();
    } else {
        console.error("contentInput 요소를 찾을 수 없습니다.");
    }
}

// 기존 이미지의 UI 및 hidden 필드에서만 삭제하는 함수
function removeExistingImageFromUI(button, imageUrl) {
    const imgContainer = button.parentElement;
    imgContainer.remove(); // UI에서 이미지 제거
    removeImageUrl(imageUrl); // hidden 필드에서 URL 제거
    console.log("기존 이미지 제거됨:", imageUrl);
}

// 서버에서 새로 추가된 이미지 삭제 함수
function deleteImage(imageUrl, imgContainer) {
    console.log("deleteImage 호출됨:", imageUrl);

    fetch('/images/deleteTempImage', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ imageUrl: imageUrl })
    })
    .then(response => {
        if (response.ok) {
            console.log("서버에서 이미지 삭제 성공:", imageUrl);
            removeImageUrl(imageUrl); // hidden 필드에서 URL 제거
            imgContainer.remove(); // UI에서 이미지 삭제
        } else {
            console.error("서버에서 이미지 삭제 실패:", imageUrl);
        }
    })
    .catch(error => console.error("이미지 삭제 요청 실패:", error));
}

// 이벤트 위임 방식으로 삭제 버튼 클릭 이벤트 처리
document.addEventListener("click", function (event) {
    if (event.target.classList.contains("delete-button")) {
        event.preventDefault();
        const imgContainer = event.target.parentElement;
        const imgElement = imgContainer.querySelector("img");
        const imageUrl = imgElement.dataset.url || imgElement.getAttribute("src");

        // 기존 이미지와 새 이미지 구분
        if (imageUrl.startsWith("/upload/")) {
            // 기존 이미지 삭제 (UI와 hidden 필드에서만 삭제)
            removeExistingImageFromUI(event.target, imageUrl);
        } else if (imageUrl.startsWith("/temp/")) {
            // 새로 추가된 이미지 삭제 (서버 및 UI 삭제)
            deleteImage(imageUrl, imgContainer);
        }
    }
});

// 이미지 업로드 버튼을 통한 파일 선택 처리
function handleImageUpload(input) {
    const file = input.files[0];
    if (file) {
        uploadImage(file); // 파일 업로드 및 미리보기 표시
    }
}
