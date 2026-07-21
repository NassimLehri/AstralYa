"""
Script maître — copie tous les assets générés dans le projet AstralYa
ET convertit les .wav en .ogg si ffmpeg est disponible.
"""
import os, shutil, subprocess

SPRITES_SRC = "/home/claude/assets_output/sprites"
AUDIO_SRC   = "/home/claude/assets_output/audio"
PROJECT_ASSETS = "/home/claude/AstralYa/android/src/main/assets"

def copy_sprites():
    dst = os.path.join(PROJECT_ASSETS, "sprites")
    os.makedirs(dst, exist_ok=True)
    count = 0
    for f in os.listdir(SPRITES_SRC):
        shutil.copy2(os.path.join(SPRITES_SRC, f), os.path.join(dst, f))
        count += 1
    print(f"  ✅ {count} sprites copiés → assets/sprites/")

def copy_audio():
    dst = os.path.join(PROJECT_ASSETS, "audio")
    os.makedirs(dst, exist_ok=True)
    count_wav = 0
    count_ogg = 0

    # Vérifier ffmpeg
    has_ffmpeg = shutil.which("ffmpeg") is not None

    for f in os.listdir(AUDIO_SRC):
        src_path = os.path.join(AUDIO_SRC, f)
        if f.endswith(".wav"):
            if has_ffmpeg:
                ogg_name = f.replace(".wav", ".ogg")
                ogg_path = os.path.join(dst, ogg_name)
                result = subprocess.run(
                    ["ffmpeg", "-y", "-i", src_path, "-c:a", "libvorbis", "-q:a", "4", ogg_path],
                    capture_output=True
                )
                if result.returncode == 0:
                    count_ogg += 1
                else:
                    # Fallback : copier en .wav
                    shutil.copy2(src_path, os.path.join(dst, f))
                    count_wav += 1
            else:
                # Pas de ffmpeg — copier en .wav
                # LibGDX supporte les .wav nativement
                shutil.copy2(src_path, os.path.join(dst, f))
                count_wav += 1

    if has_ffmpeg:
        print(f"  ✅ {count_ogg} fichiers convertis en .ogg → assets/audio/")
        if count_wav > 0:
            print(f"  ⚠️  {count_wav} fichiers copiés en .wav (conversion échouée)")
    else:
        print(f"  ✅ {count_wav} fichiers .wav copiés → assets/audio/")
        print(f"  ℹ️  ffmpeg non trouvé — fichiers en .wav (fonctionnel)")
        print(f"     Pour convertir en .ogg : installez ffmpeg puis relancez ce script")

copy_sprites()
copy_audio()
print("\n✅ Tous les assets installés dans le projet !")
