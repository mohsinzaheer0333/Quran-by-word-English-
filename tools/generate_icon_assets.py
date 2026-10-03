#!/usr/bin/env python3
import subprocess
import os

svg_content = """<?xml version="1.0" encoding="UTF-8"?>
<svg width="512" height="512" viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <!-- Background Gradient: Deep Emerald Green -->
    <linearGradient id="bgGrad" x1="85" y1="56" x2="426" y2="455" gradientUnits="userSpaceOnUse">
      <stop offset="0%" stop-color="#0F3D30"/>
      <stop offset="50%" stop-color="#0A2E23"/>
      <stop offset="100%" stop-color="#051912"/>
    </linearGradient>

    <!-- Radial Glow behind Quran -->
    <radialGradient id="centerGlow" cx="256" cy="237" r="227" gradientUnits="userSpaceOnUse">
      <stop offset="0%" stop-color="#1F5444" stop-opacity="0.9"/>
      <stop offset="60%" stop-color="#0A2E23" stop-opacity="0"/>
    </radialGradient>

    <!-- Gold Accent Gradient -->
    <linearGradient id="goldGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#F7E6B5"/>
      <stop offset="50%" stop-color="#D4AF37"/>
      <stop offset="100%" stop-color="#A67C1E"/>
    </linearGradient>

    <!-- Wooden Stand Gradient -->
    <linearGradient id="woodGradLeft" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#6B3F21"/>
      <stop offset="100%" stop-color="#422411"/>
    </linearGradient>
    <linearGradient id="woodGradRight" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#7D4A27"/>
      <stop offset="100%" stop-color="#4C2814"/>
    </linearGradient>

    <!-- Page Soft Gradient -->
    <linearGradient id="pageGradLeft" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#FAF5EB"/>
      <stop offset="85%" stop-color="#FFFDF7"/>
      <stop offset="100%" stop-color="#E2D6C0"/>
    </linearGradient>
    <linearGradient id="pageGradRight" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#E2D6C0"/>
      <stop offset="15%" stop-color="#FFFDF7"/>
      <stop offset="100%" stop-color="#FAF5EB"/>
    </linearGradient>

    <!-- Subtle Drop Shadow -->
    <filter id="softShadow" x="-10%" y="-10%" width="120%" height="125%" filterUnits="userSpaceOnUse">
      <feDropShadow dx="0" dy="8" stdDeviation="12" flood-color="#020B08" flood-opacity="0.45"/>
    </filter>
    <filter id="cardShadow" x="-20%" y="-20%" width="140%" height="140%" filterUnits="userSpaceOnUse">
      <feDropShadow dx="0" dy="4" stdDeviation="6" flood-color="#020B08" flood-opacity="0.35"/>
    </filter>
  </defs>

  <!-- 1. Background Base -->
  <rect width="512" height="512" rx="112" fill="url(#bgGrad)"/>
  <rect width="512" height="512" rx="112" fill="url(#centerGlow)"/>

  <!-- 2. Background Subtle Watermark Star -->
  <path d="M256,104 L282,159 L341,142 L325,201 L380,227 L339,273 L370,327 L310,327 L284,384 L256,343 L227,384 L201,327 L142,327 L173,273 L132,227 L187,201 L171,142 L230,159 Z"
        stroke="#225948" stroke-width="3" fill="#0D3528" fill-opacity="0.4"/>

  <!-- Group for Book and Stand with Shadow -->
  <g filter="url(#softShadow)">
    <!-- 3. Geometric Eight-Pointed Islamic Star (Refined Gold Linework) -->
    <!-- Square 1 -->
    <rect x="175" y="156" width="162" height="162" stroke="#D4AF37" stroke-opacity="0.32" stroke-width="4.5" fill="none" rx="4"/>
    <!-- Square 2 (Rotated 45 degrees) -->
    <rect x="175" y="156" width="162" height="162" stroke="#D4AF37" stroke-opacity="0.32" stroke-width="4.5" fill="none" rx="4" transform="rotate(45 256 237)"/>

    <!-- Inner Rosette Star -->
    <polygon points="256,162 275,213 327,213 284,246 303,298 256,265 208,298 228,246 185,213 237,213"
             stroke="#D4AF37" stroke-opacity="0.22" stroke-width="2.5" fill="none"/>

    <!-- 4. Wooden Quran Stand (Rehal / X-Stand) -->
    <!-- Stand Shadow -->
    <ellipse cx="256" cy="396" rx="108" ry="12" fill="#020A07" fill-opacity="0.5"/>

    <!-- Left Leg -->
    <path d="M161,384 L199,384 C218,355 242,332 265,313 L246,303 C223,322 194,350 161,384 Z"
          fill="url(#woodGradLeft)"/>
    <path d="M161,384 L199,384 L194,389 L156,389 Z" fill="#2E170A"/>

    <!-- Right Leg -->
    <path d="M351,384 L313,384 C294,355 270,332 246,313 L265,303 C289,322 318,350 351,384 Z"
          fill="url(#woodGradRight)"/>
    <path d="M313,384 L351,384 L355,389 L318,389 Z" fill="#2E170A"/>

    <!-- Center Arch Cutout -->
    <path d="M227,365 C246,346 265,346 284,365 L294,384 L218,384 Z" fill="#1C0D05"/>

    <!-- Pivot Gold Rivet -->
    <rect x="249" y="334" width="14" height="14" rx="3" fill="url(#goldGrad)"/>

    <!-- 5. Open Quran Cover Trim (Emerald & Gold) -->
    <path d="M142,303 C142,294 180,265 256,318 C332,265 370,294 370,303 L365,313 C332,303 256,332 256,332 C256,332 180,303 147,313 Z"
          fill="#0D3B2E"/>
    <path d="M142,308 C180,298 256,327 256,327 C256,327 332,298 370,308 L365,313 C332,303 256,332 256,332 C256,332 180,303 147,313 Z"
          fill="url(#goldGrad)"/>

    <!-- 6. Left Page Wing -->
    <path d="M147,294 C180,256 227,251 256,298 L256,313 C227,265 180,270 147,308 Z" fill="#E8DFCE"/>
    <path d="M147,294 C175,251 223,246 256,298 C232,270 185,270 152,303 Z" fill="#FAF6ED"/>
    <path d="M152,289 C180,244 227,244 256,296 C232,267 185,267 152,289 Z" fill="url(#pageGradLeft)"/>

    <!-- 7. Right Page Wing -->
    <path d="M365,294 C332,256 284,251 256,298 L256,313 C284,265 332,270 365,308 Z" fill="#E0D6C4"/>
    <path d="M365,294 C336,251 289,246 256,298 C280,270 327,270 360,303 Z" fill="#FAF6ED"/>
    <path d="M360,289 C332,244 284,244 256,296 C280,267 327,267 360,289 Z" fill="url(#pageGradRight)"/>

    <!-- Page Center Spine Line -->
    <line x1="256" y1="265" x2="256" y2="313" stroke="#D1C0A5" stroke-width="3"/>

    <!-- Golden Bookmark Ribbon -->
    <path d="M253,303 C253,322 261,336 258,365 L265,365 C268,336 259,322 259,303 Z" fill="url(#goldGrad)"/>
    <polygon points="253,365 261,375 270,365" fill="#C89B2B"/>

    <!-- Subtle Impression Guidelines on Pages -->
    <path d="M171,275 C194,261 223,265 237,284" stroke="#D4AF37" stroke-opacity="0.3" stroke-width="3" stroke-linecap="round"/>
    <path d="M166,284 C190,270 218,275 232,294" stroke="#D4AF37" stroke-opacity="0.3" stroke-width="3" stroke-linecap="round"/>
    <path d="M275,284 C289,265 318,261 341,275" stroke="#D4AF37" stroke-opacity="0.3" stroke-width="3" stroke-linecap="round"/>
    <path d="M280,294 C294,275 322,270 346,284" stroke="#D4AF37" stroke-opacity="0.3" stroke-width="3" stroke-linecap="round"/>
  </g>

  <!-- 8. Floating Word-by-Word Cards (The Signature Feature) -->
  <!-- Left Card -->
  <g filter="url(#cardShadow)">
    <rect x="161" y="147" width="57" height="47" rx="10" fill="#FFFDF8" stroke="#D4AF37" stroke-width="3.5"/>
    <line x1="173" y1="163" x2="206" y2="163" stroke="#0A2E23" stroke-width="6.5" stroke-linecap="round"/>
    <line x1="180" y1="179" x2="199" y2="179" stroke="#8C6D23" stroke-width="4.5" stroke-linecap="round"/>
  </g>

  <!-- Center Card (Elevated, Golden Accent) -->
  <g filter="url(#cardShadow)">
    <rect x="225" y="118" width="62" height="52" rx="11" fill="#F6E7B4" stroke="#C59E30" stroke-width="4"/>
    <line x1="239" y1="136" x2="273" y2="136" stroke="#062118" stroke-width="7.5" stroke-linecap="round"/>
    <line x1="246" y1="153" x2="266" y2="153" stroke="#6B4F12" stroke-width="5" stroke-linecap="round"/>
  </g>

  <!-- Right Card -->
  <g filter="url(#cardShadow)">
    <rect x="294" y="147" width="57" height="47" rx="10" fill="#FFFDF8" stroke="#D4AF37" stroke-width="3.5"/>
    <line x1="306" y1="163" x2="339" y2="163" stroke="#0A2E23" stroke-width="6.5" stroke-linecap="round"/>
    <line x1="313" y1="179" x2="332" y2="179" stroke="#8C6D23" stroke-width="4.5" stroke-linecap="round"/>
  </g>
</svg>
"""

