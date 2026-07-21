@echo off
REM ═══════════════════════════════════════════════════════════════
REM  BUILD SCRIPT — Les Gardiens d'Astralya (Windows)
REM ═══════════════════════════════════════════════════════════════

echo.
echo  ╔═══════════════════════════════════════════════╗
echo  ║   Les Gardiens d'Astralya — Build APK        ║
echo  ╚═══════════════════════════════════════════════╝
echo.

WHERE java >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
  echo [ERREUR] Java non trouve. Installez JDK 17+.
  pause & exit /b 1
)

IF "%ANDROID_HOME%"=="" (
  echo [AVERTISSEMENT] ANDROID_HOME non defini.
  echo Definissez-le dans vos variables d'environnement systeme.
  echo Ex : ANDROID_HOME=C:\Users\VotreNom\AppData\Local\Android\Sdk
)

echo [1] Build DEBUG  ^(rapide^)
echo [2] Build RELEASE ^(optimise^)
echo.
set /p BUILD_CHOICE="Choix [1/2] : "

IF NOT EXIST output mkdir output

IF "%BUILD_CHOICE%"=="2" (
  echo.
  echo Build RELEASE...
  call gradlew.bat :android:assembleRelease
  IF EXIST android\build\outputs\apk\release\android-release.apk (
    copy android\build\outputs\apk\release\android-release.apk output\AstralYa-release.apk
    echo.
    echo  SUCCESS : output\AstralYa-release.apk
  ) ELSE (
    echo [ERREUR] APK release non trouve.
  )
) ELSE (
  echo.
  echo Build DEBUG...
  call gradlew.bat :android:assembleDebug
  IF EXIST android\build\outputs\apk\debug\android-debug.apk (
    copy android\build\outputs\apk\debug\android-debug.apk output\AstralYa-debug.apk
    echo.
    echo  SUCCESS : output\AstralYa-debug.apk
    echo.
    echo Installation sur appareil :
    echo   adb install -r output\AstralYa-debug.apk
  ) ELSE (
    echo [ERREUR] APK debug non trouve.
  )
)

pause
