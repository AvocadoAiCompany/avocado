#!/usr/bin/env bash
# Generates docs/favicon.ico — run once, then commit the result.
# Produces a 1×1 transparent 32-bpp ICO (70 bytes).
set -euo pipefail

OUT="$(dirname "$0")/../docs/favicon.ico"
mkdir -p "$(dirname "$OUT")"

python3 - "$OUT" <<'EOF'
import struct, sys

path = sys.argv[1]

# ICONDIR  (6 bytes)
icondir = struct.pack('<HHH', 0, 1, 1)

# BITMAPINFOHEADER  (40 bytes)
# biHeight is doubled in ICO format (XOR + AND masks)
bih = struct.pack('<IiiHHIIiiII', 40, 1, 2, 1, 32, 0, 0, 0, 0, 0, 0)

# XOR mask: 1 pixel, BGRA, fully transparent
xor_mask = b'\x00\x00\x00\x00'

# AND mask: 1 row, 1 bit/pixel, padded to DWORD boundary.
# Bit 7 = pixel 0; value 1 = transparent.
and_mask = b'\x80\x00\x00\x00'

image_data = bih + xor_mask + and_mask       # 48 bytes
image_offset = 6 + 16                         # 22

# ICONDIRENTRY  (16 bytes)
entry = struct.pack('<BBBBHHII', 1, 1, 0, 0, 1, 32, len(image_data), image_offset)

with open(path, 'wb') as f:
    f.write(icondir + entry + image_data)

print(f'wrote {path} ({len(icondir)+len(entry)+len(image_data)} bytes)')
EOF
