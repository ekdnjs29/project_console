document.addEventListener('DOMContentLoaded', function () {
    setListView(); // 페이지 로드 시 목록형 기본 설정
    document.getElementById('listViewBtn').addEventListener('click', setListView);
    document.getElementById('gridViewBtn').addEventListener('click', setGridView);
    
    // 각 뷰에서 글 요약 길이를 조정
    applyExcerptLimit();
});

function applyExcerptLimit() {
    const maxLength = 60;
    document.querySelectorAll(".post-excerpt").forEach(excerpt => {
        if (excerpt.textContent.length > maxLength) {
            excerpt.textContent = excerpt.textContent.slice(0, maxLength) + "...";
        }
    });
}

function adjustContentHeight(image, content) {
    const imageWidth = 280; // 이미지 가로 크기를 280px로 가정
    const aspectRatio = image.naturalHeight / image.naturalWidth;

    // 가로 크기가 280px일 때 비율에 맞는 높이 계산
    let calculatedHeight = imageWidth * aspectRatio;

    // 높이가 230px를 넘지 않도록 제한
    if (calculatedHeight > 230) {
        calculatedHeight = 230;
    } else if (calculatedHeight < 180) {
        calculatedHeight = 180;
    }

    // 이미지 높이 설정
    image.style.width = "100%";
    image.style.height = `${calculatedHeight}px`;

    // 컨텐츠 영역 높이 설정 (그리드 뷰에서만 적용)
    const totalHeight = 320; // .post-item의 전체 높이
    const contentHeight = totalHeight - calculatedHeight; // 이미지 높이와 간격을 뺀 값
    content.style.height = `${contentHeight}px`;
}

function filterPosts() {
    const filter = document.getElementById("postFilter").value;

    $.ajax({
        url: "/posts/filter",
        type: "GET",
        data: { filter: filter },
        success: function(data) {
            // 서버에서 받은 HTML 데이터를 두 컨테이너에 모두 적용
            $("#listViewContainer").html(data);
            $("#gridViewContainer").html(data);

            applyExcerptLimit();

            if (document.getElementById('gridViewContainer').style.display === 'grid') {
                applyGridStyle(); // 그리드형 스타일 적용
                filterGridViewItems(); // 그리드형에서 이미지가 없는 게시글 숨기기
                setGridItemHeights();  // 그리드 아이템 높이 조정
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                window.location.href = xhr.responseText;
            } else {
                alert("게시글을 불러오는 중 오류가 발생했습니다.");
            }
        }
    });
}

function setListView() {
    document.getElementById('listViewContainer').style.display = 'flex';
    document.getElementById('gridViewContainer').style.display = 'none';
    document.getElementById('listViewBtn').classList.add('active');
    document.getElementById('gridViewBtn').classList.remove('active');
}

function setGridView() {
    document.getElementById('listViewContainer').style.display = 'none';
    document.getElementById('gridViewContainer').style.display = 'grid';
    document.getElementById('gridViewBtn').classList.add('active');
    document.getElementById('listViewBtn').classList.remove('active');
    applyGridStyle(); // 그리드형 스타일 적용
    filterGridViewItems(); // 그리드형일 때 이미지가 없는 게시글 숨기기
    setGridItemHeights(); // 그리드 아이템 높이 조정
}

function applyGridStyle() {
    const gridViewContainer = document.getElementById('gridViewContainer');
    gridViewContainer.classList.add('grid-style'); // 그리드형 전용 스타일 클래스 추가
}

function filterGridViewItems() {
    document.querySelectorAll('#gridViewContainer .post-item').forEach(item => {
        const hasImage = item.querySelector('.post-image img') !== null;
        item.style.display = hasImage ? 'flex' : 'none'; // 이미지가 없으면 숨기기
    });
}

function setGridItemHeights() {
    const postItems = document.querySelectorAll("#gridViewContainer .post-item");
    postItems.forEach((postItem) => {
        const image = postItem.querySelector(".post-image img");
        const content = postItem.querySelector(".post-content");

        if (image && content) {
            if (image.complete) {
                adjustContentHeight(image, content);
            } else {
                image.addEventListener("load", () => {
                    adjustContentHeight(image, content);
                });
            }
        }
    });
}
