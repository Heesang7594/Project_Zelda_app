import urllib.request
import json
import re
import os

def get_wiki_image(character):
    url = f'https://zelda.fandom.com/api.php?action=query&titles={character}&prop=pageimages&format=json&pithumbsize=500'
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        response = urllib.request.urlopen(req)
        data = json.loads(response.read())
        pages = data['query']['pages']
        for page_id in pages:
            if 'thumbnail' in pages[page_id]:
                return pages[page_id]['thumbnail']['source']
    except Exception as e:
        print(f'Error fetching {character}: {e}')
    return None

chars = {'zelda_link': 'Link', 'zelda_princess': 'Princess_Zelda', 'zelda_ganon': 'Ganondorf', 'zelda_champion': 'Tulin'}
for name, query in chars.items():
    img_url = get_wiki_image(query)
    if img_url:
        print(f'{name}: {img_url}')
        filepath = f'app/src/main/res/drawable/{name}.png'
        # Download
        req = urllib.request.Request(img_url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response, open(filepath, 'wb') as out_file:
            out_file.write(response.read())
        print(f'Downloaded {name} to {filepath}')
