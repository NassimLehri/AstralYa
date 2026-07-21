"""
Générateur de sprites pixel art pour Les Gardiens d'Astralya
Produit tous les PNG nécessaires au projet LibGDX
"""
from PIL import Image, ImageDraw
import os

OUT = "/home/claude/assets_output/sprites"
os.makedirs(OUT, exist_ok=True)

# ── Palette de couleurs ────────────────────────────────────────
TRANSPARENT = (0, 0, 0, 0)
BLACK  = (10, 10, 20, 255)
WHITE  = (240, 240, 255, 255)

# Héros
NASSIM_BODY  = (40, 100, 200, 255)
NASSIM_HAIR  = (30, 30, 80, 255)
NASSIM_SWORD = (200, 210, 255, 255)
NASSIM_ARMOR = (60, 140, 220, 255)

YASMINE_BODY = (220, 170, 80, 255)
YASMINE_ROBE = (220, 200, 100, 255)
YASMINE_HAIR = (180, 130, 40, 255)
YASMINE_STAFF= (200, 240, 200, 255)
YASMINE_GLOW = (255, 255, 180, 200)

LWIZ_BODY    = (140, 60, 200, 255)
LWIZ_ROBE    = (100, 40, 180, 255)
LWIZ_HAIR    = (220, 180, 255, 255)
LWIZ_ORB     = (180, 120, 255, 255)
LWIZ_GLOW    = (200, 150, 255, 180)

SKIN         = (230, 190, 150, 255)
SKIN_DARK    = (200, 160, 120, 255)

def new_img(w, h):
    return Image.new("RGBA", (w, h), TRANSPARENT)

def pixel(img, x, y, color):
    if 0 <= x < img.width and 0 <= y < img.height:
        img.putpixel((x, y), color)

def rect(img, x, y, w, h, color):
    for px in range(x, x+w):
        for py in range(y, y+h):
            pixel(img, px, py, color)

def outline(img, x, y, w, h, color):
    for px in range(x, x+w):
        pixel(img, px, y, color)
        pixel(img, px, y+h-1, color)
    for py in range(y, y+h):
        pixel(img, x, py, color)
        pixel(img, x+w-1, py, color)

# ══════════════════════════════════════════════════════════════
# NASSIM — Chevalier Stellaire (32x48 px)
# ══════════════════════════════════════════════════════════════
def draw_nassim():
    img = new_img(32, 48)
    # Corps / armure
    rect(img, 8, 20, 16, 20, NASSIM_ARMOR)
    # Tête
    rect(img, 10, 6, 12, 12, SKIN)
    # Cheveux
    rect(img, 9, 4, 14, 5, NASSIM_HAIR)
    rect(img, 9, 6, 3, 8, NASSIM_HAIR)
    # Visage — yeux
    rect(img, 12, 10, 2, 2, BLACK)
    rect(img, 17, 10, 2, 2, BLACK)
    # Contour tête
    outline(img, 10, 6, 12, 12, BLACK)
    # Épaulières
    rect(img, 4, 20, 6, 5, NASSIM_SWORD)
    rect(img, 22, 20, 6, 5, NASSIM_SWORD)
    # Jambes
    rect(img, 8, 40, 6, 8, (50, 80, 160, 255))
    rect(img, 18, 40, 6, 8, (50, 80, 160, 255))
    # Bras gauche (épée)
    rect(img, 2, 22, 5, 14, NASSIM_ARMOR)
    # Épée
    rect(img, 0, 14, 3, 22, NASSIM_SWORD)
    rect(img, -1, 24, 5, 3, (150, 150, 100, 255))  # garde
    outline(img, 0, 14, 3, 22, BLACK)
    # Bras droit
    rect(img, 25, 22, 5, 12, NASSIM_ARMOR)
    # Bouclier simple
    rect(img, 26, 24, 7, 10, (80, 120, 200, 255))
    outline(img, 26, 24, 7, 10, BLACK)
    pixel(img, 29, 29, (200, 220, 255, 255))  # étoile bouclier
    # Contour corps
    outline(img, 8, 20, 16, 20, BLACK)
    return img

