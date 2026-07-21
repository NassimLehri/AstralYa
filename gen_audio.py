"""
Générateur audio pour Les Gardiens d'Astralya
Produit tous les fichiers .wav (effets sonores) et simule les musiques
en synthèse pure (wave + math), sans dépendances externes.
"""
import wave, struct, math, os, array

OUT = "/home/claude/assets_output/audio"
os.makedirs(OUT, exist_ok=True)

RATE = 22050   # 22 kHz — suffisant pour un RPG mobile

# ── Utilitaires ───────────────────────────────────────────────

def save_wav(filename, samples, rate=RATE):
    path = os.path.join(OUT, filename)
    with wave.open(path, 'w') as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(rate)
        data = struct.pack('<' + 'h'*len(samples), *samples)
        f.writeframes(data)

def clamp(v):
    return max(-32767, min(32767, int(v)))

def silence(duration):
    return [0] * int(RATE * duration)

def sine(freq, duration, volume=0.5, rate=RATE):
    n = int(rate * duration)
    return [clamp(volume * 32767 * math.sin(2*math.pi*freq*i/rate)) for i in range(n)]

def square(freq, duration, volume=0.3, rate=RATE):
    n = int(rate * duration)
    return [clamp(volume * 32767 * (1 if math.sin(2*math.pi*freq*i/rate)>0 else -1)) for i in range(n)]

def triangle(freq, duration, volume=0.4, rate=RATE):
    n = int(rate * duration)
    period = rate / freq
    return [clamp(volume * 32767 * (2*abs(2*(i/period - math.floor(i/period + 0.5)))-1)) for i in range(n)]

def envelope(samples, attack=0.01, decay=0.05, sustain=0.7, release=0.1):
    n = len(samples)
    a = int(RATE * attack)
    d = int(RATE * decay)
    r = int(RATE * release)
    result = []
    for i, s in enumerate(samples):
        if i < a:
            gain = i / a
        elif i < a + d:
            gain = 1.0 - (1.0-sustain)*(i-a)/d
        elif i < n - r:
            gain = sustain
        else:
            gain = sustain * (n-i) / r
        result.append(clamp(s * gain))
    return result

def mix(*tracks):
    maxlen = max(len(t) for t in tracks)
    result = []
    for i in range(maxlen):
        v = sum(t[i] if i < len(t) else 0 for t in tracks)
        result.append(clamp(v / len(tracks)))
    return result

def concat(*parts):
    result = []
    for p in parts:
        result.extend(p)
    return result

# ── Notes (fréquences) ────────────────────────────────────────
NOTE = {
    'C3':130,'D3':147,'E3':165,'F3':175,'G3':196,'A3':220,'B3':247,
    'C4':262,'D4':294,'E4':330,'F4':349,'G4':392,'A4':440,'B4':494,
    'C5':523,'D5':587,'E5':659,'F5':698,'G5':784,'A5':880,'B5':988,
    'C6':1047,'R':0
}

def note(name, dur, vol=0.4, wave_fn=sine):
    freq = NOTE.get(name, 0)
    if freq == 0:
        return silence(dur)
    s = wave_fn(freq, dur, vol)
    return envelope(s, attack=0.01, decay=0.03, sustain=0.6, release=0.08)

# ═══════════════════════════════════════════════════════════════
# EFFETS SONORES
# ═══════════════════════════════════════════════════════════════

print("🔊 Génération des effets sonores...")

# sfx_attack — coup d'épée (sweep descendant)
atk = []
for i in range(int(RATE*0.25)):
    t = i/RATE
    freq = 400 - 300*t
    v = math.sin(2*math.pi*freq*t) * 0.6 * (1 - t*3)
    noise = ((__import__('random').random()-0.5)*0.3) * (1-t*3)
    atk.append(clamp((v+noise)*32767))
save_wav("sfx_attack.wav", envelope(atk, attack=0.002, decay=0.05, sustain=0.3, release=0.15))
print("  ✅ sfx_attack.wav")

