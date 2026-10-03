#!/usr/bin/env python3
import subprocess
import os

def render_icon(output_path, size=512, is_round=False):
    # Scale from 512 base
    s = size / 512.0
    
    # Coordinates scaled
    def sc(val):
        return int(round(val * s))

    # Base background
    cmd = [
        "convert",
        "-size", f"{size}x{size}",
        "xc:none"
    ]

    # Draw rounded or circular background
    if is_round:
        rad = size // 2
        cmd.extend([
            "-fill", "#0A2E23",
            "-draw", f"circle {rad},{rad} {rad},0"
        ])
    else:
        # Rounded rectangle squircle
        r = sc(96)
        cmd.extend([
            "-fill", "#0A2E23",
            "-draw", f"roundrectangle 0,0 {size-1},{size-1} {r},{r}"
        ])

    # Subtle radial highlight in center
    cmd.extend([
        "-fill", "#144837",
        "-draw", f"circle {sc(256)},{sc(235)} {sc(256)},{sc(110)}"
    ])

    # Background Geometric 8-pointed star (subtle gold / emerald)
    # Square 1
    cmd.extend([
        "-stroke", "#386E5B",
        "-strokewidth", str(max(1, sc(2.5))),
        "-fill", "none",
        "-draw", f"roundrectangle {sc(175)},{sc(155)} {sc(337)},{sc(317)} {sc(6)},{sc(6)}"
    ])
    # Rotated Star Square 2
    cmd.extend([
        "-stroke", "#386E5B",
        "-strokewidth", str(max(1, sc(2.5))),
        "-fill", "none",
        "-draw", f"polygon {sc(256)},{sc(96)} {sc(370)},{sc(210)} {sc(256)},{sc(324)} {sc(142)},{sc(210)}"
    ])

    # Inner Gold Star Accent
    cmd.extend([
        "-stroke", "#7FA687",
        "-strokewidth", str(max(1, sc(1.5))),
        "-fill", "none",
        "-draw", f"polygon {sc(256)},{sc(145)} {sc(321)},{sc(210)} {sc(256)},{sc(275)} {sc(191)},{sc(210)}"
    ])

    # Wooden Quran Stand (Rehal)
    # Rehal shadow
    cmd.extend([
        "-stroke", "none",
        "-fill", "#03120C",
        "-draw", f"ellipse {sc(256)},{sc(394)} {sc(100)},{sc(10)} 0,360"
    ])
    # Left leg
    cmd.extend([
        "-fill", "#5C351B",
        "-draw", f"polygon {sc(162)},{sc(382)} {sc(198)},{sc(382)} {sc(264)},{sc(312)} {sc(246)},{sc(302)}"
    ])
    # Right leg
    cmd.extend([
        "-fill", "#704122",
        "-draw", f"polygon {sc(350)},{sc(382)} {sc(314)},{sc(382)} {sc(248)},{sc(312)} {sc(266)},{sc(302)}"
    ])
    # Rehal feet base
    cmd.extend([
        "-fill", "#381E0D",
        "-draw", f"roundrectangle {sc(156)},{sc(382)} {sc(198)},{sc(388)} {sc(2)},{sc(2)}",
        "-draw", f"roundrectangle {sc(314)},{sc(382)} {sc(356)},{sc(388)} {sc(2)},{sc(2)}"
    ])
    # Carved Arch in center
    cmd.extend([
        "-fill", "#1E0E05",
        "-draw", f"polygon {sc(228)},{sc(362)} {sc(256)},{sc(345)} {sc(284)},{sc(362)} {sc(292)},{sc(382)} {sc(220)},{sc(382)}"
    ])
    # Gold Pivot Rivet
    cmd.extend([
        "-fill", "#D4AF37",
        "-draw", f"roundrectangle {sc(250)},{sc(332)} {sc(262)},{sc(344)} {sc(2)},{sc(2)}"
    ])

    # Open Quran Book Binding & Cover Trim (Emerald & Gold)
    cmd.extend([
        "-fill", "#0D3E30",
        "-draw", f"polygon {sc(144)},{sc(302)} {sc(256)},{sc(326)} {sc(368)},{sc(302)} {sc(364)},{sc(312)} {sc(256)},{sc(334)} {sc(148)},{sc(312)}"
    ])
    cmd.extend([
        "-fill", "#D4AF37",
        "-draw", f"polygon {sc(145)},{sc(307)} {sc(256)},{sc(330)} {sc(367)},{sc(307)} {sc(364)},{sc(311)} {sc(256)},{sc(333)} {sc(148)},{sc(311)}"
    ])

    # Left Page Wings
    cmd.extend([
        "-fill", "#EAE1CF",
        "-draw", f"polygon {sc(148)},{sc(292)} {sc(256)},{sc(296)} {sc(256)},{sc(312)} {sc(148)},{sc(306)}"
    ])
    cmd.extend([
        "-fill", "#FFFDF7",
        "-stroke", "#E0D5BE",
        "-strokewidth", str(max(1, sc(1.5))),
        "-draw", f"bezier {sc(150)},{sc(288)} {sc(180)},{sc(246)} {sc(226)},{sc(248)} {sc(256)},{sc(296)} {sc(226)},{sc(266)} {sc(180)},{sc(266)} {sc(150)},{sc(288)}"
    ])

    # Right Page Wings
    cmd.extend([
        "-fill", "#E4D9C4",
        "-draw", f"polygon {sc(364)},{sc(292)} {sc(256)},{sc(296)} {sc(256)},{sc(312)} {sc(364)},{sc(306)}"
    ])
    cmd.extend([
        "-fill", "#FFFDF7",
        "-stroke", "#E0D5BE",
        "-strokewidth", str(max(1, sc(1.5))),
        "-draw", f"bezier {sc(362)},{sc(288)} {sc(332)},{sc(246)} {sc(286)},{sc(248)} {sc(256)},{sc(296)} {sc(286)},{sc(266)} {sc(332)},{sc(266)} {sc(362)},{sc(288)}"
    ])

    # Center Spine Line
    cmd.extend([
        "-stroke", "#C9BA9F",
        "-strokewidth", str(max(1, sc(2.5))),
        "-draw", f"line {sc(256)},{sc(265)} {sc(256)},{sc(312)}"
    ])

    # Golden Bookmark Ribbon
    cmd.extend([
        "-stroke", "none",
        "-fill", "#E5C058",
        "-draw", f"polygon {sc(253)},{sc(300)} {sc(259)},{sc(300)} {sc(260)},{sc(366)} {sc(254)},{sc(366)}"
    ])
    cmd.extend([
        "-fill", "#C89B2B",
        "-draw", f"polygon {sc(252)},{sc(366)} {sc(257)},{sc(375)} {sc(262)},{sc(366)}"
    ])

    # Left & Right Page Impression Lines
    cmd.extend([
        "-stroke", "#D8C7A0",
        "-strokewidth", str(max(1, sc(1.8))),
        "-draw", f"line {sc(172)},{sc(278)} {sc(236)},{sc(278)}",
        "-draw", f"line {sc(168)},{sc(286)} {sc(232)},{sc(286)}",
        "-draw", f"line {sc(276)},{sc(278)} {sc(340)},{sc(278)}",
        "-draw", f"line {sc(280)},{sc(286)} {sc(344)},{sc(286)}"
    ])

    # Floating Word-by-Word Cards
    # Left Card
    cmd.extend([
        "-stroke", "none",
        "-fill", "#04140D",
        "-draw", f"roundrectangle {sc(163)},{sc(149)} {sc(219)},{sc(195)} {sc(8)},{sc(8)}"
    ])
    cmd.extend([
        "-stroke", "#D4AF37",
        "-strokewidth", str(max(1, sc(2.2))),
        "-fill", "#FFFDF8",
        "-draw", f"roundrectangle {sc(161)},{sc(147)} {sc(217)},{sc(193)} {sc(8)},{sc(8)}"
    ])
    cmd.extend([
        "-stroke", "#0A2E23",
        "-strokewidth", str(max(2, sc(5.5))),
        "-draw", f"line {sc(173)},{sc(162)} {sc(205)},{sc(162)}"
    ])
    cmd.extend([
        "-stroke", "#8C6D23",
        "-strokewidth", str(max(1, sc(3.8))),
        "-draw", f"line {sc(178)},{sc(178)} {sc(200)},{sc(178)}"
    ])

    # Center Card (Elevated Gold Accent)
    cmd.extend([
        "-stroke", "none",
        "-fill", "#04140D",
        "-draw", f"roundrectangle {sc(227)},{sc(120)} {sc(287)},{sc(170)} {sc(9)},{sc(9)}"
    ])
    cmd.extend([
        "-stroke", "#C59E30",
        "-strokewidth", str(max(1, sc(2.6))),
        "-fill", "#F6E7B4",
        "-draw", f"roundrectangle {sc(225)},{sc(118)} {sc(285)},{sc(168)} {sc(9)},{sc(9)}"
    ])
    cmd.extend([
        "-stroke", "#062118",
        "-strokewidth", str(max(2, sc(6.5))),
        "-draw", f"line {sc(239)},{sc(135)} {sc(271)},{sc(135)}"
    ])
    cmd.extend([
        "-stroke", "#6B4F12",
        "-strokewidth", str(max(1, sc(4.2))),
        "-draw", f"line {sc(245)},{sc(152)} {sc(265)},{sc(152)}"
    ])

    # Right Card
    cmd.extend([
        "-stroke", "none",
        "-fill", "#04140D",
        "-draw", f"roundrectangle {sc(296)},{sc(149)} {sc(352)},{sc(195)} {sc(8)},{sc(8)}"
    ])
    cmd.extend([
        "-stroke", "#D4AF37",
        "-strokewidth", str(max(1, sc(2.2))),
        "-fill", "#FFFDF8",
        "-draw", f"roundrectangle {sc(294)},{sc(147)} {sc(350)},{sc(193)} {sc(8)},{sc(8)}"
    ])
    cmd.extend([
        "-stroke", "#0A2E23",
        "-strokewidth", str(max(2, sc(5.5))),
        "-draw", f"line {sc(306)},{sc(162)} {sc(338)},{sc(162)}"
    ])
    cmd.extend([
        "-stroke", "#8C6D23",
        "-strokewidth", str(max(1, sc(3.8))),
        "-draw", f"line {sc(311)},{sc(178)} {sc(333)},{sc(178)}"
    ])

    cmd.append(f"PNG32:{output_path}")
    subprocess.run(cmd, check=True)

