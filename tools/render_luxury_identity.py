#!/usr/bin/env python3
import subprocess
import os

def render_luxury_icon(output_path, size=512, is_round=False):
    s = size / 512.0
    def sc(val):
        return int(round(val * s))

    cmd = ["convert", "-size", f"{size}x{size}", "xc:none"]

    # 1. Base Canvas
    if is_round:
        rad = size // 2
        cmd.extend([
            "-fill", "#081017",
            "-draw", f"circle {rad},{rad} {rad},0"
        ])
    else:
        r = sc(105)
        cmd.extend([
            "-fill", "#081017",
            "-draw", f"roundrectangle 0,0 {size-1},{size-1} {r},{r}"
        ])

    # 2. Subtle Velvet Radial Illumination
    cmd.extend([
        "-fill", "#152433",
        "-draw", f"circle {sc(256)},{sc(245)} {sc(256)},{sc(110)}"
    ])

    # 3. Outer Octagonal Hairline Geometric Frame
    cmd.extend([
        "-stroke", "#1C364A",
        "-strokewidth", str(max(1, sc(2.2))),
        "-fill", "none",
        "-draw", f"polygon {sc(162)},{sc(85)} {sc(350)},{sc(85)} {sc(427)},{sc(162)} {sc(427)},{sc(350)} {sc(350)},{sc(427)} {sc(162)},{sc(427)} {sc(85)},{sc(350)} {sc(85)},{sc(162)}"
    ])

    # 4. Subtle 8-Pointed Star Lattice Halo
    cmd.extend([
        "-stroke", "#304A3C",
        "-strokewidth", str(max(1, sc(1.6))),
        "-fill", "none",
        "-draw", f"polygon {sc(256)},{sc(110)} {sc(298)},{sc(156)} {sc(365)},{sc(156)} {sc(336)},{sc(218)} {sc(375)},{sc(270)} {sc(323)},{sc(304)} {sc(323)},{sc(370)} {sc(256)},{sc(342)} {sc(189)},{sc(370)} {sc(189)},{sc(304)} {sc(137)},{sc(270)} {sc(176)},{sc(218)} {sc(147)},{sc(156)} {sc(214)},{sc(156)}"
    ])

    # 5. Pedestal Base (Rehal Silhouette in Champagne Bronze/Gold)
    # Floor shadow
    cmd.extend([
        "-stroke", "none",
        "-fill", "#02060A",
        "-draw", f"ellipse {sc(256)},{sc(398)} {sc(105)},{sc(12)} 0,360"
    ])
    # Left leg
    cmd.extend([
        "-fill", "#967232",
        "-draw", f"polygon {sc(175)},{sc(384)} {sc(208)},{sc(384)} {sc(265)},{sc(327)} {sc(251)},{sc(313)}"
    ])
    cmd.extend([
        "-fill", "#BA934A",
        "-draw", f"polygon {sc(185)},{sc(379)} {sc(208)},{sc(384)} {sc(256)},{sc(332)} {sc(242)},{sc(322)}"
    ])
    # Right leg
    cmd.extend([
        "-fill", "#CBA255",
        "-draw", f"polygon {sc(337)},{sc(384)} {sc(304)},{sc(384)} {sc(247)},{sc(327)} {sc(261)},{sc(313)}"
    ])
    cmd.extend([
        "-fill", "#E5C57E",
        "-draw", f"polygon {sc(327)},{sc(379)} {sc(304)},{sc(384)} {sc(256)},{sc(332)} {sc(270)},{sc(322)}"
    ])
    # Pivot Diamond Jewel
    cmd.extend([
        "-fill", "#F6E7BA",
        "-draw", f"polygon {sc(256)},{sc(339)} {sc(268)},{sc(351)} {sc(256)},{sc(363)} {sc(244)},{sc(351)}"
    ])

    # 6. Quran Binding Foundation (Deep Emerald Trim & Gold Rim)
    cmd.extend([
        "-fill", "#092E22",
        "-draw", f"polygon {sc(128)},{sc(280)} {sc(256)},{sc(337)} {sc(384)},{sc(280)} {sc(379)},{sc(294)} {sc(256)},{sc(346)} {sc(133)},{sc(294)}"
    ])
    cmd.extend([
        "-fill", "#D8B568",
        "-draw", f"polygon {sc(133)},{sc(275)} {sc(256)},{sc(332)} {sc(379)},{sc(275)} {sc(375)},{sc(284)} {sc(256)},{sc(337)} {sc(137)},{sc(284)}"
    ])

    # 7. Open Quran Main Leaves (Luminous Ivory & Architectural Curvature)
    # Left Leaf
    cmd.extend([
        "-fill", "#FAF6ED",
        "-draw", f"bezier {sc(256)},{sc(327)} {sc(228)},{sc(313)} {sc(171)},{sc(304)} {sc(133)},{sc(265)} {sc(133)},{sc(237)} {sc(162)},{sc(199)} {sc(195)},{sc(175)} {sc(228)},{sc(151)} {sc(247)},{sc(133)} {sc(256)},{sc(123)} {sc(256)},{sc(327)}"
    ])
    # Left leaf depth shade
    cmd.extend([
        "-fill", "#E4D7C0",
        "-draw", f"bezier {sc(256)},{sc(327)} {sc(242)},{sc(298)} {sc(228)},{sc(275)} {sc(208)},{sc(251)} {sc(228)},{sc(218)} {sc(251)},{sc(166)} {sc(256)},{sc(123)} {sc(256)},{sc(327)}"
    ])

    # Right Leaf
    cmd.extend([
        "-fill", "#FFFDF9",
        "-draw", f"bezier {sc(256)},{sc(327)} {sc(284)},{sc(313)} {sc(341)},{sc(304)} {sc(379)},{sc(265)} {sc(379)},{sc(237)} {sc(350)},{sc(199)} {sc(317)},{sc(175)} {sc(284)},{sc(151)} {sc(265)},{sc(133)} {sc(256)},{sc(123)} {sc(256)},{sc(327)}"
    ])
    # Right leaf tone
    cmd.extend([
        "-fill", "#F0E5D3",
        "-draw", f"bezier {sc(256)},{sc(327)} {sc(270)},{sc(298)} {sc(284)},{sc(275)} {sc(304)},{sc(251)} {sc(284)},{sc(218)} {sc(261)},{sc(166)} {sc(256)},{sc(123)} {sc(256)},{sc(327)}"
    ])

    # 8. Sculpted Champagne Gold Arch Ribbons (Mihrab Profile)
    cmd.extend([
        "-fill", "#C8A458",
        "-draw", f"bezier {sc(256)},{sc(123)} {sc(242)},{sc(151)} {sc(218)},{sc(180)} {sc(195)},{sc(195)} {sc(171)},{sc(208)} {sc(147)},{sc(228)} {sc(137)},{sc(256)} {sc(147)},{sc(246)} {sc(180)},{sc(223)} {sc(204)},{sc(208)} {sc(228)},{sc(195)} {sc(247)},{sc(162)} {sc(256)},{sc(137)} {sc(256)},{sc(123)}"
    ])
    cmd.extend([
        "-fill", "#F6E7BE",
        "-draw", f"bezier {sc(256)},{sc(123)} {sc(251)},{sc(133)} {sc(232)},{sc(162)} {sc(208)},{sc(180)} {sc(185)},{sc(199)} {sc(156)},{sc(218)} {sc(142)},{sc(242)} {sc(151)},{sc(228)} {sc(180)},{sc(208)} {sc(204)},{sc(190)} {sc(228)},{sc(171)} {sc(247)},{sc(142)} {sc(256)},{sc(123)}"
    ])
    cmd.extend([
        "-fill", "#DDBB71",
        "-draw", f"bezier {sc(256)},{sc(123)} {sc(270)},{sc(151)} {sc(294)},{sc(180)} {sc(317)},{sc(195)} {sc(341)},{sc(208)} {sc(365)},{sc(228)} {sc(375)},{sc(256)} {sc(365)},{sc(246)} {sc(332)},{sc(223)} {sc(308)},{sc(208)} {sc(284)},{sc(195)} {sc(265)},{sc(162)} {sc(256)},{sc(137)} {sc(256)},{sc(123)}"
    ])
    cmd.extend([
        "-fill", "#FFF0CE",
        "-draw", f"bezier {sc(256)},{sc(123)} {sc(261)},{sc(133)} {sc(280)},{sc(162)} {sc(304)},{sc(180)} {sc(327)},{sc(199)} {sc(356)},{sc(218)} {sc(370)},{sc(242)} {sc(361)},{sc(228)} {sc(332)},{sc(208)} {sc(308)},{sc(190)} {sc(284)},{sc(171)} {sc(265)},{sc(142)} {sc(256)},{sc(123)}"
    ])

    # 9. Stepped Meaning Facets (Horizontal tier accents)
    cmd.extend([
        "-fill", "#EAD8A6",
        "-draw", f"roundrectangle {sc(165)},{sc(265)} {sc(200)},{sc(273)} {sc(3)},{sc(3)}",
        "-draw", f"roundrectangle {sc(180)},{sc(280)} {sc(212)},{sc(287)} {sc(3)},{sc(3)}",
        "-draw", f"roundrectangle {sc(312)},{sc(265)} {sc(347)},{sc(273)} {sc(3)},{sc(3)}",
        "-draw", f"roundrectangle {sc(300)},{sc(280)} {sc(332)},{sc(287)} {sc(3)},{sc(3)}"
    ])

    # 10. Central Bookmark Ribbon Tassel
    cmd.extend([
        "-fill", "#E5C77A",
        "-draw", f"polygon {sc(253)},{sc(327)} {sc(259)},{sc(327)} {sc(260)},{sc(380)} {sc(254)},{sc(380)}"
    ])
    cmd.extend([
        "-fill", "#C49F4E",
        "-draw", f"polygon {sc(253)},{sc(380)} {sc(257)},{sc(390)} {sc(261)},{sc(380)}"
    ])

    # 11. Central Luminous Star Rosette (Rub-el-Hizb & Noor Jewel)
    # Ambient halo
    cmd.extend([
        "-fill", "#284D3B",
        "-draw", f"circle {sc(256)},{sc(208)} {sc(256)},{sc(172)}"
    ])
    # Outer 8-pointed star
    # Square 1
    cmd.extend([
        "-fill", "#DFC07C",
        "-draw", f"roundrectangle {sc(236)},{sc(188)} {sc(276)},{sc(228)} {sc(3)},{sc(3)}"
    ])
    # Square 2 (Rotated 45 degrees)
    cmd.extend([
        "-fill", "#EDD496",
        "-draw", f"polygon {sc(256)},{sc(180)} {sc(284)},{sc(208)} {sc(256)},{sc(236)} {sc(228)},{sc(208)}"
    ])
    # Inner Pearl Diamond Core
    cmd.extend([
        "-fill", "#FFFDF8",
        "-draw", f"polygon {sc(256)},{sc(195)} {sc(269)},{sc(208)} {sc(256)},{sc(221)} {sc(243)},{sc(208)}"
    ])
    # Micro Gold Center
    cmd.extend([
        "-fill", "#B38E37",
        "-draw", f"circle {sc(256)},{sc(208)} {sc(256)},{sc(204)}"
    ])

    cmd.append(f"PNG32:{output_path}")
    subprocess.run(cmd, check=True)

