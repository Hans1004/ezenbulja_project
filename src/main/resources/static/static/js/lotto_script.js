// console.log('custom_script.js 파일 로드됨');

// 전역 변수 선언
let lossChart, accuracyChart, frequencyChart, predictionChart;
let hotColdChart, numberGapChart, sumHistogram, consecutiveNumbersChart;
let parsedData = []; // 전역 변수로 데이터 저장
let currentModel; // 현재 모델 저장

// CSV 파일 로드 및 파싱 함수
// function loadCSVFile(event) {
//     const file = event.target.files[0];
//     const reader = new FileReader();
//
//     reader.onload = function(event) {
//         const data = event.target.result;
//         // console.log("파일 데이터:", data); // 파일 데이터를 콘솔에 출력
//
//         parsedData = parseCSV(data);
//         // console.log("파싱된 데이터:", parsedData); // 파싱된 데이터를 콘솔에 출력
//
//         setupCharts();  // 차트 초기화
//         alert('CSV 파일이 로드되었습니다. "분석" 버튼을 눌러주세요.');
//     };
//     reader.readAsText(file);
// }
// 서버에서 파일 로드 하는 함수
async function loadCSVFromServer() {
    const response = await fetch('/csv/lotto_data.csv');  // CSV 파일 로드
    const data = await response.text();

    parsedData = parseCSV(data);  // 파싱된 데이터를 전역 변수에 저장
    setupCharts();  // 차트 초기화
    alert('CSV 파일이 로드되었습니다. "분석" 버튼을 눌러주세요.');
}

// CSV 데이터를 배열로 변환
function parseCSV(data) {
    const rows = data.trim().split('\n');
    const result = [];
    for (let i = 1; i < rows.length; i++) { // 첫 행은 헤더이므로 제외
        const values = rows[i].split(',');
        if (values.length >= 7) {
            // 데이터에 회차 정보가 있다고 가정하고, 첫 번째 열이 회차라고 가정합니다.
            result.push(values.slice(1, 7).map(Number));
        }
    }
    return result;
}

// 기간 필터링 함수
function applyPeriodFilter() {
    const period = document.querySelector('input[name="period"]:checked').value;
    let filteredData = [];

    if (period === 'all') {
        filteredData = parsedData;
    } else {
        const numMonths = parseInt(period);
        const drawsPerMonth = 4; // 월별 평균 추첨 수 (주 1회 추첨 시)
        const numDraws = numMonths * drawsPerMonth;

        // 최근 데이터부터 선택
        filteredData = parsedData.slice(-numDraws);
    }

    return filteredData;
}

// 모델 학습 및 예측 함수
async function trainAndPredict(data) {
    if (currentModel) {
        currentModel.dispose();
    }

    const xs = data.slice(0, -1);
    const ys = data.slice(1).map(draw => {
        const oneHot = Array(45).fill(0);
        draw.forEach(num => {
            oneHot[num - 1] = 1;
        });
        return oneHot;
    });

    const inputTensor = tf.tensor2d(xs);
    const outputTensor = tf.tensor2d(ys);

    const model = createModel();
    currentModel = model;

    const earlyStopping = tf.callbacks.earlyStopping({ monitor: 'val_loss', patience: 5 });
    const callbacks = {
        onEpochEnd: (epoch, logs) => updateCharts(epoch, logs),
        earlyStopping
    };

    await model.fit(inputTensor, outputTensor, {
        epochs: 50,
        batchSize: 32,
        validationSplit: 0.2,
        callbacks
    });

    // 예측은 모델 학습 후 자동으로 수행하도록 수정
    predictNumbers();
}

// 모델 예측 함수
async function predictNumbers() {
    if (!currentModel) {
        alert('먼저 "분석" 버튼을 눌러 모델을 학습시켜주세요.');
        return;
    }

    const lastDraw = parsedData[parsedData.length - 1];
    const prediction = currentModel.predict(tf.tensor2d([lastDraw]));
    const predictionArray = await prediction.array();

    const probabilities = predictionArray[0];
    const indices = probabilities.map((prob, idx) => ({ prob, idx }));
    indices.sort((a, b) => b.prob - a.prob);
    const predictedNumbers = indices.slice(0, 6).map(item => item.idx + 1);

    displayPredictedNumbers(predictedNumbers);

    updatePredictionChart(probabilities);
}

