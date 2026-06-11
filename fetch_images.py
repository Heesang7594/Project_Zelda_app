import urllib.request
import json
import os
import re

def search_duckduckgo(query, filename):
    url = f"https://html.duckduckgo.com/html/?q={urllib.parse.quote(query)}"
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        response = urllib.request.urlopen(req)
        html = response.read().decode('utf-8')
        # Find first image source
        match = re.search(r'img class="tile--img__img" src="([^"]+)"', html)
        if match:
            img_url = "https:" + match.group(1) if match.group(1).startswith('//') else match.group(1)
            print(f"Downloading {filename} from {img_url}")
            urllib.request.urlretrieve(img_url, filename)
            return True
    except Exception as e:
        print(f"Failed {query}: {e}")
    return False

# Fallback direct download from stable sources if possible, or just search
os.makedirs("app/src/main/res/drawable", exist_ok=True)
import urllib.parse
queries = {
    "zelda_link.png": "Zelda Tears of the Kingdom official render Link filetype:png",
    "zelda_princess.png": "Zelda Tears of the Kingdom official render Zelda filetype:png",
    "zelda_ganon.png": "Zelda Tears of the Kingdom official render Ganondorf filetype:png",
    "zelda_champion.png": "Zelda Tears of the Kingdom official render Purah filetype:png"
}

for fname, q in queries.items():
    search_duckduckgo(q, "app/src/main/res/drawable/" + fname)