# ══════════════════════════════════════════════════════════════
# YASMINE — Prêtresse de Lumière (32x48 px)
# ══════════════════════════════════════════════════════════════
def draw_yasmine():
    img = new_img(32, 48)
    # Robe
    rect(img, 7, 20, 18, 24, YASMINE_ROBE)
    # Robe bas (élargie)
    rect(img, 5, 34, 22, 10, YASMINE_ROBE)
    # Tête
    rect(img, 10, 5, 12, 13, SKIN)
    # Cheveux longs
    rect(img, 8, 3, 16, 6, YASMINE_HAIR)
    rect(img, 7, 6, 4, 18, YASMINE_HAIR)
    rect(img, 21, 6, 4, 18, YASMINE_HAIR)
    # Yeux
    rect(img, 12, 10, 2, 2, (80, 50, 120, 255))
    rect(img, 17, 10, 2, 2, (80, 50, 120, 255))
    # Sourire
    pixel(img, 14, 14, BLACK)
    pixel(img, 15, 15, BLACK)
    pixel(img, 16, 15, BLACK)
    pixel(img, 17, 14, BLACK)
    # Contours
    outline(img, 10, 5, 12, 13, BLACK)
    outline(img, 7, 20, 18, 24, BLACK)
    # Bras + bâton
    rect(img, 2, 22, 4, 14, SKIN)
    rect(img, 26, 22, 4, 12, SKIN)
    # Bâton de cristal
    rect(img, 0, 8, 3, 36, (160, 200, 160, 255))
    rect(img, -1, 6, 5, 6, (180, 255, 180, 255))   # cristal
    outline(img, 0, 8, 3, 36, BLACK)
    # Halo lumineux (pixels semi-transparents)
    for ox, oy in [(-2,2),(-2,-2),(2,2),(2,-2),(-3,0),(3,0),(0,-3)]:
        pixel(img, 16+ox, 8+oy, YASMINE_GLOW)
    # Ceinture
    rect(img, 7, 32, 18, 3, (180, 150, 60, 255))
    return img

# ══════════════════════════════════════════════════════════════
# LWIZ — Enfant des Étoiles (32x48 px)
# ══════════════════════════════════════════════════════════════
def draw_lwiz():
    img = new_img(32, 48)
    # Robe cosmique
    rect(img, 8, 20, 16, 24, LWIZ_ROBE)
    rect(img, 6, 30, 20, 14, LWIZ_ROBE)
    # Motifs étoiles sur robe
    for sx, sy in [(10,25),(20,28),(14,35),(18,38)]:
        pixel(img, sx, sy, (200, 180, 255, 255))
    # Tête (plus petite, enfantin)
    rect(img, 11, 7, 10, 11, SKIN)
    # Cheveux bouclés violets
    rect(img, 9, 4, 14, 7, LWIZ_HAIR)
    rect(img, 8, 6, 3, 6, LWIZ_HAIR)
    rect(img, 21, 6, 3, 6, LWIZ_HAIR)
    # Grands yeux cosmiques
    rect(img, 12, 11, 3, 3, (100, 60, 180, 255))
    rect(img, 17, 11, 3, 3, (100, 60, 180, 255))
    pixel(img, 13, 12, WHITE)
    pixel(img, 18, 12, WHITE)
    # Contours
    outline(img, 11, 7, 10, 11, BLACK)
    outline(img, 8, 20, 16, 24, BLACK)
    # Bras
    rect(img, 3, 22, 4, 12, LWIZ_ROBE)
    rect(img, 25, 22, 4, 12, LWIZ_ROBE)
    # Orbe cosmique
    rect(img, 26, 16, 8, 8, LWIZ_ORB)
    outline(img, 26, 16, 8, 8, BLACK)
    pixel(img, 29, 19, WHITE)
    pixel(img, 30, 20, WHITE)
    # Halo cosmique
    for ox, oy in [(-3,0),(3,0),(0,-3),(0,3),(-2,-2),(2,-2),(-2,2),(2,2)]:
        pixel(img, 30+ox, 20+oy, LWIZ_GLOW)
    # Compagnon végétal (mini gardien en bas à droite)
    rect(img, 24, 38, 6, 8, (60, 160, 60, 255))
    rect(img, 25, 35, 4, 5, (80, 200, 80, 255))
    pixel(img, 26, 36, (255, 200, 100, 255))  # fleur
    pixel(img, 27, 36, (255, 200, 100, 255))
    outline(img, 24, 38, 6, 8, BLACK)
    return img

