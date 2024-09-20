import requests  # HTTP 요청을 보내기 위한 라이브러리
from bs4 import BeautifulSoup  # HTML/XML 파싱을 위한 라이브러리
import csv  # CSV 파일에 데이터를 기록하기 위한 라이브러리

# 로또 당첨 정보를 제공하는 웹사이트의 URL
main_url = "https://www.dhlottery.co.kr/gameResult.do?method=byWin"  # 최신 회차 정보를 가져오는 메인 URL
basic_url = "https://www.dhlottery.co.kr/gameResult.do?method=byWin&drwNo="  # 특정 회차 정보를 가져오기 위한 URL (회차 번호를 붙여 사용)

# 최신 회차 번호를 가져오는 함수
def GetLast():
    # main_url로 HTTP GET 요청을 보내 HTML 페이지를 받아옴
    resp = requests.get(main_url)
    # 받아온 HTML을 BeautifulSoup로 파싱, lxml 파서를 사용하여 더 빠르게 처리
    soup = BeautifulSoup(resp.text, "lxml")

    # meta 태그에서 description 속성의 content를 찾아서 최신 회차 정보를 가져옴
    result = str(soup.find("meta", {"id": "desc", "name": "description"})['content'])

    # '회차'라는 단어 이전에 있는 숫자를 추출 (최신 회차 번호)
    s_idx = result.find(" ")  # 숫자가 시작되는 인덱스 위치
    e_idx = result.find("회")  # 회차가 끝나는 인덱스 위치
    # 최신 회차 번호를 추출하여 정수로 변환 후 반환
    return int(result[s_idx + 1: e_idx])

# 지정된 범위의 로또 회차 데이터를 크롤링하고 CSV 파일로 저장하는 함수
def Crawler(s_count, e_count, file_name):
    # 지정된 파일 이름으로 CSV 파일을 생성 (쓰기 모드로 파일 열기)
    with open(file_name, 'w', newline='') as fp:
        # CSV 파일에 기록할 writer 객체 생성
        writer = csv.writer(fp)
        # CSV 파일의 첫 번째 행에 컬럼 이름을 기록 (헤더)
        writer.writerow(['DrawNo', 'Number1', 'Number2', 'Number3', 'Number4', 'Number5', 'Number6', 'Bonus', '1st Prize', '2nd Prize', '3rd Prize', '4th Prize', '5th Prize'])

        # 시작 회차부터 종료 회차까지 데이터를 가져오기 위한 반복문
        for i in range(s_count, e_count + 1):
            # 각 회차별로 URL을 생성 (회차 번호를 URL에 추가)
            crawler_url = basic_url + str(i)
            # 해당 회차의 URL로 HTTP GET 요청을 보내 HTML 페이지를 받아옴
            resp = requests.get(crawler_url)
            # HTML 페이지를 BeautifulSoup로 파싱
            soup = BeautifulSoup(resp.text, "html.parser")

            # 로또 당첨 번호를 추출 (당첨 번호가 있는 span 태그, 클래스 이름 'ball_645'로 선택)
            numbers = [num.text for num in soup.select('.ball_645')]
            # numbers 리스트에서 마지막 번호는 보너스 번호이므로 이를 따로 분리
            bonus = numbers.pop()  # 보너스 번호 추출

            # 당첨금 정보 추출 (각 등수별 상금이 들어있는 'tar' 클래스를 가진 td 태그를 선택)
            prize_money = soup.find_all('td', {'class': 'tar'})
            # 숫자만 추출하도록 '원' 기호와 쉼표 제거
            money1 = prize_money[0].text.strip().replace(',', '').replace('원', '')  # 1등 상금
            money2 = prize_money[1].text.strip().replace(',', '').replace('원', '')  # 2등 상금
            money3 = prize_money[2].text.strip().replace(',', '').replace('원', '')  # 3등 상금
            money4 = prize_money[3].text.strip().replace(',', '').replace('원', '')  # 4등 상금
            money5 = prize_money[4].text.strip().replace(',', '').replace('원', '')  # 5등 상금

            # 한 회차의 데이터를 리스트 형태로 만듦 (각 회차의 번호와 상금을 포함)
            row = [str(i)] + numbers + [bonus, money1, money2, money3, money4, money5]
            # 디버깅용으로 콘솔에 출력 (각 회차의 데이터를 확인할 수 있음)
            print(','.join(row))
            # CSV 파일에 데이터를 기록 (한 행씩)
            writer.writerow(row)

# 프로그램 실행 부분

# GetLast 함수를 호출하여 최신 회차 번호를 가져옴
last = GetLast()  # 최신 회차를 가져옴
# 1회부터 최신 회차까지 데이터를 크롤링하여 lotto_data.csv 파일에 저장
Crawler(1, last, 'lotto_data.csv')
