# 🌟 Les Gardiens d'Astralya

RPG Android inspiré de Golden Sun et Final Fantasy, développé avec Kotlin + LibGDX.

---

## 📋 Prérequis

| Outil | Version minimale | Lien |
|---|---|---|
| Android Studio | Hedgehog (2023.1+) | https://developer.android.com/studio |
| JDK | 17 | https://adoptium.net |
| Android SDK | API 26+ | via Android Studio |
| Gradle | 8.4 (wrapper inclus) | automatique |

---

## 🚀 Installation rapide

### 1. Ouvrir dans Android Studio

```
File → Open → sélectionner le dossier AstralYa
```

Attendre la synchronisation Gradle (première fois : 3–5 min, téléchargement des dépendances).

### 2. Configurer le SDK Android

```
File → Project Structure → SDK Location
→ Indiquer le chemin de votre Android SDK
   Ex: ~/Android/Sdk  (Linux/macOS)
       C:\Users\<nom>\AppData\Local\Android\Sdk  (Windows)
```

### 3. Build APK Debug (le plus simple)

#### Via Android Studio :
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
APK généré dans :
```
android/build/outputs/apk/debug/android-debug.apk
```

#### Via script terminal :
```bash
# Linux / macOS
chmod +x build_apk.sh
./build_apk.sh

# Windows
build_apk.bat
```

### 4. Installer sur votre appareil Android

```bash
# Activer "Sources inconnues" sur l'appareil
# Activer "Débogage USB" dans Options développeur

adb install -r android/build/outputs/apk/debug/android-debug.apk
```

Ou copiez l'APK sur votre téléphone et ouvrez-le.

---

## 🎮 Contrôles

| Action | Clavier | Tactile |
|---|---|---|
| Déplacement | ZQSD / Flèches | Joystick gauche (bas-gauche écran) |
| Interagir / Confirmer | ESPACE ou ENTRÉE | Bouton droit bas |
| Annuler / Retour | X ou ÉCHAP | Bouton gauche bas |
| Menu | ÉCHAP ou M | Bouton MENU (haut droite) |
| Combat — Naviguer | Flèches | Toucher les options |

---

## 🗺️ Zones du jeu

| Zone | Musique | Difficulté |
|---|---|---|
| Village d'Étoilebourg | music_village.ogg | — |
| Forêt Enchantée | music_foret.ogg | ⭐ |
| Grotte des Cristaux | music_grotte.ogg | ⭐⭐ |
| Désert Oublié | music_desert.ogg | ⭐⭐⭐ |
| Temple des Sept Étoiles | music_temple.ogg | ⭐⭐⭐⭐ |
| Cité Volante | music_cite.ogg | ⭐⭐⭐⭐ |
| Château de Morvax | music_boss.ogg | ⭐⭐⭐⭐⭐ |

---

## 🎵 Assets audio à créer

Le jeu nécessite les fichiers audio suivants (format `.ogg` et `.wav`) dans `android/src/main/assets/audio/` :

**Musiques :**
- `music_village.ogg` — Ambiance paisible, instruments à cordes
- `music_foret.ogg` — Mystérieux, flûte et nature
- `music_grotte.ogg` — Sombre et cristallin
- `music_desert.ogg` — Épique et chaud, instruments du Moyen-Orient
- `music_temple.ogg` — Grandiose, chœurs et cordes
- `music_cite.ogg` — Orchestral et aérien
- `music_boss.ogg` — Intense et dramatique (combat final)
- `music_battle.ogg` — Combat rythmé et énergique
- `music_victory.ogg` — Fanfare de victoire (court)
- `music_gameover.ogg` — Sombre et mélancolique

**Effets sonores :**
- `sfx_attack.wav`, `sfx_magic.wav`, `sfx_heal.wav`
- `sfx_hit.wav`, `sfx_critical.wav`, `sfx_levelup.wav`
- `sfx_chest.wav`, `sfx_menu_select.wav`, `sfx_menu_cancel.wav`
- `sfx_portal.wav`, `sfx_boss_appear.wav`

