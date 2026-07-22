import os

def fix_bom(filepath):
    with open(filepath, 'rb') as f:
        content = f.read()

    if content.startswith(b'\xef\xbb\xbf'):
        print(f"Fixing BOM in {filepath}")
        with open(filepath, 'wb') as f:
            f.write(content[3:])
        return True
    return False

map_dir = "android/src/main/assets/maps"
fixed_count = 0
for filename in os.listdir(map_dir):
    if filename.endswith(".tmx"):
        if fix_bom(os.path.join(map_dir, filename)):
            fixed_count += 1

print(f"Finished. Fixed {fixed_count} files.")