# ══════════════════════════════════════════════════════════════
# ENNEMIS
# ══════════════════════════════════════════════════════════════
def draw_slime():
    img = new_img(32, 32)
    # Corps verdâtre
    rect(img, 6, 14, 20, 14, (80, 180, 60, 255))
    rect(img, 4, 18, 24, 10, (80, 180, 60, 255))
    rect(img, 8, 12, 16, 6, (80, 180, 60, 255))
    # Reflet
    rect(img, 10, 14, 5, 3, (140, 230, 100, 200))
    # Yeux
    rect(img, 11, 18, 3, 3, BLACK)
    rect(img, 18, 18, 3, 3, BLACK)
    pixel(img, 12, 19, WHITE)
    pixel(img, 19, 19, WHITE)
    # Contour
    outline(img, 6, 14, 20, 14, BLACK)
    outline(img, 4, 18, 24, 10, BLACK)
    return img

def draw_loup():
    img = new_img(48, 32)
    # Corps
    rect(img, 8, 12, 30, 16, (40, 30, 60, 255))
    # Tête
    rect(img, 4, 6, 16, 14, (40, 30, 60, 255))
    # Museau
    rect(img, 2, 12, 8, 6, (60, 50, 80, 255))
    # Oreilles
    rect(img, 6, 2, 4, 6, (40, 30, 60, 255))
    rect(img, 14, 2, 4, 6, (40, 30, 60, 255))
    # Yeux rouges
    rect(img, 7, 9, 3, 3, (200, 30, 30, 255))
    rect(img, 13, 9, 3, 3, (200, 30, 30, 255))
    # Dents
    pixel(img, 4, 16, WHITE); pixel(img, 6, 17, WHITE); pixel(img, 8, 16, WHITE)
    # Pattes
    rect(img, 10, 26, 5, 6, (40, 30, 60, 255))
    rect(img, 20, 26, 5, 6, (40, 30, 60, 255))
    rect(img, 30, 26, 5, 6, (40, 30, 60, 255))
    # Queue
    rect(img, 36, 8, 10, 5, (40, 30, 60, 255))
    # Contours
    outline(img, 8, 12, 30, 16, BLACK)
    outline(img, 4, 6, 16, 14, BLACK)
    return img

def draw_golem():
    img = new_img(48, 56)
    CRYSTAL_BLUE = (80, 140, 220, 255)
    CRYSTAL_LIGHT = (140, 200, 255, 255)
    # Corps massif
    rect(img, 8, 16, 32, 36, CRYSTAL_BLUE)
    # Tête
    rect(img, 12, 4, 24, 18, CRYSTAL_BLUE)
    # Cristaux sur corps
    for cx, cy in [(10,20),(30,20),(18,36),(10,40),(34,40)]:
        rect(img, cx, cy, 4, 8, CRYSTAL_LIGHT)
    # Yeux lumineux
    rect(img, 18, 10, 5, 5, (200, 230, 255, 255))
    rect(img, 26, 10, 5, 5, (200, 230, 255, 255))
    # Bras énormes
    rect(img, 0, 18, 10, 20, CRYSTAL_BLUE)
    rect(img, 38, 18, 10, 20, CRYSTAL_BLUE)
    # Pieds
    rect(img, 8, 50, 12, 6, (60, 100, 180, 255))
    rect(img, 28, 50, 12, 6, (60, 100, 180, 255))
    # Contours
    outline(img, 8, 16, 32, 36, BLACK)
    outline(img, 12, 4, 24, 18, BLACK)
    outline(img, 0, 18, 10, 20, BLACK)
    outline(img, 38, 18, 10, 20, BLACK)
    return img

