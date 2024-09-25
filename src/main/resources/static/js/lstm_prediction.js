// 변수 선언
let model;
let X_train, X_test, y_train, y_test;
let normParams;
let labelMin, labelMax;
let lossData = [];
let valLossData = [];
let epochsData = [];
let batchLossData = [];
let batchData = [];

// Min-Max 정규화 함수
function MinMaxScaling(tensor, prevMin = null, prevMax = null) {
    const min = prevMin || tensor.min();
    const max = prevMax || tensor.max();
    const normedTensor = tensor.sub(min).div(max.sub(min));
    return { tensor: normedTensor, min, max };
}

// 데이터 시각화 함수 (Plotly)
async function plot(dataPoints, elementId) {
    const surface = document.getElementById(elementId);
    const xData = dataPoints.map(v => v.x[3]); // candleAccTradeVolume
    const yData = dataPoints.map(v => v.y[0]); // tradePrice

    const data = [{
        x: xData,
        y: yData,
        mode: 'markers',
        type: 'scatter',
        marker: { 
            size: 12,
            color: yData,
            colorscale: 'Viridis',
            showscale: true,
            line: {
                width: 1,
                color: 'rgba(0,0,0,0.5)'
            }
        },
        text: xData.map((val, index) => `Volume: ${val}<br>Price: ${yData[index]}`),
        hoverinfo: 'text'
    }];

    const layout = {
        title: {
            text: 'Data Points',
            font: { size: 24 },
            x: 0.5,
            xanchor: 'center'
        },
        xaxis: { 
            title: 'Candle Acc Trade Volume',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        yaxis: { 
            title: 'Trade Price',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        margin: { t: 50, l: 50, r: 50, b: 50 },
        width: surface.clientWidth,
        height: surface.clientHeight,
        plot_bgcolor: '#f2f2f2',
        paper_bgcolor: '#ffffff',
        font: { family: 'Arial, sans-serif', size: 14 }
    };

    Plotly.newPlot(surface, data, layout);
}

// 훈련 성능 시각화 (Epoch 단위)
async function plotTrainingPerformance() {
    const surface = document.getElementById("training-performance");

    const lossSeries = {
        x: epochsData,
        y: lossData,
        mode: 'lines+markers',
        type: 'scatter',
        name: 'Loss',
        line: { color: 'rgba(255, 99, 132, 0.8)', width: 3 },
        marker: { size: 8 }
    };

    const valLossSeries = {
        x: epochsData,
        y: valLossData,
        mode: 'lines+markers',
        type: 'scatter',
        name: 'Validation Loss',
        line: { color: 'rgba(54, 162, 235, 0.8)', width: 3 },
        marker: { size: 8 },  
    };

    const data = [lossSeries, valLossSeries];

    const layout = {
        title: {
            text: 'Training Performance',
            font: { size: 24 },
            x: 0.5,
            xanchor: 'center'
        },
        xaxis: { 
            title: 'Epoch',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        yaxis: { 
            title: 'Loss',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        margin: { t: 50, l: 50, r: 50, b: 50 },
        autosize: true,
        plot_bgcolor: '#f2f2f2',
        paper_bgcolor: '#ffffff',
        font: { family: 'Arial, sans-serif', size: 14 }
    };

    Plotly.newPlot(surface, data, layout);
}

// 배치 성능 시각화 (Batch 단위)
async function plotBatchPerformance() {
    const surface = document.getElementById("batch-performance");

    const batchLossSeries = {
        x: batchData,
        y: batchLossData,
        mode: 'lines',
        type: 'scatter',
        name: 'Batch Loss',
        line: { color: 'rgba(54, 162, 235, 0.8)', width: 3 }, // 선 색상을 파란색으로 설정
        // marker: { size: 8 }
    };

    const data = [batchLossSeries];

    const layout = {
        title: {
            text: 'Batch Performance',
            font: { size: 24 },
            x: 0.5,
            xanchor: 'center'
        },
        xaxis: { 
            title: 'Batch',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        yaxis: { 
            title: 'Loss',
            titlefont: { size: 18 },
            tickfont: { size: 14 }
        },
        margin: { t: 50, l: 50, r: 50, b: 50 },
        width: surface.clientWidth,
        height: surface.clientHeight,
        plot_bgcolor: '#f2f2f2',
        paper_bgcolor: '#ffffff',
        font: { family: 'Arial, sans-serif', size: 14 }
    };

    Plotly.newPlot(surface, data, layout);
}

// 데이터 복원 함수
function denormalize(tensor, min, max) {
    return tensor.mul(max.sub(min)).add(min);
}

// 모델 생성 함수
async function createModel() {
    model = tf.sequential();
    model.add(tf.layers.lstm({
        units: 50,
        inputShape: [7, 1],
        returnSequences: false
    }));
    model.add(tf.layers.dense({ units: 1 }));
    model.compile({ optimizer: 'adam', loss: 'meanSquaredError' });
}

// 데이터 전처리 함수
async function preprocessData() {
    const dataUrl = "/csv/upbit_btc_data_with_rsi.csv";
    const dataset = tf.data.csv(dataUrl, {
        columnConfigs: {
            opening_price: { isLabel: false },
            trade_price: { isLabel: false },
            low_price: { isLabel: false },
            candle_acc_trade_volume: { isLabel: false },
            prev_closing_price: { isLabel: false },
            change_price: { isLabel: false },
            RSI: { isLabel: false },
            high_price: { isLabel: true }
        },
        configuredColumnsOnly: true
    });

    let dataPoints = await dataset.map(({ xs, ys }) => {
        return {
            x: [
                xs.opening_price || 0,
                xs.trade_price || 0,
                xs.low_price || 0,
                xs.candle_acc_trade_volume || 0,
                xs.prev_closing_price || 0,
                xs.change_price || 0,
                xs.RSI || 0
            ],
            y: [ys.high_price || 0]
        };
    }).toArray();

    console.log('Data points:', dataPoints);

    tf.util.shuffle(dataPoints);

    await plot(dataPoints, 'data-plot');

    const featureValues = dataPoints.map(p => p.x);
    const labelValues = dataPoints.map(p => p.y);

    const reshapedFeatureValues = featureValues.map(fv => [
        [fv[0]],
        [fv[1]],
        [fv[2]],
        [fv[3]],
        [fv[4]],
        [fv[5]],
        [fv[6]]
    ]);

    const featureTensor = tf.tensor3d(reshapedFeatureValues, [reshapedFeatureValues.length, 7, 1]);
    const labelTensor = tf.tensor2d(labelValues, [labelValues.length, 1]);

    const trainLen = Math.floor(dataPoints.length * 0.75);
    const testLen = dataPoints.length - trainLen;

    [X_train, X_test] = tf.split(featureTensor, [trainLen, testLen]);
    [y_train, y_test] = tf.split(labelTensor, [trainLen, testLen]);

    labelMin = y_train.min();
    labelMax = y_train.max();

    const normedTrainTensor = MinMaxScaling(X_train);
    normParams = { min: normedTrainTensor.min, max: normedTrainTensor.max };
    X_train = normedTrainTensor.tensor;

    const normedTestTensor = MinMaxScaling(X_test, normParams.min, normParams.max);
    X_test = normedTestTensor.tensor;

    y_train = MinMaxScaling(y_train, labelMin, labelMax).tensor;
    y_test = MinMaxScaling(y_test, labelMin, labelMax).tensor;
}

// 모델 학습 함수
async function train() {
    try {
        await createModel();
        await preprocessData();

        const statusElement = document.getElementById("training-status");
        statusElement.textContent = "Training...";

        // 초기 데이터
        lossData = [];
        valLossData = [];
        epochsData = [];
        batchLossData = [];
        batchData = [];

        const history = await model.fit(X_train, y_train, {
            epochs: 25,
            batchSize: 16,
            validationData: [X_test, y_test],
            callbacks: {
                onBatchEnd: async (batch, logs) => {
                    // 배치 손실 데이터 저장 및 시각화
                    batchLossData.push(logs.loss);
                    batchData.push(batch);

                    await plotBatchPerformance(); // 배치 성능 시각화 업데이트
                },
                onEpochEnd: async (epoch, logs) => {
                    // 에포크별 성능 업데이트
                    lossData.push(logs.loss);
                    valLossData.push(logs.val_loss);
                    epochsData.push(epoch);

                    await plotTrainingPerformance(); // 에포크 성능 시각화 업데이트
                }
            }
        });

        statusElement.textContent = "Training Complete!";
        document.getElementById("predict-button").disabled = false;
    } catch (error) {
        console.error('Training error:', error);
        document.getElementById("training-status").textContent = "Training failed.";
    }
}

// 예측 함수
async function predict() {
    const predInputOpeningPrice = parseFloat(document.getElementById("predict-input-opening-price").value);
    const predInputHighPrice = parseFloat(document.getElementById("predict-input-high-price").value);
    const predInputLowPrice = parseFloat(document.getElementById("predict-input-low-price").value);
    const predInputVolume = parseFloat(document.getElementById("predict-input-volume").value);
    const predInputPrevClose = parseFloat(document.getElementById("predict-input-prev-close").value);
    const predInputChangePrice = parseFloat(document.getElementById("predict-input-change-price").value);
    const predInputRSI = parseFloat(document.getElementById("predict-input-rsi").value);

    if (isNaN(predInputOpeningPrice) || isNaN(predInputHighPrice) || isNaN(predInputLowPrice) || isNaN(predInputVolume) || isNaN(predInputPrevClose) || isNaN(predInputChangePrice) || isNaN(predInputRSI)) {
        alert("Please enter valid inputs.");
        return;
    }

    const features = [[predInputOpeningPrice, predInputHighPrice, predInputLowPrice, predInputVolume, predInputPrevClose, predInputChangePrice, predInputRSI]];
    const reshapedFeatures = features.map(fv => [
        [fv[0]],
        [fv[1]],
        [fv[2]],
        [fv[3]],
        [fv[4]],
        [fv[5]],
        [fv[6]]
    ]);

    const tempTensor = tf.tensor3d(reshapedFeatures, [1, 7, 1]);
    const normedTensor = MinMaxScaling(tempTensor, normParams.min, normParams.max).tensor;

    model.predict(normedTensor).array().then(predictedValue => {
        const predictedValueTensor = tf.tensor2d(predictedValue, [1, 1]);
        const denormalizedValue = denormalize(predictedValueTensor, labelMin, labelMax);

        denormalizedValue.array().then(denormalized => {
            const predictedPrice = denormalized[0][0];
            const formattedPrice = new Intl.NumberFormat('ko-KR').format(predictedPrice);
            document.getElementById('predict-output').innerHTML = `예측된 최고 가격은 <h3 class="text-primary">₩${formattedPrice}</h3>입니다. 코인투자에 도움이 되길 바랍니다.`;

            // 서버로 데이터 전송
            const coinResult = {
                createdDate: new Date().toISOString(), // 현재 날짜
                predictedPrice: Math.round(predictedPrice) // 소수점 없이 정수로 저장
            };

            fetch('/predict', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(coinResult),
            })
                .then(response => response.text())
                .then(data => {
                    console.log(data);
                })
                .catch((error) => {
                    console.error('Error:', error);
                });



            // 변동 가격 비교
            const highPrice = parseFloat(document.getElementById('predict-input-high-price').value);
            const upElement = document.getElementById('up');
            const downElement = document.getElementById('down');

            // 이미지 요소 초기화
            upElement.innerHTML = '';
            downElement.innerHTML = '';

            // 예측 가격과 변동 가격 비교
            if (predictedPrice > highPrice) {
                upElement.innerHTML = '<img src="/images/up.png" alt="Up">';
            } else if (predictedPrice < highPrice) {
                downElement.innerHTML = '<img src="/images/down.png" alt="Down">';
            }
        }).catch(error => {
            console.error('Denormalization error:', error);
            document.getElementById('predict-output').innerText = "예측 중 오류가 발생했습니다.";
        });
    }).catch(error => {
        console.error('Prediction error:', error);
        document.getElementById('predict-output').innerText = "예측 중 오류가 발생했습니다.";
    });
}

// 페이지 로딩 후 초기화
window.onload = () => {
    document.getElementById("train-button").addEventListener("click", train);
    document.getElementById("predict-button").addEventListener("click", predict);
};