// 딥러닝 모델 생성 함수
function createModel() {
    const model = tf.sequential();
    model.add(tf.layers.dense({ units: 128, activation: 'relu', inputShape: [6] }));
    model.add(tf.layers.batchNormalization());
    model.add(tf.layers.dense({ units: 64, activation: 'relu' }));
    model.add(tf.layers.dropout({ rate: 0.3 }));
    model.add(tf.layers.dense({ units: 32, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 45, activation: 'sigmoid' }));

    const optimizer = tf.train.adam(0.001);
    model.compile({
        optimizer: optimizer,
        loss: 'binaryCrossentropy',
        metrics: ['binaryAccuracy']
    });

    return model;
}

// 차트 설정 함수
function setupCharts() {
    const lossCtx = document.getElementById('lossChart').getContext('2d');
    const accuracyCtx = document.getElementById('accuracyChart').getContext('2d');
    const frequencyCtx = document.getElementById('frequencyChart').getContext('2d');
    const predictionCtx = document.getElementById('predictionChart').getContext('2d');

    lossChart = new Chart(lossCtx, {
        type: 'line',
        data: { labels: [], datasets: [{ label: 'Loss', borderColor: 'red', data: [], fill: false }] },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: '모델 학습 손실(Loss)'
                }
            }
        }
    });

    accuracyChart = new Chart(accuracyCtx, {
        type: 'line',
        data: { labels: [], datasets: [{ label: 'Binary Accuracy', borderColor: 'blue', data: [], fill: false }] },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: '모델 학습 정확도(Binary Accuracy)'
                }
            }
        }
    });

    frequencyChart = new Chart(frequencyCtx, {
        type: 'bar',
        data: {
            labels: Array.from({ length: 45 }, (_, i) => i + 1),
            datasets: [{
                label: 'Number Frequency',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
                data: Array(45).fill(0)
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Number' }},
                y: { beginAtZero: true, title: { display: true, text: 'Frequency' }}
            },
            plugins: {
                title: {
                    display: true,
                    text: '번호 출현 빈도'
                }
            }
        }
    });

    predictionChart = new Chart(predictionCtx, {
        type: 'bar',
        data: {
            labels: Array.from({ length: 45 }, (_, i) => i + 1),
            datasets: [{
                label: 'Prediction Probability',
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgba(153, 102, 255, 1)',
                borderWidth: 1,
                data: Array(45).fill(0)
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Number' }},
                y: { beginAtZero: true, title: { display: true, text: 'Probability' }}
            },
            plugins: {
                title: {
                    display: true,
                    text: '예측 확률 분포'
                }
            }
        }
    });

    // 추가된 시각화 차트 설정

    // Hot & Cold Number Chart
    const hotColdCtx = document.getElementById('hotColdChart').getContext('2d');
    hotColdChart = new Chart(hotColdCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Hot & Cold Numbers',
                data: [],
                backgroundColor: [],
                borderColor: [],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: '핫 & 콜드 번호 차트'
                }
            }
        }
    });

    // Number Gap Analysis Chart
    const numberGapCtx = document.getElementById('numberGapChart').getContext('2d');
    numberGapChart = new Chart(numberGapCtx, {
        type: 'bar',
        data: {
            labels: Array.from({ length: 45 }, (_, i) => i + 1),
            datasets: [{
                label: 'Number Gaps',
                data: Array(45).fill(0),
                backgroundColor: 'rgba(255, 206, 86, 0.2)',
                borderColor: 'rgba(255, 206, 86, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Number' } },
                y: { beginAtZero: true, title: { display: true, text: 'Gap (Draws)' } }
            },
            plugins: {
                title: {
                    display: true,
                    text: '번호 갭 분석 차트'
                }
            }
        }
    });

    // Number Sum Histogram
    const sumHistogramCtx = document.getElementById('sumHistogram').getContext('2d');
    sumHistogram = new Chart(sumHistogramCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Sum of Numbers',
                data: [],
                backgroundColor: 'rgba(75, 192, 192, 0.6)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Sum Range' } },
                y: { beginAtZero: true, title: { display: true, text: 'Frequency' } }
            },
            plugins: {
                title: {
                    display: true,
                    text: '번호 합계 히스토그램'
                }
            }
        }
    });

    // Consecutive Number Frequency Chart
    const consecutiveCtx = document.getElementById('consecutiveNumbersChart').getContext('2d');
    consecutiveNumbersChart = new Chart(consecutiveCtx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Consecutive Number Frequency',
                data: [],
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: { title: { display: true, text: 'Consecutive Number Pair' } },
                y: { beginAtZero: true, title: { display: true, text: 'Frequency' } }
            },
            plugins: {
                title: {
                    display: true,
                    text: '연속 번호 출현 빈도 차트'
                }
            }
        }
    });
}

