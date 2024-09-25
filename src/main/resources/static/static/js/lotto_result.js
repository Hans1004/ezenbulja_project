// 기준 날짜와 회차 정보 설정
const BASE_DATE = new Date(2024, 8, 7); // 2024년 9월 7일, 월은 0부터 시작하므로 8은 9월을 의미
const BASE_DRAW_NUMBER = 1136; // 기준 회차

// 회차를 계산하는 함수
function calculateDrawNumber(currentDate) {
    const timeDiff = currentDate.getTime() - BASE_DATE.getTime();
    const daysDiff = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
    const weeksDiff = Math.floor(daysDiff / 7); // 정확하게 주차 차이 계산
    return BASE_DRAW_NUMBER + weeksDiff + 1; // 9월 7일이 속한 주부터 다음 회차가 계산되도록 +1을 추가
}

// 번호 색상 결정 함수
function getColorClass(number) {
    if (number >= 1 && number <= 10) return 'yellow';
    if (number >= 11 && number <= 20) return 'blue';
    if (number >= 21 && number <= 30) return 'red';
    if (number >= 31 && number <= 40) return 'gray';
    if (number >= 41 && number <= 45) return 'green';
    return '';
}

// 저장된 번호를 불러와서 표시하는 함수
function displaySavedNumbers() {
    const savedNumbers = JSON.parse(localStorage.getItem('savedNumbers')) || [];
    const savedNumbersList = document.getElementById('savedNumbersList');

    savedNumbersList.innerHTML = ''; // 기존 내용 지우기

    if (savedNumbers.length === 0) {
    savedNumbersList.innerHTML = '<div class="no-saved-numbers">저장된 번호가 없습니다.</div>';
    return;
    }

    // 날짜순으로 정렬 (최신순)
    savedNumbers.sort((a, b) => new Date(b.date) - new Date(a.date));

    // 한 줄에 2세트씩 표시하기 위해 배열을 나누어서 처리
    for (let i = 0; i < savedNumbers.length; i += 2) {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'row mb-4'; // 한 줄(row)에 두 세트가 들어가도록 함

        // 첫 번째 세트
        const item1 = savedNumbers[i];
        const col1 = createNumberSet(item1);
        col1.className = 'col-md-5'; // 한 줄에 6개의 번호를 가진 첫 번째 세트

        rowDiv.appendChild(col1);

        // 두 번째 세트가 있으면 추가
        if (i + 1 < savedNumbers.length) {
            const item2 = savedNumbers[i + 1];
            const col2 = createNumberSet(item2);
            col2.className = 'col-md-5'; // 한 줄에 6개의 번호를 가진 두 번째 세트

            rowDiv.appendChild(col2);
        }

        savedNumbersList.appendChild(rowDiv); // 한 줄에 두 개의 세트 추가
    }
}

// 번호 세트를 생성하는 함수
function createNumberSet(item) {
    const date = new Date(item.date);
    const dateString = date.toLocaleString(); // 날짜를 읽기 쉽게 변환
    const drawNumber = calculateDrawNumber(date); // 저장된 날짜를 기준으로 회차 계산

    const numbers = item.numbers;
    const numbersDiv = document.createElement('div');
    numbersDiv.className = 'saved-number-item';

    const dateDiv = document.createElement('div');
    dateDiv.className = 'saved-number-date';
    dateDiv.textContent = `생성일: ${dateString} (회차: ${drawNumber})`;

    const numbersContainer = document.createElement('div');
    numbersContainer.className = 'saved-number-balls';

    numbers.forEach(num => {
        const ball = document.createElement('div');
        ball.className = 'lotto-number ' + getColorClass(num);
        ball.textContent = num;
        numbersContainer.appendChild(ball);
    });

    numbersDiv.appendChild(dateDiv);
    numbersDiv.appendChild(numbersContainer);

    return numbersDiv;
}

// 페이지 로드 시 저장된 번호 표시
window.onload = function() {
    // 저장된 번호 불러오고 회차 업데이트
    displaySavedNumbers();
};
