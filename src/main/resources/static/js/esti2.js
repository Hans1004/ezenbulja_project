const EPOCHS = 5; // 학습 횟수
const LEARNING_RATE = 0.001; // 학습률
let XnormParams, YnormParams, model;
let lossHistory = [];

// 페이지 로드 시 이벤트 리스너 추가
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("train-button").addEventListener("click", trainModel);
    document.getElementById("predict-button").addEventListener("click", predict);
});

// 데이터 정규화 함수
function zScoreNormalization(tensor) {
    const mean = tensor.mean();
    const std = tensor.sub(mean).square().mean().sqrt();
    const normedTensor = tensor.sub(mean).div(std);

    return {
        tensor: normedTensor,
        mean: mean,
        std: std
    };
}

// 데이터 비정규화 함수
function denormalize(tensor, mean, std) {
    return tensor.mul(std).add(mean);
}

async function trainModel() {
    document.getElementById("text").innerHTML = "학습 중...";
    document.getElementById("train-button").setAttribute("disabled", true);

    await train();

    document.getElementById("text").innerHTML = "학습 완료";
    document.getElementById("train-button").style.display = "none"; // 학습하기 버튼 숨기기
    document.getElementById("predict-button").removeAttribute("disabled"); // 예측하기 버튼 활성화
}

function predict() {
    const areaInput = document.getElementById("area-select").value;
    const regionInput = parseInt(document.getElementById("region-select").value);
    const purposeInput = parseInt(document.getElementById("purpose-select").value);

    const areaValue = parseInt(areaInput);

    if (isNaN(areaValue) || isNaN(regionInput) || isNaN(purposeInput) || areaInput === "선택하세요." || regionInput === 0 || purposeInput === 0) {
        alert("모든 값을 올바르게 선택하세요.");
        return;
    }

    const regionOneHot = Array(25).fill(0);
    regionOneHot[regionInput - 1] = 1;

    const purposeOneHot = Array(4).fill(0);
    purposeOneHot[purposeInput - 1] = 1;

    tf.tidy(() => {
        const inputTensor = tf.tensor2d([[areaValue, ...regionOneHot, ...purposeOneHot]]);
        const { tensor: normedInput } = zScoreNormalization(inputTensor);
        const prediction = model.predict(normedInput);
        const denormedPredict = denormalize(prediction, YnormParams.mean.dataSync()[0], YnormParams.std.dataSync()[0]);

        let output = Math.exp(denormedPredict.dataSync()[0]);

        // 아파트일 때 추가 조정
        if (purposeInput === 1) { // 아파트 선택 시
            output *= 1.1; // 예시로 10% 증가
        }

        const outputFormatted = output.toLocaleString('ko-KR', { maximumFractionDigits: 0 });
        document.getElementById("predicted-price").innerText = outputFormatted + ' 원';
    });
}

// 손실 값을 시각화하는 함수
function plotLoss(loss) {
    lossHistory.push(loss);
    const trace = {
        x: Array.from(lossHistory.keys()),
        y: lossHistory,
        mode: 'lines+markers',
        type: 'scatter'
    };
    const data = [trace];
    const layout = {
        title: 'Training Loss',
        xaxis: { title: 'Epoch' },
        yaxis: { title: 'Loss' }
    };
    Plotly.newPlot('loss-plot', data, layout);
}

// 모델 학습 함수
async function train() {
    const HouseSalesDataset = tf.data.csv("/csv/SeoulEst3.csv", {
        columnConfigs: {
            price: { isLabel: true },
            area: { isLabel: false },
            region: { isLabel: false },
            purpose: { isLabel: false }
        },
        configuredColumnsOnly: true
    });

    let dataset = await HouseSalesDataset.map(({ xs, ys }) => ({
        x: [xs.area, xs.region, xs.purpose],
        y: Math.log(ys.price)
    }));

    let dataPoints = await dataset.toArray();
    tf.util.shuffle(dataPoints);

    // 데이터셋 나누기
    const trainSize = Math.floor(dataPoints.length * 0.8);
    const trainData = dataPoints.slice(0, trainSize);
    const testData = dataPoints.slice(trainSize);

    const X_train = tf.tensor2d(trainData.map(dp => {
        const area = dp.x[0];
        const region = dp.x[1];
        const purpose = dp.x[2];

        const regionOneHot = Array(25).fill(0);
        regionOneHot[region - 1] = 1;

        const purposeOneHot = Array(4).fill(0);
        purposeOneHot[purpose - 1] = 1;

        return [area, ...regionOneHot, ...purposeOneHot];
    }));

    const y_train = tf.tensor2d(trainData.map(dp => dp.y), [trainData.length, 1]);

    // 정규화
    const normedXtrain = zScoreNormalization(X_train);
    const normedYtrain = zScoreNormalization(y_train);

    model = tf.sequential();
    model.add(tf.layers.dense({ inputShape: [X_train.shape[1]], units: 256, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 128, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 64, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 1, activation: "linear" }));

    model.compile({ loss: 'meanSquaredError', optimizer: tf.train.adam(LEARNING_RATE) });
    model.summary();

    for (let epoch = 0; epoch < EPOCHS; epoch++) {
        const history = await model.fit(normedXtrain.tensor, normedYtrain.tensor, {
            epochs: 1,
            batchSize: 32,
            callbacks: {
                onEpochEnd: (epoch, logs) => {
                    console.log(`Epoch: ${epoch}, Loss: ${logs.loss}`);
                    plotLoss(logs.loss);
                }
            }
        });
    }

    // 정규화된 값 저장
    XnormParams = { mean: normedXtrain.mean, std: normedXtrain.std };
    YnormParams = { mean: normedYtrain.mean, std: normedYtrain.std };
}