// 학습 과정 시각화 함수
function updateCharts(epoch, logs) {
    console.log('Epoch ' + (epoch + 1) + ' logs:', logs);

    lossChart.data.labels.push(epoch + 1);
    lossChart.data.datasets[0].data.push(logs.loss);
    lossChart.update();

    accuracyChart.data.labels.push(epoch + 1);
    const accuracy = logs.binaryAccuracy || logs.acc || logs.accuracy;
    if (accuracy !== undefined) {
        accuracyChart.data.datasets[0].data.push(accuracy);
    } else {
        accuracyChart.data.datasets[0].data.push(null);
    }
    accuracyChart.update();
}

// 출현 빈도 차트를 업데이트하는 함수
function updateFrequencyChart(frequency) {
    frequencyChart.data.datasets[0].data = frequency;
    frequencyChart.update();
}

// 예측 분포 차트를 업데이트하는 함수
function updatePredictionChart(probabilities) {
    predictionChart.data.datasets[0].data = probabilities;
    predictionChart.update();
}

// 출현 빈도 계산 함수
function calculateFrequency(data) {
    const frequency = Array(45).fill(0);
    data.forEach(row => {
        row.forEach(number => {
            frequency[number - 1]++;
        });
    });
    return frequency;
}

// 예측 번호 출력 함수
function displayPredictedNumbers(numbers) {
    // 번호를 정렬
    numbers.sort((a, b) => a - b);

    const resultDiv = document.getElementById("result");

    // 이전 결과를 지우기 위해 innerHTML 초기화
    resultDiv.innerHTML = "";

    numbers.forEach(num => {
        const ball = document.createElement('div');
        ball.className = 'lotto-number ' + getColorClass(num);
        ball.textContent = num;
        resultDiv.appendChild(ball);
    });

    // 번호를 로컬 스토리지에 저장
    saveNumbersToLocal(numbers);
}

