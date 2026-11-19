#!/usr/bin/env python3
"""
Builds authentic offline page-by-page Mushaf datasets for:
  1. Indo-Pak 16-Line Taj Company Layout (mushaf=7, 548 pages, 16 lines/page)
  2. Madinah 15-Line Uthmani Layout (mushaf=2, 604 pages, 15 lines/page)

Outputs compact JSON assets to app/src/main/assets/mushaf/.
"""
import urllib.request
import json
import os
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) QuranWordByWord/1.0"
}

def fetch_json(url, retries=4):
    for attempt in range(retries):
        try:
            req = urllib.request.Request(url, headers=HEADERS)
            with urllib.request.urlopen(req, timeout=45) as resp:
                return json.loads(resp.read().decode("utf-8"))
        except Exception as e:
            if attempt == retries - 1:
                raise e
            time.sleep(1.5 * (attempt + 1))

def clean_arabic_word(text):
    if not text:
        return ""
    # Strip Private Use Area font-specific glyphs (0xE000..0xF8FF) and directional marks
    out = []
    for ch in text:
        code = ord(ch)
        if 0xE000 <= code <= 0xF8FF:
            continue
        if code in (0x200E, 0x200F, 0xFEFF, 0x061C):
            continue
        out.append(ch)
    return "".join(out).strip()

def fetch_juz_verses(juz_num, mushaf_id, word_field):
    verses = []
    page = 1
    while True:
        url = (
            f"https://api.qurancdn.com/api/qdc/verses/by_juz/{juz_num}"
            f"?mushaf={mushaf_id}&page={page}&per_page=300"
            f"&words=true&word_fields={word_field},line_number,page_number"
            f"&fields=page_number,ruku_number,juz_number,chapter_id"
        )
        data = fetch_json(url)
        verses.extend(data["verses"])
        pagination = data.get("pagination", {})
        if page >= pagination.get("total_pages", 1):
            break
        page += 1
    return juz_num, verses