def render_feature_graphic(output_path):
    # 1024x500 Google Play Store Feature Graphic
    cmd = [
        "convert",
        "-size", "1024x500",
        "xc:#0A2E23"
    ]
    # Subtle diagonal gradient
    cmd.extend([
        "-fill", "#104233",
        "-draw", "circle 512,250 512,50"
    ])
    # Background Star Motifs Left & Right
    cmd.extend([
        "-stroke", "#1C5442",
        "-strokewidth", "2",
        "-fill", "none",
        "-draw", "polygon 180,80 240,140 180,200 120,140",
        "-draw", "polygon 844,80 904,140 844,200 784,140"
    ])
    # Add title typography
    cmd.extend([
        "-fill", "#FFFFFF",
        "-pointsize", "44",
        "-font", "DejaVu-Sans-Bold",
        "-gravity", "North",
        "-annotate", "+0+70", "Quran Word by Word"
    ])
    cmd.extend([
        "-fill", "#D4AF37",
        "-pointsize", "22",
        "-font", "DejaVu-Sans",
        "-gravity", "North",
        "-annotate", "+0+130", "READ  •  UNDERSTAND  •  REFLECT"
    ])
    cmd.extend([
        "-fill", "#C0D9CD",
        "-pointsize", "18",
        "-font", "DejaVu-Sans",
        "-gravity", "North",
        "-annotate", "+0+175", "Authentic Uthmani Script  |  Word-by-Word Study  |  100% Offline"
    ])
    # Gold decorative accent line
    cmd.extend([
        "-stroke", "#D4AF37",
        "-strokewidth", "2",
        "-draw", "line 412,215 612,215"
    ])
    # Floating feature pills at bottom
    cmd.extend([
        "-stroke", "#D4AF37",
        "-strokewidth", "1",
        "-fill", "#0D3528",
        "-draw", "roundrectangle 120,380 320,430 16,16",
        "-draw", "roundrectangle 360,380 660,430 16,16",
        "-draw", "roundrectangle 700,380 904,430 16,16"
    ])
    cmd.extend([
        "-fill", "#FFFFFF",
        "-pointsize", "16",
        "-font", "DejaVu-Sans",
        "-gravity", "NorthWest",
        "-annotate", "+155+396", "Word-by-Word",
        "-annotate", "+405+396", "Continuous & Swipe Modes",
        "-annotate", "+755+396", "Auto-Scroll"
    ])
    cmd.append(f"PNG32:{output_path}")
    subprocess.run(cmd, check=True)

# Generate Play Store 512x512 icons
png_512 = "app/src/main/res/drawable/play_store_icon.png"
render_icon(png_512, 512, is_round=False)
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
    
    render_icon(sq_path, size=size, is_round=False)
    render_icon(rd_path, size=size, is_round=True)
    print(f"Rendered {folder}: {size}x{size} square and round icons")

print("All raster assets successfully rendered!")