def render_feature_graphic(output_path):
    # 1024x500 Feature Graphic with updated brand name "Quran Word by Word Meanings"
    cmd = [
        "convert",
        "-size", "1024x500",
        "xc:#081017"
    ]
    # Subtle velvet radial glow
    cmd.extend([
        "-fill", "#12202C",
        "-draw", "circle 512,250 512,50"
    ])
    # Elegant geometric framing line
    cmd.extend([
        "-stroke", "#1C364A",
        "-strokewidth", "1.5",
        "-fill", "none",
        "-draw", "roundrectangle 24,24 1000,476 16,16"
    ])
    # Background Star Motifs Left & Right
    cmd.extend([
        "-stroke", "#203A4E",
        "-strokewidth", "1.5",
        "-fill", "none",
        "-draw", "polygon 180,80 240,140 180,200 120,140",
        "-draw", "polygon 844,80 904,140 844,200 784,140"
    ])
    # Brand Title
    cmd.extend([
        "-fill", "#FFFFFF",
        "-pointsize", "44",
        "-font", "DejaVu-Sans-Bold",
        "-gravity", "North",
        "-annotate", "+0+64", "Quran by Word English"
    ])
    cmd.extend([
        "-fill", "#DFC07C",
        "-pointsize", "20",
        "-font", "DejaVu-Sans",
        "-gravity", "North",
        "-annotate", "+0+122", "READ  •  UNDERSTAND  •  REFLECT"
    ])
    cmd.extend([
        "-fill", "#B0C6D4",
        "-pointsize", "17",
        "-font", "DejaVu-Sans",
        "-gravity", "North",
        "-annotate", "+0+164", "Authentic Uthmani Script  |  Precision Word-by-Word  |  100% Offline"
    ])
    # Refined Gold hairline divider
    cmd.extend([
        "-stroke", "#DFC07C",
        "-strokewidth", "2",
        "-draw", "line 384,205 640,205"
    ])
    # Feature pills at bottom
    cmd.extend([
        "-stroke", "#DFC07C",
        "-strokewidth", "1",
        "-fill", "#0C1721",
        "-draw", "roundrectangle 90,380 330,430 14,14",
        "-draw", "roundrectangle 360,380 664,430 14,14",
        "-draw", "roundrectangle 694,380 934,430 14,14"
    ])
    cmd.extend([
        "-fill", "#FFFFFF",
        "-pointsize", "15",
        "-font", "DejaVu-Sans",
        "-gravity", "NorthWest",
        "-annotate", "+124+397", "Word-by-Word Cards",
        "-annotate", "+400+397", "Continuous & Swipe Modes",
        "-annotate", "+750+397", "Smooth Auto-Scroll"
    ])
    cmd.append(f"PNG32:{output_path}")
    subprocess.run(cmd, check=True)