def build_edition(mushaf_id, word_field, lines_per_page, expected_pages, output_path):
    print(f"\nBuilding Mushaf edition (mushaf={mushaf_id}, {lines_per_page} lines/page)...")
    juz_verses_map = {}
    with ThreadPoolExecutor(max_workers=5) as executor:
        futures = {
            executor.submit(fetch_juz_verses, j, mushaf_id, word_field): j
            for j in range(1, 31)
        }
        for future in as_completed(futures):
            j_num, vs = future.result()
            juz_verses_map[j_num] = vs

    # Combine all 6,236 verses in canonical order
    all_verses = []
    for j in range(1, 31):
        all_verses.extend(juz_verses_map[j])
    assert len(all_verses) == 6236, f"Expected 6236 verses, got {len(all_verses)}"

    # Track pages: page_num -> { "j": juz, "s": primary_surah, "lines": { line_num: {...} } }
    pages = {}
    surah_first_page = {}
    juz_first_page = {}

    def ensure_page(p_num, juz_num, ch_id):
        if p_num not in pages:
            pages[p_num] = {
                "p": p_num,
                "j": juz_num,
                "s": ch_id,
                "lines": {}
            }
        return pages[p_num]

    # Precompute which verses end a Ruku
    ruku_end_verse_keys = set()
    for i, v in enumerate(all_verses):
        curr_ruku = v.get("ruku_number")
        next_ruku = all_verses[i + 1].get("ruku_number") if i + 1 < len(all_verses) else None
        if curr_ruku != next_ruku:
            ruku_end_verse_keys.add(v["verse_key"])

    # Populate text lines from word-level page_number and line_number
    surah_start_anchors = []  # (ch_id, first_page, first_line, juz_num)
    for v in all_verses:
        v_key = v["verse_key"]
        ch_id = v["chapter_id"]
        v_num = v["verse_number"]
        j_num = v["juz_number"]
        is_ruku_end = v_key in ruku_end_verse_keys
        words = v.get("words", [])

        first_word_page = None
        first_word_line = None
        end_word_page = None
        end_word_line = None

        for w in words:
            p_num = w.get("page_number") or v.get("page_number")
            l_num = w.get("line_number")
            if not p_num or not l_num:
                continue

            if first_word_page is None:
                first_word_page = p_num
                first_word_line = l_num

            if ch_id not in surah_first_page or p_num < surah_first_page[ch_id]:
                surah_first_page[ch_id] = p_num
            if j_num not in juz_first_page or p_num < juz_first_page[j_num]:
                juz_first_page[j_num] = p_num

            page_obj = ensure_page(p_num, j_num, ch_id)
            line_entry = page_obj["lines"].setdefault(l_num, {"n": l_num, "t": "t", "toks": [], "r": False})

            if w.get("char_type_name") == "end":
                line_entry["toks"].append(f"E:{v_num}")
                end_word_page = p_num
                end_word_line = l_num
            else:
                cleaned = clean_arabic_word(w.get(word_field) or w.get("text") or "")
                if cleaned:
                    line_entry["toks"].append(cleaned)

        if v_num == 1 and first_word_page is not None:
            surah_start_anchors.append((ch_id, first_word_page, first_word_line, j_num))

        if is_ruku_end and end_word_page is not None and end_word_line is not None:
            pages[end_word_page]["lines"][end_word_line]["r"] = True

    # Place authentic Surah Header ("s") and Bismillah ("b") lines directly above verse 1
    for ch_id, p_num, first_line, j_num in surah_start_anchors:
        has_bismillah = (ch_id != 1 and ch_id != 9)
        page_lines = pages[p_num]["lines"]

        if has_bismillah:
            if first_line >= 3:
                if (first_line - 1) not in page_lines:
                    page_lines[first_line - 1] = {"n": first_line - 1, "t": "b", "ch": ch_id}
                if (first_line - 2) not in page_lines:
                    page_lines[first_line - 2] = {"n": first_line - 2, "t": "s", "ch": ch_id}
            elif first_line == 2:
                if 1 not in page_lines:
                    page_lines[1] = {"n": 1, "t": "b", "ch": ch_id}
                # Put Surah banner at bottom of previous page if free
                if p_num > 1 and (p_num - 1) in pages and lines_per_page not in pages[p_num - 1]["lines"]:
                    pages[p_num - 1]["lines"][lines_per_page] = {"n": lines_per_page, "t": "s", "ch": ch_id}
                else:
                    # Combine banner tag on Bismillah line if prev page full
                    page_lines[1]["bannerCh"] = ch_id
            elif first_line == 1:
                # Surah starts on line 1: place banner & Bismillah on empty bottom lines of previous page
                if p_num > 1 and (p_num - 1) in pages:
                    prev_lines = pages[p_num - 1]["lines"]
                    if lines_per_page not in prev_lines and (lines_per_page - 1) not in prev_lines:
                        prev_lines[lines_per_page - 1] = {"n": lines_per_page - 1, "t": "s", "ch": ch_id}
                        prev_lines[lines_per_page] = {"n": lines_per_page, "t": "b", "ch": ch_id}
                    elif lines_per_page not in prev_lines:
                        prev_lines[lines_per_page] = {"n": lines_per_page, "t": "s", "ch": ch_id}
        else:
            # Surah 1 (Al-Fatihah) or Surah 9 (At-Tawbah): header banner only
            if first_line >= 2:
                if (first_line - 1) not in page_lines:
                    page_lines[first_line - 1] = {"n": first_line - 1, "t": "s", "ch": ch_id}
            elif first_line == 1 and p_num > 1 and (p_num - 1) in pages:
                prev_lines = pages[p_num - 1]["lines"]
                if lines_per_page not in prev_lines:
                    prev_lines[lines_per_page] = {"n": lines_per_page, "t": "s", "ch": ch_id}

    # Assemble sorted page array
    sorted_page_nums = sorted(pages.keys())
    print(f"Generated {len(sorted_page_nums)} pages (range {sorted_page_nums[0]}..{sorted_page_nums[-1]}, expected {expected_pages})")
    assert len(sorted_page_nums) == expected_pages, f"Expected {expected_pages} pages, got {len(sorted_page_nums)}"

    pages_list = []
    for p_num in sorted_page_nums:
        p = pages[p_num]
        sorted_lines = [p["lines"][ln] for ln in sorted(p["lines"].keys())]
        # Compact representation: omit r=False to save size
        compact_lines = []
        for ln in sorted_lines:
            item = {"n": ln["n"], "t": ln["t"]}
            if ln["t"] == "t":
                item["toks"] = ln["toks"]
                if ln.get("r"):
                    item["r"] = True
            else:
                item["ch"] = ln["ch"]
                if "bannerCh" in ln:
                    item["bannerCh"] = ln["bannerCh"]
            compact_lines.append(item)
        pages_list.append({
            "p": p["p"],
            "j": p["j"],
            "s": p["s"],
            "lines": compact_lines
        })

    payload = {
        "edition": "indopak_16" if lines_per_page == 16 else "madinah_15",
        "linesPerPage": lines_per_page,
        "totalPages": len(pages_list),
        "surahPages": {str(k): v for k, v in sorted(surah_first_page.items())},
        "juzPages": {str(k): v for k, v in sorted(juz_first_page.items())},
        "pages": pages_list
    }

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, separators=(",", ":"))
    size_mb = os.path.getsize(output_path) / (1024 * 1024)
    print(f"✓ Saved {output_path} ({size_mb:.2f} MB)")

def main():
    build_edition(
        mushaf_id=7,
        word_field="text_indopak",
        lines_per_page=16,
        expected_pages=548,
        output_path="app/src/main/assets/mushaf/indopak_16.json"
    )
    build_edition(
        mushaf_id=2,
        word_field="text_uthmani",
        lines_per_page=15,
        expected_pages=604,
        output_path="app/src/main/assets/mushaf/madinah_15.json"
    )
    print("\nALL MUSHAF PAGE DATASETS BUILT & VALIDATED SUCCESSFULLY!")

if __name__ == "__main__":
    main()
