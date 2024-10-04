function toggleMode() {
    document.body.classList.toggle('dark-mode');

    // 로컬 스토리지에 현재 모드를 저장해 페이지 새로고침 후에도 유지
    if (document.body.classList.contains('dark-mode')) {
        localStorage.setItem('theme', 'dark');
    } else {
        localStorage.setItem('theme', 'light');
    }
}

// 페이지 로드 시 로컬 스토리지에서 테마 설정 확인
window.onload = function() {
    const theme = localStorage.getItem('theme');
    if (theme === 'dark') {
        document.body.classList.add('dark-mode');
    }
};