# sfx_magic — son magique (montée harmonique)
mag = []
for i in range(int(RATE*0.5)):
    t = i/RATE
    s = (math.sin(2*math.pi*440*t) * 0.3 +
         math.sin(2*math.pi*660*t) * 0.2 +
         math.sin(2*math.pi*880*t*1.5) * 0.15 *t*2)
    mag.append(clamp(s * 32767 * min(1, t*5) * max(0,(0.5-t)*2)))
save_wav("sfx_magic.wav", mag)
print("  ✅ sfx_magic.wav")

# sfx_heal — son de soin (arpège montant doux)
heal = concat(
    note('C5', 0.08, 0.4, triangle),
    note('E5', 0.08, 0.4, triangle),
    note('G5', 0.08, 0.4, triangle),
    note('C6', 0.20, 0.5, triangle),
)
save_wav("sfx_heal.wav", heal)
print("  ✅ sfx_heal.wav")

# sfx_hit — impact (bruit court percussif)
hit = []
for i in range(int(RATE*0.15)):
    t = i/RATE
    noise = (__import__('random').random()-0.5) * 0.8 * (1-t*6)
    thud = math.sin(2*math.pi*80*t) * 0.5 * (1-t*6)
    hit.append(clamp((noise+thud)*32767))
save_wav("sfx_hit.wav", hit)
print("  ✅ sfx_hit.wav")

# sfx_critical — coup critique (flash sonore)
crit = []
for i in range(int(RATE*0.4)):
    t = i/RATE
    s = (math.sin(2*math.pi*800*t)*0.4 +
         math.sin(2*math.pi*1200*t)*0.3 +
         math.sin(2*math.pi*400*t)*0.2)
    s *= max(0, 1-t*2.5)
    crit.append(clamp(s*32767))
save_wav("sfx_critical.wav", crit)
print("  ✅ sfx_critical.wav")

# sfx_levelup — fanfare montante
lvl = concat(
    note('C4',0.12,0.5,triangle), note('E4',0.12,0.5,triangle),
    note('G4',0.12,0.5,triangle), note('C5',0.12,0.5,triangle),
    note('E5',0.12,0.5,triangle), note('G5',0.12,0.5,triangle),
    note('C6',0.30,0.6,triangle),
    silence(0.05),
    note('C6',0.15,0.5,triangle),
)
save_wav("sfx_levelup.wav", lvl)
print("  ✅ sfx_levelup.wav")

# sfx_chest — coffre (claquement + tintement)
chest = concat(
    envelope(sine(200,0.08,0.6), attack=0.002,decay=0.04,sustain=0.2,release=0.03),
    silence(0.03),
    note('G5',0.12,0.4,triangle),
    note('C6',0.20,0.45,triangle),
)
save_wav("sfx_chest.wav", chest)
print("  ✅ sfx_chest.wav")

# sfx_menu_select — bip menu
sel = envelope(sine(660,0.08,0.35), attack=0.005,decay=0.02,sustain=0.5,release=0.04)
save_wav("sfx_menu_select.wav", sel)
print("  ✅ sfx_menu_select.wav")

# sfx_menu_cancel — bip grave annulation
can = envelope(sine(330,0.1,0.35), attack=0.005,decay=0.03,sustain=0.4,release=0.05)
save_wav("sfx_menu_cancel.wav", can)
print("  ✅ sfx_menu_cancel.wav")

# sfx_portal — téléportation (sweep montant)
portal = []
for i in range(int(RATE*0.8)):
    t = i/RATE
    freq = 220 + 880*t
    env = math.sin(math.pi*t/0.8)
    v = math.sin(2*math.pi*freq*t)*0.4*env
    portal.append(clamp(v*32767))
save_wav("sfx_portal.wav", portal)
print("  ✅ sfx_portal.wav")

