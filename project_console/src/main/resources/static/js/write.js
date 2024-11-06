// 내용 입력 영역 높이 자동 조절 함수
  function autoResizeTextarea() {
      const textarea = document.getElementById("contentInput");
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

  document.addEventListener("DOMContentLoaded", function () {
      const contentInput = document.getElementById("contentInput");
      contentInput.addEventListener("input", autoResizeTextarea); // 입력 이벤트에 따라 높이 자동 조절
      autoResizeTextarea(); // 페이지 로딩 시 초기 높이 조정
  });
