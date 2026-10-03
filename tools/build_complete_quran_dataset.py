#!/usr/bin/env python3
import urllib.request
import json
import re
import os
import sys
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) NoorQuran/1.0"
}

def clean_translation(text):
    if not text:
        return ""
    # Strip footnotes like <sup foot_note=195932>1</sup> and other html tags
    cleaned = re.sub(r'<sup[^>]*>.*?</sup>', '', text)
    cleaned = re.sub(r'<[^>]+>', '', cleaned)
    return cleaned.strip()

def fetch_json(url, retries=3):
    for attempt in range(retries):
        try:
            req = urllib.request.Request(url, headers=HEADERS)
            with urllib.request.urlopen(req, timeout=45) as resp:
                return json.loads(resp.read().decode('utf-8'))
        except Exception as e:
            if attempt == retries - 1:
                raise e
            time.sleep(1.5 * (attempt + 1))

def process_juz(juz_num, chapters_map, output_dir):
    print(f"Processing Juz {juz_num}...")
    
    # 1. Fetch translations for Juz
    trans_url = f"https://api.quran.com/api/v4/quran/translations/20?juz_number={juz_num}"
    trans_data = fetch_json(trans_url)
    translations = [clean_translation(t["text"]) for t in trans_data["translations"]]

    # 2. Fetch verses with words and uthmani text (handle pagination)
    all_verses = []
    page = 1
    while True:
        verses_url = f"https://api.quran.com/api/v4/verses/by_juz/{juz_num}?page={page}&per_page=300&words=true&word_fields=text_uthmani&fields=text_uthmani,chapter_id"
        data = fetch_json(verses_url)
        all_verses.extend(data["verses"])
        pagination = data.get("pagination", {})
        if page >= pagination.get("total_pages", 1):
            break
        page += 1

    if len(all_verses) != len(translations):
        raise ValueError(f"Juz {juz_num} count mismatch: {len(all_verses)} verses vs {len(translations)} translations")

    # 3. Assemble structured data
    juz_ayahs = []
    for idx, v in enumerate(all_verses):
        ch_id = v.get("chapter_id")
        ch_info = chapters_map.get(ch_id, {"name_simple": "Unknown", "name_arabic": "Unknown"})
        
        words_list = []
        raw_words = v.get("words", [])
        w_pos = 1
        for w in raw_words:
            if w.get("char_type_name") == "end":
                continue
            ar_word = w.get("text_uthmani") or w.get("text") or ""
            trans_obj = w.get("translation") or {}
            en_word = trans_obj.get("text") or ""
            translit_obj = w.get("transliteration") or {}
            translit_text = translit_obj.get("text")
            
            words_list.append({
                "id": w.get("id", w_pos),
                "position": w_pos,
                "arabic": ar_word,
                "english": en_word,
                "transliteration": translit_text
            })
            w_pos += 1

        ayah_item = {
            "surahNumber": ch_id,
            "ayahNumber": v.get("verse_number"),
            "surahNameArabic": ch_info["name_arabic"],
            "surahNameEnglish": ch_info["name_simple"],
            "juzNumber": juz_num,
            "arabicText": v.get("text_uthmani", ""),
            "englishTranslation": translations[idx],
            "words": words_list
        }
        juz_ayahs.append(ayah_item)

    # 4. Save to JSON
    juz_path = os.path.join(output_dir, f"juz_{juz_num}.json")
    with open(juz_path, "w", encoding="utf-8") as f:
        json.dump(juz_ayahs, f, ensure_ascii=False, indent=None)

    print(f"✓ Saved Juz {juz_num}: {len(juz_ayahs)} Ayahs")
    return juz_num, len(juz_ayahs)

def main():
    output_dir = "app/src/main/assets/quran"
    os.makedirs(output_dir, exist_ok=True)

    print("Fetching 114 Surahs metadata...")
    ch_data = fetch_json("https://api.quran.com/api/v4/chapters?language=en")
    chapters_map = {
        c["id"]: {
            "name_simple": c["name_simple"],
            "name_arabic": c["name_arabic"],
            "verses_count": c["verses_count"]
        } for c in ch_data["chapters"]
    }
    
    surahs_summary = [
        {
            "number": c["id"],
            "nameSimple": c["name_simple"],
            "nameArabic": c["name_arabic"],
            "nameComplex": c["name_complex"],
            "versesCount": c["verses_count"],
            "revelationPlace": c["revelation_place"]
        } for c in ch_data["chapters"]
    ]
    with open(os.path.join(output_dir, "surahs.json"), "w", encoding="utf-8") as f:
        json.dump(surahs_summary, f, ensure_ascii=False, indent=2)
    print(f"Saved {len(surahs_summary)} surahs metadata to surahs.json")

    # Fetch all 30 Juz with concurrency
    total_ayahs = 0
    results = {}
    with ThreadPoolExecutor(max_workers=4) as executor:
        futures = {executor.submit(process_juz, j, chapters_map, output_dir): j for j in range(1, 31)}
        for future in as_completed(futures):
            j_num, count = future.result()
            results[j_num] = count
            total_ayahs += count

    print("\n================== VALIDATION REPORT ==================")
    print(f"Total Juz Processed: {len(results)}/30")
    print(f"Total Ayahs Across 30 Juz: {total_ayahs} (Canonical Holy Quran total is 6,236)")
    assert len(results) == 30, f"Expected 30 Juz, got {len(results)}"
    assert total_ayahs == 6236, f"Expected 6,236 verses, got {total_ayahs}"
    print("ALL 6,236 AYAHS VALIDATED ACROSS ALL 30 JUZ SUCCESSFULLY!")
    print("=======================================================")

if __name__ == "__main__":
    main()