# ══════════════════════════════════════════════════════════════
# MORVAX — Boss final (80x96 px)
# ══════════════════════════════════════════════════════════════
def draw_morvax():
    img = new_img(80, 96)
    VOID_PURPLE = (30, 10, 50, 255)
    VOID_DARK   = (15, 5, 30, 255)
    VOID_GLOW   = (150, 30, 200, 255)
    VOID_EYES   = (255, 50, 50, 255)
    ROBE        = (20, 8, 40, 255)

    # Cape/corps
    rect(img, 15, 30, 50, 60, ROBE)
    rect(img, 10, 50, 60, 40, ROBE)
    # Éclats de cape
    rect(img, 5, 55, 12, 30, VOID_DARK)
    rect(img, 63, 55, 12, 30, VOID_DARK)
    rect(img, 0, 65, 8, 20, VOID_DARK)
    rect(img, 72, 65, 8, 20, VOID_DARK)

    # Tête
    rect(img, 22, 8, 36, 28, VOID_PURPLE)
    # Couronne du néant
    for cx in [22, 28, 34, 40, 46, 52]:
        rect(img, cx, 2, 4, 10, VOID_GLOW)
    rect(img, 20, 6, 40, 6, VOID_GLOW)

    # Yeux rouges brillants
    rect(img, 28, 16, 8, 6, VOID_EYES)
    rect(img, 44, 16, 8, 6, VOID_EYES)
    # Pupilles
    rect(img, 30, 17, 4, 4, (255, 0, 0, 255))
    rect(img, 46, 17, 4, 4, (255, 0, 0, 255))
    pixel(img, 31, 18, WHITE)
    pixel(img, 47, 18, WHITE)

    # Bouche / crocs
    rect(img, 32, 28, 16, 4, BLACK)
    for tx in [33, 37, 41, 45]:
        pixel(img, tx, 31, WHITE)
        pixel(img, tx, 32, WHITE)

    # Bras spectraux
    rect(img, 2, 32, 16, 8, VOID_PURPLE)
    rect(img, 62, 32, 16, 8, VOID_PURPLE)
    # Mains/griffes
    for i in range(4):
        rect(img, 2 + i*2, 38, 2, 10, VOID_GLOW)
    for i in range(4):
        rect(img, 62 + i*2, 38, 2, 10, VOID_GLOW)

    # Aura de néant (pixels semi-transparents autour)
    AURA = (100, 0, 150, 120)
    for x in range(0, 80, 3):
        pixel(img, x, 0, AURA)
        pixel(img, x, 95, AURA)
    for y in range(0, 96, 3):
        pixel(img, 0, y, AURA)
        pixel(img, 79, y, AURA)

    # Orbe du néant (tenu dans la main)
    rect(img, 62, 24, 16, 16, (50, 0, 80, 255))
    outline(img, 62, 24, 16, 16, VOID_GLOW)
    pixel(img, 69, 31, (200, 100, 255, 255))
    pixel(img, 70, 30, (255, 150, 255, 255))

    # Contours principaux
    outline(img, 22, 8, 36, 28, BLACK)
    outline(img, 15, 30, 50, 60, BLACK)

    return img