# sfx_boss_appear — apparition boss (grondement + stinger)
boss_sfx = []
for i in range(int(RATE*1.2)):
    t = i/RATE
    rumble = (__import__('random').random()-0.5)*0.3*min(1,t*2)
    low = math.sin(2*math.pi*60*t)*0.4*min(1,t*2)*max(0,1-t*0.5)
    stinger = 0
    if t > 0.8:
        stinger = math.sin(2*math.pi*880*(t-0.8))*0.5*max(0,1-(t-0.8)*2.5)
    boss_sfx.append(clamp((rumble+low+stinger)*32767))
save_wav("sfx_boss_appear.wav", boss_sfx)
print("  ✅ sfx_boss_appear.wav")

# ═══════════════════════════════════════════════════════════════
# MUSIQUES (courtes boucles synthétisées, ~8-16 secondes)
# Sauvegardées en .wav (renommer en .ogg après conversion si besoin)
# ═══════════════════════════════════════════════════════════════

print("\n🎵 Génération des musiques...")

def make_music(melody_notes, bass_notes=None, tempo=0.25, vol=0.35, repeats=2):
    """Construit une piste musicale simple avec mélodie + basse"""
    melody = concat(*[note(n, tempo, vol, triangle) for n in melody_notes])
    if bass_notes:
        bass = concat(*[note(n, tempo*2, vol*0.6, sine) for n in bass_notes])
        # Étirer la basse pour correspondre
        while len(bass) < len(melody):
            bass = bass + bass
        bass = bass[:len(melody)]
        track = mix(melody, bass)
    else:
        track = melody
    # Répétitions
    result = track * repeats
    return result

# music_village — Do majeur, tempo tranquille
village_mel = ['E5','D5','C5','D5','E5','E5','E5','R',
               'D5','D5','D5','R','E5','G5','G5','R',
               'E5','D5','C5','D5','E5','E5','E5','E5',
               'D5','D5','E5','D5','C5','R','R','R']
village_bass = ['C3','C3','G3','G3','A3','A3','E3','E3',
                'F3','F3','C3','C3','G3','G3','C3','C3']
save_wav("music_village.wav", make_music(village_mel, village_bass, 0.22, repeats=3))
print("  ✅ music_village.wav")

# music_foret — La mineur, mystérieux
foret_mel = ['A4','C5','E5','A5','G5','E5','F5','E5',
             'D5','F5','A5','G5','E5','D5','C5','R',
             'A4','B4','C5','E5','D5','C5','B4','A4']
foret_bass = ['A3','A3','E3','E3','F3','F3','C4','C4',
              'D3','D3','A3','A3','E3','E3','A3','A3']
save_wav("music_foret.wav", make_music(foret_mel, foret_bass, 0.25, repeats=3))
print("  ✅ music_foret.wav")

# music_grotte — Mi mineur, sombre
grotte_mel = ['E4','F4','G4','A4','B4','A4','G4','F4',
              'E4','G4','B4','G4','E4','D4','C4','B3',
              'A3','C4','E4','G4','F4','E4','D4','E4']
grotte_bass = ['E3','E3','B3','B3','C4','C4','G3','G3',
               'A3','A3','E3','E3','B3','B3','E3','E3']
save_wav("music_grotte.wav", make_music(grotte_mel, grotte_bass, 0.28, 0.3, repeats=2))
print("  ✅ music_grotte.wav")

# music_desert — Sol majeur, épique chaleureux
desert_mel = ['G4','A4','B4','C5','D5','C5','B4','A4',
              'G4','B4','D5','B4','G4','F4','E4','D4',
              'G4','A4','B4','D5','E5','D5','C5','B4']
desert_bass = ['G3','G3','D4','D4','E3','E3','C4','C4',
               'G3','G3','B3','B3','C4','C4','G3','G3']
save_wav("music_desert.wav", make_music(desert_mel, desert_bass, 0.20, 0.4, repeats=3))
print("  ✅ music_desert.wav")

