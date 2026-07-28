# Guide de Déploiement : Astralya sur le Google Play Store

Ce guide vous accompagne dans les étapes finales pour publier officiellement **Astralya : Les Gardiens du Néant** sur le magasin d'applications.

## 📦 Étape 1 : Préparation du Bundle de Production (AAB)

Google exige désormais le format `.aab` (Android App Bundle) pour les nouvelles applications. Ce format permet à Google d'optimiser la taille du téléchargement pour chaque téléphone.

1.  **Générer le Bundle** :
    Ouvrez le terminal dans Android Studio et lancez :
    ```bash
    ./gradlew :android:bundleRelease
    ```
2.  **Localisation du fichier** :
    Une fois terminé, votre fichier se trouvera ici :
    `android/build/outputs/bundle/release/android-release.aab`

> [!IMPORTANT]
> Pour une publication réelle, vous devez **signer** votre application avec une clé `.jks`. Si vous n'en avez pas, allez dans `Build > Generate Signed Bundle / APK` dans Android Studio et suivez l'assistant pour créer un nouveau "KeyStore".

---

## 🛠️ Étape 2 : Configuration de la Google Play Console

1.  **Compte Développeur** : Connectez-vous sur [play.google.com/console](https://play.google.com/console). (Frais d'inscription unique de 25$).
2.  **Créer une application** : Cliquez sur "Créer une application" et remplissez les informations de base (Nom, Langue par défaut).
3.  **Fiche du Store** :
    *   **Description courte** : Un RPG épique inspiré des classiques du genre.
    *   **Description longue** : Utilisez le texte de l'introduction que nous avons poli (L'éveil des gardiens, Morvax, les 7 cristaux).
    *   **Graphismes** : Vous aurez besoin d'une icône (512x512) et d'une image de bannière (1024x500).

---

## 📜 Étape 3 : Informations Légales et Sécurité

1.  **Politique de Confidentialité** : Google exige un lien vers une politique de confidentialité.
    *   Utilisez le contenu du fichier [privacy_policy.artifact.md](file:///C:/Users/nassim_lehri/Downloads/AstralYa-v4-Final/AstralYa_v4/.artifacts/52897bfc-e352-44f7-8651-469d74ccb19c/privacy_policy.artifact.md) que nous avons généré.
    *   Hébergez ce texte sur une page simple (GitHub Pages ou un site perso).
2.  **Sécurité des données** :
    *   Répondez "Non" à la question "Votre application collecte-t-elle des données utilisateur ?".
    *   Précisez que toutes les données sont stockées localement sur l'appareil.

---

## 🚀 Étape 4 : Envoi pour Examen

1.  Allez dans **Production > Créer une nouvelle version**.
2.  Importez votre fichier `android-release.aab`.
3.  Cliquez sur **Vérifier la version** puis **Lancer le déploiement**.

> [!TIP]
> Le premier examen par Google peut prendre entre 2 et 7 jours. Profitez-en pour tester votre APK une dernière fois sur différents modèles de téléphones !

## 🎉 Conclusion
Votre projet est maintenant entre les mains de Google. Félicitations pour avoir transformé votre vision en un produit fini et professionnel !
