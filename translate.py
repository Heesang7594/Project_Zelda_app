import requests
import json
import time
from deep_translator import GoogleTranslator

# 1. 변경된 '새로운 API 주소'에서 왕국의 눈물 데이터 불러오기
print("🌐 API에서 데이터를 불러오는 중...")
url = "https://api.hyrule-compendium.com/v3/compendium/all?game=totk"

# 봇(Bot) 차단을 막기 위해 일반 브라우저인 것처럼 헤더 추가
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

response = requests.get(url, headers=headers)

if response.status_code == 200:
    api_data = response.json()
    items = api_data.get("data", [])
    print(f"✅ 총 {len(items)}개의 데이터를 성공적으로 불러왔습니다.\n")
else:
    print(f"❌ 데이터를 불러오는데 실패했습니다. (에러 코드: {response.status_code})")
    exit()

# 2. 한/영 번역기 초기화
translator = GoogleTranslator(source='en', target='ko')

# 번역 중 발생할 수 있는 에러를 방지하기 위한 번역 도우미 함수
def safe_translate(text):
    if not text: # 텍스트가 비어있거나 None이면 그대로 반환
        return text
    try:
        return translator.translate(text)
    except Exception as e:
        print(f"⚠️ 번역 오류 발생 ('{text}'): {e}")
        return text

# 3. 데이터 순회하며 번역 수행
print("🔄 모든 텍스트 항목에 대한 번역을 시작합니다. (데이터 양이 많아 시간이 조금 걸릴 수 있습니다.)")

for i, item in enumerate(items):
    # 3-1. 단순 문자열 데이터 번역 (name, description, category)
    if item.get("name"):
        item["name_ko"] = safe_translate(item["name"])
        
    if item.get("description"):
        item["description_ko"] = safe_translate(item["description"])
        
    if item.get("category"):
        item["category_ko"] = safe_translate(item["category"])

    # 3-2. 리스트(배열) 형태의 데이터 번역 (common_locations, drops)
    # 리스트 안의 각 단어들을 뽑아내어 하나씩 번역한 뒤 새로운 리스트로 저장합니다.
    if item.get("common_locations"):
        item["common_locations_ko"] = [safe_translate(loc) for loc in item["common_locations"]]
        
    if item.get("drops"):
        item["drops_ko"] = [safe_translate(drop) for drop in item["drops"]]
        
    # 진행 상황 출력
    if (i + 1) % 10 == 0:
        print(f"진행 상황: {i + 1} / {len(items)} 완료... (최근 번역: {item.get('name_ko')})")
        
    # 구글 번역기 서버 차단을 막기 위한 짧은 대기 시간
    time.sleep(0.1) 

# 4. 번역된 데이터를 새로운 JSON 파일로 저장
file_name = "totk_compendium_ko.json"
with open(file_name, "w", encoding="utf-8") as file:
    json.dump(api_data, file, ensure_ascii=False, indent=4)

print(f"\n✨ 모든 항목의 번역이 완료되었습니다! 같은 폴더에 생긴 '{file_name}' 파일을 확인해 보세요.")