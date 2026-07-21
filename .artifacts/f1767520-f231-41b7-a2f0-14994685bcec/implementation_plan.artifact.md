# Ajout d'un module Desktop (LWJGL3) pour le test rapide

L'objectif est d'ajouter un module permettant de lancer le jeu directement sur Windows/Mac/Linux. Cela permet de tester les changements de code quasi-instantanément sans avoir à compiler un APK ou utiliser un émulateur.

## Proposed Changes

### [Nouveau Module LWJGL3]

#### [NEW] [lwjgl3/build.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/lwjgl3/build.gradle.kts)
- Configuration des dépendances LibGDX LWJGL3.
- Configuration du dossier d'assets pointant vers `android/src/main/assets`.

#### [NEW] [Lwjgl3Launcher.kt](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/lwjgl3/src/main/kotlin/com/astralya/lwjgl3/Lwjgl3Launcher.kt)
- Création de la classe principale pour démarrer l'application avec `Lwjgl3Application`.

### [Configuration du Projet]

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/settings.gradle.kts)
- Inclusion du module `:lwjgl3`.

## Verification Plan

### Automated Tests
- Exécuter `./gradlew :lwjgl3:assemble` pour vérifier que le module compile.

### Manual Verification
- L'utilisateur pourra lancer la tâche Gradle `:lwjgl3:run` pour démarrer le jeu sur son PC.