# music_temple — Do majeur, grandiose
temple_mel = ['C5','E5','G5','C6','B5','A5','G5','E5',
              'F5','A5','C6','A5','F5','E5','D5','C5',
              'G4','B4','D5','G5','F5','E5','D5','G4']
temple_bass = ['C3','C3','G3','G3','F3','F3','E3','E3',
               'F3','F3','C4','C4','G3','G3','C3','C3']
save_wav("music_temple.wav", make_music(temple_mel, temple_bass, 0.22, 0.4, repeats=3))
print("  ✅ music_temple.wav")

# music_cite — Ré majeur, aérien
cite_mel = ['D5','F5','A5','D6','C6','B5','A5','F5',
            'D5','E5','F5','A5','G5','F5','E5','D5',
            'A4','D5','F5','A5','G5','F5','E5','D5']
cite_bass = ['D3','D3','A3','A3','B3','B3','F3','F3',
             'G3','G3','D4','D4','A3','A3','D3','D3']
save_wav("music_cite.wav", make_music(cite_mel, cite_bass, 0.20, 0.38, repeats=3))
print("  ✅ music_cite.wav")

# music_battle — Rapide, énergique (La mineur)
battle_mel = ['A4','A4','C5','A4','E5','D5','C5','A4',
              'G4','G4','B4','G4','D5','C5','B4','G4',
              'F4','F4','A4','F4','C5','B4','A4','F4',
              'E4','G4','B4','E5','D5','C5','B4','E4']
battle_bass = ['A3','E3','A3','E3','F3','C3','G3','D3',
               'A3','E3','A3','E3','G3','D3','E3','A3']
save_wav("music_battle.wav", make_music(battle_mel, battle_bass, 0.14, 0.4, repeats=4))
print("  ✅ music_battle.wav")

# music_boss — Intense, dissonant (Mi mineur dramatique)
boss_mel = ['E4','F4','E4','G4','E4','F4','G4','A4',
            'B4','C5','B4','A4','G4','F4','E4','D4',
            'E4','E4','G4','E4','B4','A4','G4','E4',
            'F4','G4','A4','B4','C5','B4','A4','G4']
boss_bass = ['E3','B3','E3','B3','C4','G3','D4','A3',
             'E3','B3','E3','B3','A3','E3','B3','E3']
save_wav("music_boss.wav", make_music(boss_mel, boss_bass, 0.16, 0.4, repeats=3))
print("  ✅ music_boss.wav")

# music_victory — Fanfare courte
vic = concat(
    note('C5',0.15,0.5,triangle), note('E5',0.15,0.5,triangle),
    note('G5',0.15,0.5,triangle), note('C6',0.30,0.6,triangle),
    silence(0.1),
    note('G5',0.12,0.5,triangle), note('A5',0.12,0.5,triangle),
    note('B5',0.12,0.5,triangle), note('C6',0.40,0.6,triangle),
    silence(0.1),
    note('E6',0.20,0.5,triangle), note('D6',0.12,0.4,triangle),
    note('C6',0.50,0.55,triangle),
)
save_wav("music_victory.wav", vic * 1)
print("  ✅ music_victory.wav")

# music_gameover — Mélancolique
go = concat(
    note('E4',0.4,0.3,sine), note('D4',0.4,0.3,sine),
    note('C4',0.4,0.3,sine), note('B3',0.8,0.3,sine),
    silence(0.2),
    note('A3',0.5,0.25,sine), note('G3',0.5,0.25,sine),
    note('F3',0.5,0.25,sine), note('E3',1.2,0.2,sine),
)
save_wav("music_gameover.wav", go)
print("  ✅ music_gameover.wav")

# Compte fichiers
files = os.listdir(OUT)
print(f"\n✅ Tous les fichiers audio générés dans {OUT}/")
print(f"   Total : {len(files)} fichiers")
print(f"\n⚠️  Note : les .wav peuvent être convertis en .ogg")
print(f"   avec ffmpeg : ffmpeg -i input.wav output.ogg")
