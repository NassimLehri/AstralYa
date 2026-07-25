package com.astralya.engine.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import java.util.Locale

/**
 * Epic 7 — Gère la localisation des textes (I18N).
 */
class LocalizationManager {

    private var bundle: I18NBundle? = null

    init {
        loadBundle()
    }

    fun loadBundle(locale: Locale = Locale.getDefault()) {
        try {
            val baseFileHandle = Gdx.files.internal("i18n/messages")
            bundle = I18NBundle.createBundle(baseFileHandle, locale)
        } catch (e: Exception) {
            Gdx.app.error("Localization", "Erreur lors du chargement de l'I18N: ${e.message}")
        }
    }

    fun get(key: String): String = bundle?.get(key) ?: key

    fun format(key: String, vararg args: Any): String = bundle?.format(key, *args) ?: key
}