svg_path = "app/src/main/res/drawable/play_store_icon.svg"
png_path = "app/src/main/res/drawable/play_store_icon.png"
store_path = "app/src/main/res/drawable/ic_launcher_store.png"

with open(svg_path, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"Saved {svg_path}")

# Convert to 512x512 PNG using ImageMagick
subprocess.run(["convert", "-background", "none", svg_path, "-resize", "512x512!", f"PNG32:{png_path}"], check=True)
subprocess.run(["cp", png_path, store_path], check=True)
print(f"Generated 512x512 PNG: {png_path} and {store_path}")

# Generate raster mipmaps for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
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
    
    # Remove stale webp if present
    for old in ["ic_launcher.webp", "ic_launcher_round.webp"]:
        old_f = os.path.join(dir_path, old)
        if os.path.exists(old_f):
            os.remove(old_f)

    # Standard square icon
    target_png = os.path.join(dir_path, "ic_launcher.png")
    subprocess.run(["convert", png_path, "-resize", f"{size}x{size}!", f"PNG32:{target_png}"], check=True)

    # Round icon (masked with circle)
    target_round = os.path.join(dir_path, "ic_launcher_round.png")
    radius = size // 2
    subprocess.run([
        "convert", png_path, "-resize", f"{size}x{size}!",
        "(", "-size", f"{size}x{size}", "xc:none", "-fill", "white",
        "-draw", f"circle {radius},{radius} {radius},0", ")",
        "-alpha", "set", "-compose", "DstIn", "-composite",
        f"PNG32:{target_round}"
    ], check=True)
    print(f"Generated {folder}: {size}x{size} square and round icons")

print("All icons successfully generated!")
