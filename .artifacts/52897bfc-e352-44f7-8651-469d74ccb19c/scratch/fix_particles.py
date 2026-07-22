import os
import re

def fix_particle_file(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()

    new_lines = []
    for line in lines:
        line = line.strip()
        if not line:
            new_lines.append("")
            continue

        match = re.match(r'^(\w+)Values:\s*(.*)$', line)
        if match:
            label_base = match.group(1)
            values = [v.strip() for v in match.group(2).split(',')]
            for i, val in enumerate(values):
                new_lines.append(f"{label_base}{i}: {val}")
        else:
            new_lines.append(line)

    with open(filepath, 'w') as f:
        f.write("\n".join(new_lines) + "\n")

particle_dir = "android/src/main/assets/particles"
for filename in os.listdir(particle_dir):
    if filename.endswith(".p"):
        print(f"Fixing {filename}...")
        fix_particle_file(os.path.join(particle_dir, filename))
print("Done.")