> 💡 Sources audio libres : [OpenGameArt.org](https://opengameart.org) · [freesound.org](https://freesound.org)

---

## 🖼️ Assets graphiques à créer

Dossier : `android/src/main/assets/sprites/`

**Requis :**
- `splash.png` — Logo intro (1920×1080)
- `title_bg.png` — Fond menu principal
- `battle_bg_*.png` — Fonds de combat par zone (7 images)
- `heroes.atlas` + `heroes.png` — Sprites des héros (TextureAtlas LibGDX)
- `enemies.atlas` + `enemies.png` — Sprites ennemis
- `ui.atlas` + `ui.png` — Éléments UI (fenêtres, boutons, curseurs)
- `effects.atlas` + `effects.png` — Effets magiques et animations

> 💡 Outils : [Libresprite](https://libresprite.github.io) · [Aseprite](https://www.aseprite.org) · [TexturePacker](https://www.codeandweb.com/texturepacker)
> Pour les tilemaps `.tmx` : [Tiled Map Editor](https://www.mapeditor.org)

---

## 🏗️ Architecture du projet

```
AstralYa/
├── core/                          # Logique jeu (plateforme-indépendant)
│   └── src/main/java/com/astralya/
│       ├── AstralYaGame.kt        # Classe principale LibGDX
│       ├── screens/               # Tous les écrans
│       │   ├── MenuScreens.kt     # Splash + Menu principal
│       │   ├── ExplorationScreen.kt
│       │   ├── BattleScreen.kt
│       │   └── UIScreens.kt       # Inventaire, Équipe, Save, Options
│       ├── entities/              # Héros, ennemis, items
│       │   ├── Entities.kt        # Hero, Enemy, Item, Skills
│       │   ├── HeroFactory.kt     # Nassim, Yasmine, Lwiz + Combos
│       │   ├── EnemyFactory.kt    # Tous les ennemis + Morvax
│       │   └── ItemFactory.kt     # Catalogue d'items
│       ├── combat/
│       │   └── CombatSystem.kt    # Moteur tour par tour complet
│       ├── map/
│       │   ├── MapSystem.kt       # 7 zones, NPC, coffres, portails
│       │   └── QuestSystem.kt     # Quêtes principales + secondaires
│       ├── data/
│       │   └── GameState.kt       # État global runtime
│       ├── utils/
│       │   └── AssetLoader.kt     # Chargement assets LibGDX
│       └── audio/
│           └── AudioManager.kt    # Musique + SFX
│
├── android/                       # Module Android
│   └── src/main/
│       ├── java/com/astralya/
│       │   ├── AndroidLauncher.kt # Point d'entrée Android
│       │   └── data/
│       │       ├── AstralYaDatabase.kt
│       │       ├── dao/GameDaos.kt
│       │       ├── entities/GameEntities.kt
│       │       └── repository/GameRepository.kt
│       ├── assets/                # Audio, sprites, maps
│       ├── res/                   # Icônes, strings, styles
│       └── AndroidManifest.xml
│
├── build.gradle.kts               # Config racine
├── settings.gradle.kts
├── gradle.properties
├── build_apk.sh                   # Script build Linux/macOS
└── build_apk.bat                  # Script build Windows
```

---

## 🐛 Résolution des problèmes courants

**`SDK location not found`**
→ Créez le fichier `local.properties` à la racine :
```
sdk.dir=/home/votre_nom/Android/Sdk
```

**`Gradle sync failed`**
→ `File → Invalidate Caches → Invalidate and Restart`

**`Could not resolve com.badlogicgames.gdx`**
→ Vérifiez votre connexion internet. Les dépendances LibGDX se téléchargent depuis Maven Central.

**`AAPT2 error: check logs`**
→ `Build → Clean Project` puis `Build → Rebuild Project`

**L'APK s'installe mais écran noir**
→ Les assets audio/sprites sont manquants. Ajoutez des fichiers placeholder dans `android/src/main/assets/` ou commentez temporairement les appels à `assetLoader.getMusic()`.

---

## 📦 Build Release (distribution)

### 1. Créer un keystore (une seule fois)
```bash
keytool -genkey -v \
  -keystore astralya-release.jks \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -alias astralya
```

### 2. Signer et builder
```bash
./gradlew :android:assembleRelease \
  -Pandroid.injected.signing.store.file=astralya-release.jks \
  -Pandroid.injected.signing.store.password=VOTRE_MOT_DE_PASSE \
  -Pandroid.injected.signing.key.alias=astralya \
  -Pandroid.injected.signing.key.password=VOTRE_MOT_DE_PASSE
```

APK release : `android/build/outputs/apk/release/android-release.apk`

---

## 👥 Personnages

| Héros | Classe | Rôle | Arme |
|---|---|---|---|
| **Nassim** | Chevalier Stellaire | Tank/DPS | Épée Aurore |
| **Yasmine** | Prêtresse de Lumière | Support/Soins | Bâton de Cristal |
| **Lwiz** | Enfant des Étoiles | Mage Cosmique | Orbe Cosmique |

**Boss final :** Morvax — Seigneur du Néant (9999 HP)

---

## ⚡ Combos disponibles

| Combo | Héros | Effet |
|---|---|---|
| Lumière Astrale | Yasmine + Lwiz | Dégâts + soin zone |
| Lame Stellaire | Nassim + Lwiz | Dégâts massifs |
| Rempart Sacré | Nassim + Yasmine | Bouclier équipe |

---

*Les Gardiens d'Astralya — Projet open source*