# Generate 512x512 Play Store icons
png_512 = "app/src/main/res/drawable/play_store_icon.png"
render_luxury_icon(png_512, 512, is_round=False)
subprocess.run(["cp", png_512, "app/src/main/res/drawable/ic_launcher_store.png"], check=True)
print(f"Rendered {png_512}")

# Generate 1024x500 Feature Graphic
feat_graphic = "app/src/main/res/drawable/img_feature_graphic.png"
render_feature_graphic(feat_graphic)
print(f"Rendered {feat_graphic}")

# Generate raster mipmap density PNGs
specs = [
    ("mipmap-mdpi", 48),
    ("mipmap-hdpi", 72),
    ("mipmap-xhdpi", 96),
    ("mipmap-xxhdpi", 144),
    ("mipmap-xxxhdpi", 192),
]

base_res = "app/src/main/res"
for folder, size in specs:
    dir_path = os.path.join(base_res, folder)
    os.makedirs(dir_path, exist_ok=True)
    for old in ["ic_launcher.webp", "ic_launcher_round.webp"]:
        old_f = os.path.join(dir_path, old)
        if os.path.exists(old_f):
            os.remove(old_f)

    sq_path = os.path.join(dir_path, "ic_launcher.png")
    rd_path = os.path.join(dir_path, "ic_launcher_round.png")

    render_luxury_icon(sq_path, size=size, is_round=False)
    render_luxury_icon(rd_path, size=size, is_round=True)
    print(f"Rendered {folder}: {size}x{size} square and round icons")

print("All luxury brand assets successfully rendered!")
