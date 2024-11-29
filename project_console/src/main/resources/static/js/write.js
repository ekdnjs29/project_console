// 내용 입력 영역 높이 자동 조절 함수
function autoResizeTextarea() {
    const textarea = document.getElementById("contentInput");
    if (textarea) {
        textarea.style.height = "auto"; // 기존 높이를 초기화
        const extraSpace = 50; // 추가적인 간격 (여백) 설정
        textarea.style.height = (textarea.scrollHeight + extraSpace) + "px"; // 내용에 따라 높이를 설정하고 여백 추가
        
        // 텍스트 입력 시 화면 스크롤 조정
        const rect = textarea.getBoundingClientRect();
        const offset = 50; // 여백
        if (rect.bottom > window.innerHeight) {
            window.scrollBy(0, rect.bottom - window.innerHeight + offset);
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const contentInput = document.getElementById("contentInput");
    if (contentInput) {
        contentInput.addEventListener("input", autoResizeTextarea); // 입력 이벤트에 따라 높이 자동 조절
        autoResizeTextarea(); // 페이지 로딩 시 초기 높이 조정
    } else {
        console.error("contentInput 요소를 찾을 수 없습니다.");
    }
});

// 텍스트 서식 적용 함수 (굵게, 기울임, 밑줄, 취소선, 정렬 등)
function formatText(command) {
    document.execCommand(command, false, null);
}

// 글자 크기 변경 함수
function changeFontSize(size) {
    const contentInput = document.getElementById("contentInput");
    if (contentInput) {
        document.execCommand("fontSize", false, "7"); // 기본 크기를 설정해 둔 후
        const fontElements = contentInput.getElementsByTagName("font");
        for (const fontElement of fontElements) {
            if (fontElement.size === "7") {
                fontElement.removeAttribute("size"); // 기본 크기 속성 제거
                fontElement.style.fontSize = `${size}px`; // 선택한 크기로 변경
            }
        }
    }
}

/// 페이지 로드 후 highlight.js 초기화
        document.addEventListener("DOMContentLoaded", () => {
            hljs.highlightAll();
        });

        // 소스 코드 블록 삽입 함수
   function insertCodeBlock() {
    const contentInput = document.getElementById("contentInput");
    if (contentInput) {
        // <pre><code> 태그 구조로 코드 블록 생성
        const codeBlockContainer = document.createElement("div");
        codeBlockContainer.classList.add("code-block-container"); // 코드 블록 컨테이너
        
        const codeBlock = document.createElement("pre");
        const codeElement = document.createElement("code");
        codeElement.className = "language-java";
        codeElement.contentEditable = true;
        codeElement.innerText = "// 여기에 코드를 입력하세요";

        // 삭제 버튼 생성
        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.classList.add("code-delete-button");
        deleteButton.innerText = "삭제";

        // 삭제 버튼 클릭 이벤트 처리
        deleteButton.addEventListener('click', function() {
            codeBlockContainer.remove(); // 코드 블록 삭제
        });

        // 코드 블록 컨테이너에 코드 블록과 삭제 버튼 추가
        codeBlock.appendChild(codeElement);
        codeBlockContainer.appendChild(codeBlock);
        codeBlockContainer.appendChild(deleteButton);
        contentInput.appendChild(codeBlockContainer);

        // 코드 하이라이팅 적용
        hljs.highlightElement(codeElement);
        codeElement.focus();
    }
}


// 이미지 업로드 처리 함수
function handleImageUpload(input) {
    const file = input.files[0];
    if (file) {
        uploadImage(file); // 기존 imageUpload.js의 uploadImage 함수 호출
    }
}

// 이미지 업로드 함수 (기존에 존재하는 함수로 가정)
function uploadImage(file) {
    const formData = new FormData();
    formData.append("image", file);

    fetch("/images/uploadImage", {
        method: "POST",
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        if (data.imageUrl) {
            addImageUrl(data.imageUrl);
            addImageToContent(data.imageUrl);
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

// 폼 제출 함수
function submitPostForm(formId, contentInputId, hiddenInputId) {
    const contentInput = document.getElementById(contentInputId);
    const contentHiddenInput = document.getElementById(hiddenInputId);
    if (contentInput && contentHiddenInput) {
        contentHiddenInput.value = contentInput.innerHTML; // HTML 콘텐츠 저장
        console.log("제출할 내용:", contentHiddenInput.value); // 디버깅용
        console.log("이미지 URL 목록:", document.getElementById("imageUrlsInput").value); // hidden 필드 값 확인
        document.getElementById(formId).submit(); // 폼 제출
    } else {
        console.error("요소를 찾을 수 없습니다: ", contentInputId, hiddenInputId);
    }
}



