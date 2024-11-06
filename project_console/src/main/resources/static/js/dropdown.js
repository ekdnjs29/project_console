function toggleDropdown(menuId) {
        console.log("Dropdown toggled: " + menuId);
        var dropdownMenu = document.getElementById(menuId);
        dropdownMenu.classList.toggle("show");
    }

    // 다른 영역을 클릭하면 모든 드롭다운 메뉴 닫기
    window.onclick = function(event) {
        if (!event.target.matches('.dropdown-button')) {
            // 모든 드롭다운 메뉴 닫기
            var dropdowns = document.querySelectorAll('.dropdown-menu');
            dropdowns.forEach(function(dropdown) {
                if (dropdown.classList.contains('show')) {
                    dropdown.classList.remove('show');
                }
            });
        }
    }