# ══════════════════════════════════════════════════════════════
# FONDS DE COMBAT (256x144 px — 16:9 compact)
# ══════════════════════════════════════════════════════════════
def draw_battle_bg(name, sky_top, sky_bot, ground_color, accent=None):
    W, H = 256, 144
    img = new_img(W, H)
    draw = ImageDraw.Draw(img)
    # Dégradé ciel
    for y in range(H * 2 // 3):
        t = y / (H * 2 // 3)
        r = int(sky_top[0]*(1-t) + sky_bot[0]*t)
        g = int(sky_top[1]*(1-t) + sky_bot[1]*t)
        b = int(sky_top[2]*(1-t) + sky_bot[2]*t)
        draw.line([(0,y),(W,y)], fill=(r,g,b,255))
    # Sol
    for y in range(H*2//3, H):
        t = (y - H*2//3) / (H//3)
        r = int(ground_color[0]*(1-t*0.4))
        g = int(ground_color[1]*(1-t*0.4))
        b = int(ground_color[2]*(1-t*0.4))
        draw.line([(0,y),(W,y)], fill=(r,g,b,255))
    # Ligne d'horizon
    draw.line([(0, H*2//3),(W, H*2//3)], fill=accent or (200,200,200,180), width=2)
    # Étoiles (pour les zones sombres)
    if sky_top[0] < 80:
        import random; random.seed(42)
        for _ in range(30):
            sx, sy = random.randint(0,W), random.randint(0, H*2//3-5)
            pixel(img, sx, sy, (255,255,255,200))
    return img

# ══════════════════════════════════════════════════════════════
# UI — Fenêtre de dialogue et éléments
# ══════════════════════════════════════════════════════════════
def draw_ui_frame():
    img = new_img(256, 64)
    draw = ImageDraw.Draw(img)
    # Fond
    draw.rectangle([(0,0),(255,63)], fill=(10,10,40,220))
    # Bordure externe
    draw.rectangle([(0,0),(255,63)], outline=(80,120,200,255), width=2)
    # Bordure interne (dorée)
    draw.rectangle([(3,3),(252,60)], outline=(180,150,60,200), width=1)
    # Coins décoratifs
    for cx, cy in [(2,2),(250,2),(2,58),(250,58)]:
        draw.rectangle([(cx-2,cy-2),(cx+2,cy+2)], fill=(200,170,80,255))
    return img

def draw_cursor():
    img = new_img(16, 16)
    # Flèche pixel art
    pixels = [
        (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),
        (3,2),(4,3),(5,4),(6,5),(7,6),(8,7),
        (3,4),(4,5),(5,6),
        (4,7),(5,8),(6,7),
    ]
    for px, py in pixels:
        pixel(img, px, py, (255, 220, 50, 255))
    # Contour
    for px, py in pixels:
        for dx, dy in [(-1,0),(1,0),(0,-1),(0,1)]:
            if (px+dx, py+dy) not in pixels:
                pixel(img, px+dx, py+dy, (0,0,0,180))
    return img

def draw_title_bg():
    W, H = 512, 288
    img = new_img(W, H)
    draw = ImageDraw.Draw(img)
    # Fond nuit étoilé
    for y in range(H):
        t = y / H
        r = int(5 + 15*t)
        g = int(5 + 5*t)
        b = int(20 + 30*t)
        draw.line([(0,y),(W,y)], fill=(r,g,b,255))
    # Étoiles
    import random; random.seed(99)
    for _ in range(120):
        sx, sy = random.randint(0,W), random.randint(0,H*3//4)
        br = random.randint(150, 255)
        size = random.choice([1,1,1,2])
        draw.rectangle([(sx,sy),(sx+size,sy+size)], fill=(br,br,br,200))
    # Cristal central
    cx, cy = W//2, H//2
    pts = [(cx,cy-80),(cx+40,cy),(cx,cy+40),(cx-40,cy)]
    draw.polygon(pts, fill=(80,120,220,180), outline=(180,220,255,220))
    pts2 = [(cx,cy-50),(cx+20,cy),(cx,cy+20),(cx-20,cy)]
    draw.polygon(pts2, fill=(140,180,255,220))
    # Halo
    for r in range(60,20,-8):
        alpha = max(0, 60 - r)
        draw.ellipse([(cx-r,cy-r),(cx+r,cy+r)], outline=(100,150,255,alpha))
    return img

def draw_splash():
    W, H = 512, 288
    img = new_img(W, H)
    draw = ImageDraw.Draw(img)
    # Fond noir
    draw.rectangle([(0,0),(W,H)], fill=(0,0,0,255))
    # Logo studio (étoile stylisée)
    cx, cy = W//2, H//2
    pts = []
    import math
    for i in range(10):
        angle = math.pi/2 + i * 2*math.pi/10
        r = 60 if i%2==0 else 25
        pts.append((cx + int(r*math.cos(angle)), cy + int(r*math.sin(angle))))
    draw.polygon(pts, fill=(200,170,50,255), outline=(255,220,80,255))
    # Centre
    draw.ellipse([(cx-15,cy-15),(cx+15,cy+15)], fill=(255,240,150,255))
    return img

# ══════════════════════════════════════════════════════════════
# EFFETS MAGIQUES
# ══════════════════════════════════════════════════════════════
def draw_effects():
    """Spritesheet 128x128 avec 4 effets 32x32"""
    img = new_img(128, 128)
    draw = ImageDraw.Draw(img)

    # Effet 1 — Soin (vert, croix)
    for x in range(10,22): draw.rectangle([(x,48),(x,80)], fill=(60,220,80,255))
    for y in range(58,70): draw.rectangle([(4,y),(28,y)], fill=(60,220,80,255))

    # Effet 2 — Attaque stellaire (jaune, éclat)
    import math
    for i in range(8):
        angle = i * math.pi/4
        for r in range(2,14):
            px2 = 80 + int(r*math.cos(angle))
            py2 = 16 + int(r*math.sin(angle))
            if 64<=px2<128 and 0<=py2<32:
                img.putpixel((px2,py2),(255,220,50,255))

    # Effet 3 — Magie cosmique (violet, orbe)
    draw.ellipse([(4,68),(28,92)], fill=(150,50,220,200), outline=(200,100,255,255))
    draw.ellipse([(10,74),(22,86)], fill=(220,180,255,255))

    # Effet 4 — Feu/Explosion (rouge-orange)
    for r2 in range(14,2,-2):
        alpha = min(255, r2*18)
        col = (255, max(0,220-r2*10), 0, alpha)
        draw.ellipse([(96-r2,96-r2),(96+r2,96+r2)], fill=col)

    return img

# ══════════════════════════════════════════════════════════════
# GÉNÉRATION DE TOUS LES ASSETS
# ══════════════════════════════════════════════════════════════

print("🎨 Génération des sprites...")

# Héros
nassim = draw_nassim()
nassim_big = nassim.resize((64, 96), Image.NEAREST)
nassim_big.save(f"{OUT}/nassim.png")
print("  ✅ nassim.png")

yasmine = draw_yasmine()
yasmine_big = yasmine.resize((64, 96), Image.NEAREST)
yasmine_big.save(f"{OUT}/yasmine.png")
print("  ✅ yasmine.png")

lwiz = draw_lwiz()
lwiz_big = lwiz.resize((64, 96), Image.NEAREST)
lwiz_big.save(f"{OUT}/lwiz.png")
print("  ✅ lwiz.png")

# Ennemis
slime = draw_slime()
slime.resize((64, 64), Image.NEAREST).save(f"{OUT}/enemy_slime.png")
print("  ✅ enemy_slime.png")

loup = draw_loup()
loup.resize((96, 64), Image.NEAREST).save(f"{OUT}/enemy_loup.png")
print("  ✅ enemy_loup.png")

golem = draw_golem()
golem.resize((96, 112), Image.NEAREST).save(f"{OUT}/enemy_golem.png")
print("  ✅ enemy_golem.png")

# Boss
morvax = draw_morvax()
morvax.resize((160, 192), Image.NEAREST).save(f"{OUT}/boss_morvax.png")
print("  ✅ boss_morvax.png")

# Fonds de combat
bgs = {
    "battle_bg_village": ((80,120,180),(40,70,120),(60,90,50),(200,220,255)),
    "battle_bg_foret":   ((20,50,20),(10,30,10),(30,80,20),(100,200,100)),
    "battle_bg_grotte":  ((15,15,35),(5,5,20),(40,35,60),(150,180,255)),
    "battle_bg_desert":  ((200,160,80),(160,120,40),(160,130,60),(255,220,150)),
    "battle_bg_temple":  ((80,70,50),(50,40,30),(90,80,55),(220,200,150)),
    "battle_bg_cite":    ((120,140,180),(80,100,150),(100,110,140),(200,220,255)),
    "battle_bg_chateau": ((20,5,30),(5,2,15),(15,5,25),(180,50,255)),
}
for bgname, (st,sb,gc,acc) in bgs.items():
    bg = draw_battle_bg(bgname, st, sb, gc, acc)
    bg.resize((512, 288), Image.NEAREST).save(f"{OUT}/{bgname}.png")
    print(f"  ✅ {bgname}.png")

# UI
ui_frame = draw_ui_frame()
ui_frame.resize((512, 128), Image.NEAREST).save(f"{OUT}/ui_frame.png")
print("  ✅ ui_frame.png")

cursor = draw_cursor()
cursor.resize((32, 32), Image.NEAREST).save(f"{OUT}/cursor.png")
print("  ✅ cursor.png")

title_bg = draw_title_bg()
title_bg.resize((1024, 576), Image.NEAREST).save(f"{OUT}/title_bg.png")
print("  ✅ title_bg.png")

splash = draw_splash()
splash.resize((1024, 576), Image.NEAREST).save(f"{OUT}/splash.png")
print("  ✅ splash.png")

effects = draw_effects()
effects.save(f"{OUT}/effects.png")
print("  ✅ effects.png")

print(f"\n✅ Tous les sprites générés dans {OUT}/")
print(f"   Total : {len(list(__import__('os').listdir(OUT)))} fichiers")
