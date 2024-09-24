document.addEventListener('DOMContentLoaded', function() {
    const fetchLatestData = async () => {
        try {
            const response = await fetch('./csv/upbit_btc_data_with_rsi.csv');
            if (!response.ok) {
                throw new Error('CSV 파일을 가져오는 중 오류가 발생했습니다.');
            }

            const csvText = await response.text();

            // CSV 파싱
            const rows = csvText.trim().split('\n').map(row => row.split(','));
            const header = rows[0];
            const latestRow = rows[rows.length - 1]; // 최신 데이터 (마지막 행)

            console.log('Header:', header);
            console.log('Latest Row:', latestRow);

            const data = {};
            header.forEach((key, index) => {
                data[key] = latestRow[index];
            });

            console.log('Data:', data);

            // 데이터 입력 필드에 채우기
            document.getElementById('predict-input-opening-price').value = data['opening_price'];
            document.getElementById('predict-input-high-price').value = data['trade_price'];
            document.getElementById('predict-input-low-price').value = data['low_price'];
            document.getElementById('predict-input-volume').value = data['candle_acc_trade_volume'];
            document.getElementById('predict-input-prev-close').value = data['prev_closing_price'];
            document.getElementById('predict-input-change-price').value = data['change_price'];
            document.getElementById('predict-input-rsi').value = data['RSI'];

            // document.getElementById('predict-button').disabled = false;
        } catch (error) {
            console.error('데이터를 가져오는 중 오류가 발생했습니다:', error);
        }
    };

    document.getElementById('fetch-latest-button').addEventListener('click', fetchLatestData);
});