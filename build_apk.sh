#!/bin/bash
# ═══════════════════════════════════════════════════════════════
#  BUILD SCRIPT — Les Gardiens d'Astralya
#  Génère un APK debug ou release
# ═══════════════════════════════════════════════════════════════

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="$PROJECT_DIR/output"

echo ""
echo "╔═══════════════════════════════════════════════╗"
echo "║   Les Gardiens d'Astralya — Build APK        ║"
echo "╚═══════════════════════════════════════════════╝"
echo ""

# ── Vérifications préalables ──────────────────────────────────
if ! command -v java &>/dev/null; then
  echo "❌  Java non trouvé. Installez JDK 17+."
  exit 1
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$JAVA_VER" -lt 17 ] 2>/dev/null; then
  echo "❌  Java 17+ requis (détecté : $JAVA_VER)."
  exit 1
fi

if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
  echo "⚠️   ANDROID_HOME non défini."
  echo "    Définissez-le : export ANDROID_HOME=~/Android/Sdk"
  echo "    Continuer quand même ? (o/n)"
  read -r CONTINUE
  [ "$CONTINUE" != "o" ] && exit 1
fi

echo "✅  Java $(java -version 2>&1 | head -1)"
echo "✅  ANDROID_HOME : ${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
echo ""

# ── Choix du type de build ─────────────────────────────────────
echo "Type de build :"
echo "  1) Debug   (rapide, pour tester)"
echo "  2) Release (optimisé, pour distribuer)"
echo ""
read -rp "Choix [1/2] : " BUILD_CHOICE

mkdir -p "$OUTPUT_DIR"

cd "$PROJECT_DIR"

if [ "$BUILD_CHOICE" = "2" ]; then
  echo ""
  echo "🔐  Build RELEASE — signature requise."
  echo ""
  read -rp "Chemin vers votre keystore (.jks) : " KEYSTORE_PATH
  read -rp "Alias de la clé : " KEY_ALIAS
  read -rsp "Mot de passe keystore : " KEYSTORE_PASS
  echo ""
  read -rsp "Mot de passe clé : " KEY_PASS
  echo ""

  echo "📦  Assemblage release..."
  ./gradlew :android:assembleRelease \
    -Pandroid.injected.signing.store.file="$KEYSTORE_PATH" \
    -Pandroid.injected.signing.store.password="$KEYSTORE_PASS" \
    -Pandroid.injected.signing.key.alias="$KEY_ALIAS" \
    -Pandroid.injected.signing.key.password="$KEY_PASS"

  APK_SRC="$PROJECT_DIR/android/build/outputs/apk/release/android-release.apk"
  APK_DST="$OUTPUT_DIR/AstralYa-release.apk"
else
  echo ""
  echo "📦  Assemblage debug..."
  ./gradlew :android:assembleDebug

  APK_SRC="$PROJECT_DIR/android/build/outputs/apk/debug/android-debug.apk"
  APK_DST="$OUTPUT_DIR/AstralYa-debug.apk"
fi

if [ -f "$APK_SRC" ]; then
  cp "$APK_SRC" "$APK_DST"
  SIZE=$(du -sh "$APK_DST" | cut -f1)
  echo ""
  echo "╔═══════════════════════════════════════════════╗"
  echo "║   ✅  BUILD RÉUSSI !                         ║"
  echo "╠═══════════════════════════════════════════════╣"
  echo "║   📱  APK : $APK_DST"
  echo "║   📏  Taille : $SIZE"
  echo "╚═══════════════════════════════════════════════╝"
  echo ""
  echo "Installation sur appareil connecté :"
  echo "  adb install -r \"$APK_DST\""
else
  echo "❌  APK non trouvé. Vérifiez les erreurs ci-dessus."
  exit 1
fi