// 번호를 로컬 스토리지에 저장하는 함수
function saveNumbersToLocal(numbers) {
    const savedNumbers = JSON.parse(localStorage.getItem('savedNumbers')) || [];

    // 현재 날짜와 시간을 함께 저장
    const now = new Date();
    const dateString = now.toISOString(); // 날짜를 ISO 문자열로 저장
    savedNumbers.push({ date: dateString, numbers: numbers });
    localStorage.setItem('savedNumbers', JSON.stringify(savedNumbers));
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

// // '분석' 버튼 클릭 시 이벤트 핸들러
// function onAnalyzeClick() {
//     if (parsedData.length > 0) {
//         const filteredData = applyPeriodFilter();
//         // 모델 재훈련 및 예측
//         trainAndPredict(filteredData).then(() => {
//             // 빈도 차트 업데이트
//             const frequency = calculateFrequency(filteredData);
//             updateFrequencyChart(frequency);
//
//             // 추가된 시각화 업데이트
//             updateHotColdChart(frequency);
//             const gaps = calculateNumberGaps(filteredData);
//             updateNumberGapChart(gaps);
//             updateSumHistogram(filteredData); // 번호 합계 히스토그램 업데이트
//             updateConsecutiveNumbersChart(filteredData);
//         });
//     } else {
//         alert('먼저 CSV 파일을 로드해주세요.');
//     }
// }
function onAnalyzeClick() {
    if (parsedData.length > 0) {
        const filteredData = applyPeriodFilter();
        trainAndPredict(filteredData).then(() => {
            const frequency = calculateFrequency(filteredData);
            updateFrequencyChart(frequency);

            updateHotColdChart(frequency);
            const gaps = calculateNumberGaps(filteredData);
            updateNumberGapChart(gaps);
            updateSumHistogram(filteredData);
            updateConsecutiveNumbersChart(filteredData);
        });
    } else {
        alert('CSV 파일을 서버에서 불러오는 중입니다...');
        loadCSVFromServer();  // 서버에서 파일 자동 로드
    }
}

// 추가된 시각화 함수들

// 1. Hot & Cold Number Chart 업데이트 함수
function updateHotColdChart(frequency) {
    const numberFrequency = frequency.map((freq, idx) => ({ number: idx + 1, freq }));
    const sortedFrequency = numberFrequency.sort((a, b) => b.freq - a.freq);

    const hotNumbers = sortedFrequency.slice(0, 5);
    const coldNumbers = sortedFrequency.slice(-5);

    const labels = hotNumbers.map(n => n.number).concat(coldNumbers.map(n => n.number));
    const data = hotNumbers.map(n => n.freq).concat(coldNumbers.map(n => n.freq));

    const backgroundColors = labels.map((_, idx) => idx < 5 ? 'rgba(255, 99, 132, 0.6)' : 'rgba(54, 162, 235, 0.6)');
    const borderColors = labels.map((_, idx) => idx < 5 ? 'rgba(255,99,132,1)' : 'rgba(54, 162, 235, 1)');

    hotColdChart.data.labels = labels;
    hotColdChart.data.datasets[0].data = data;
    hotColdChart.data.datasets[0].backgroundColor = backgroundColors;
    hotColdChart.data.datasets[0].borderColor = borderColors;
    hotColdChart.update();
}

// 2. Number Gap Analysis Chart 업데이트 함수
function calculateNumberGaps(data) {
    const lastSeen = Array(45).fill(-1);
    const gaps = Array(45).fill(0);

    data.forEach((draw, drawIndex) => {
        for (let i = 0; i < 45; i++) {
            if (draw.includes(i + 1)) {
                if (lastSeen[i] !== -1) {
                    gaps[i] = drawIndex - lastSeen[i];
                } else {
                    gaps[i] = drawIndex + 1; // 처음 등장하는 경우
                }
                lastSeen[i] = drawIndex;
            }
        }
    });

    return gaps;
}

function updateNumberGapChart(gaps) {
    numberGapChart.data.datasets[0].data = gaps;
    numberGapChart.update();
}

// 3. Number Sum Histogram 업데이트 함수
function updateSumHistogram(data) {
    const sums = data.map(draw => draw.reduce((a, b) => a + b, 0));
    const sumCounts = {};

    sums.forEach(sum => {
        const range = Math.floor(sum / 10) * 10; // 10단위로 그룹화
        sumCounts[range] = (sumCounts[range] || 0) + 1;
    });

    const labels = Object.keys(sumCounts).sort((a, b) => a - b);
    const counts = labels.map(range => sumCounts[range]);

    sumHistogram.data.labels = labels.map(range => `${range} - ${Number(range) + 9}`);
    sumHistogram.data.datasets[0].data = counts;
    sumHistogram.update();
}

// 4. Consecutive Number Frequency Chart 업데이트 함수
function updateConsecutiveNumbersChart(data) {
    const consecutiveCounts = {};

    data.forEach(draw => {
        const sortedDraw = draw.slice().sort((a, b) => a - b);
        for (let i = 0; i < sortedDraw.length - 1; i++) {
            if (sortedDraw[i + 1] - sortedDraw[i] === 1) {
                const pair = `${sortedDraw[i]}-${sortedDraw[i + 1]}`;
                consecutiveCounts[pair] = (consecutiveCounts[pair] || 0) + 1;
            }
        }
    });

    const labels = Object.keys(consecutiveCounts);
    const counts = Object.values(consecutiveCounts);

    consecutiveNumbersChart.data.labels = labels;
    consecutiveNumbersChart.data.datasets[0].data = counts;
    consecutiveNumbersChart.update();
}