#!/usr/bin/env python3
import json
import os
import sys

def validate():
    assets_dir = "app/src/main/assets/quran"
    if not os.path.exists(assets_dir):
        print("FAIL: Directory not found!")
        sys.exit(1)

    surahs_file = os.path.join(assets_dir, "surahs.json")
    if not os.path.exists(surahs_file):
        print("FAIL: surahs.json not found!")
        sys.exit(1)

    with open(surahs_file, "r", encoding="utf-8") as f:
        surahs = json.load(f)
    print(f"Surahs metadata count: {len(surahs)}")
    assert len(surahs) == 114, f"Expected 114 surahs, got {len(surahs)}"

    total_verses = 0
    surahs_seen = set()

    for j in range(1, 31):
        juz_file = os.path.join(assets_dir, f"juz_{j}.json")
        if not os.path.exists(juz_file):
            print(f"FAIL: juz_{j}.json missing!")
            sys.exit(1)
        with open(juz_file, "r", encoding="utf-8") as f:
            ayahs = json.load(f)
        
        count = len(ayahs)
        total_verses += count
        
        # Check every ayah in this juz
        for a in ayahs:
            assert a["juzNumber"] == j, f"Ayah {a['surahNumber']}:{a['ayahNumber']} has wrong juz {a['juzNumber']}"
            assert a["arabicText"], f"Empty arabic in {a['surahNumber']}:{a['ayahNumber']}"
            assert a["englishTranslation"], f"Empty translation in {a['surahNumber']}:{a['ayahNumber']}"
            assert len(a["words"]) > 0, f"No words in {a['surahNumber']}:{a['ayahNumber']}"
            surahs_seen.add(a["surahNumber"])

        print(f"Juz {j:2d}: {count:3d} Ayahs | Start: Surah {ayahs[0]['surahNumber']}:{ayahs[0]['ayahNumber']} | End: Surah {ayahs[-1]['surahNumber']}:{ayahs[-1]['ayahNumber']}")

    print("--------------------------------------------------")
    print(f"Total Ayahs across all 30 Juz: {total_verses}")
    print(f"Total Unique Surahs covered: {len(surahs_seen)}/114")
    assert total_verses == 6236, f"Expected 6,236 verses, got {total_verses}"
    assert len(surahs_seen) == 114, f"Expected all 114 surahs, got {len(surahs_seen)}"
    print("ALL VALIDATION CHECKS PASSED PERFECTLY!")

if __name__ == "__main__":
    validate()
