package com.astralya.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

class AssetLoader(private val manager: AssetManager) {

    // ── Chargement des assets ─────────────────────────────────────────────────

    fun loadAll() {
        // Textures individuelles - Héros
        manager.load("sprites/nassim.png", Texture::class.java)
        manager.load("sprites/yasmine.png", Texture::class.java)
        manager.load("sprites/lwiz.png", Texture::class.java)

        // Ennemis
        manager.load("sprites/enemy_slime.png", Texture::class.java)
        manager.load("sprites/enemy_loup.png", Texture::class.java)
        manager.load("sprites/enemy_golem.png", Texture::class.java)
        manager.load("sprites/boss_morvax.png", Texture::class.java)

        // Textures fonds et décors
        manager.load("sprites/splash.png", Texture::class.java)
        manager.load("sprites/title_bg.png", Texture::class.java)
        manager.load("sprites/battle_bg_foret.png", Texture::class.java)
        manager.load("sprites/battle_bg_grotte.png", Texture::class.java)
        manager.load("sprites/battle_bg_desert.png", Texture::class.java)
        manager.load("sprites/battle_bg_temple.png", Texture::class.java)
        manager.load("sprites/battle_bg_cite.png", Texture::class.java)
        manager.load("sprites/battle_bg_chateau.png", Texture::class.java)
        manager.load("sprites/battle_bg_village.png", Texture::class.java)

        // UI
        manager.load("sprites/ui_frame.png", Texture::class.java)
        manager.load("sprites/cursor.png", Texture::class.java)
        manager.load("sprites/effects.png", Texture::class.java)

        // Audio - Musique
        manager.load("audio/music_village.ogg", Music::class.java)
        manager.load("audio/music_foret.ogg", Music::class.java)
        manager.load("audio/music_grotte.ogg", Music::class.java)
        manager.load("audio/music_desert.ogg", Music::class.java)
        manager.load("audio/music_temple.ogg", Music::class.java)
        manager.load("audio/music_cite.ogg", Music::class.java)
        manager.load("audio/music_boss.ogg", Music::class.java)
        manager.load("audio/music_battle.ogg", Music::class.java)
        manager.load("audio/music_victory.ogg", Music::class.java)
        manager.load("audio/music_gameover.ogg", Music::class.java)

        // SFX - Tous en .ogg
        manager.load("audio/sfx_attack.ogg", Sound::class.java)
        manager.load("audio/sfx_magic.ogg", Sound::class.java)
        manager.load("audio/sfx_heal.ogg", Sound::class.java)
        manager.load("audio/sfx_hit.ogg", Sound::class.java)
        manager.load("audio/sfx_critical.ogg", Sound::class.java)
        manager.load("audio/sfx_levelup.ogg", Sound::class.java)
        manager.load("audio/sfx_chest.ogg", Sound::class.java)
        manager.load("audio/sfx_menu_select.ogg", Sound::class.java)
        manager.load("audio/sfx_menu_cancel.ogg", Sound::class.java)
        manager.load("audio/sfx_portal.ogg", Sound::class.java)
        manager.load("audio/sfx_boss_appear.ogg", Sound::class.java)
    }

    fun update(): Boolean = manager.update()

    val progress: Float get() = manager.progress

    fun <T> get(path: String, type: Class<T>): T = manager.get(path, type)

    fun getTexture(path: String): Texture = try {
        manager.get(path, Texture::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Texture non trouvée: $path")
        throw e
    }

    fun getAtlas(path: String): TextureAtlas = try {
        manager.get(path, TextureAtlas::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Atlas non trouvé: $path")
        throw e
    }

    fun getMusic(path: String): Music = try {
        manager.get(path, Music::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Musique non trouvée: $path")
        throw e
    }

    fun getSound(path: String): Sound = try {
        manager.get(path, Sound::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Son non trouvé: $path")
        throw e
    }

    fun getBattleBackground(mapId: String): Texture {
        val path = when (mapId) {
            "foret_enchantee" -> "sprites/battle_bg_foret.png"
            "grotte_cristal"  -> "sprites/battle_bg_grotte.png"
            "desert_oublie"   -> "sprites/battle_bg_desert.png"
            "temple_etoiles"  -> "sprites/battle_bg_temple.png"
            "cite_volante"    -> "sprites/battle_bg_cite.png"
            "chateau_morvax"  -> "sprites/battle_bg_chateau.png"
            else              -> "sprites/battle_bg_village.png"
        }
        return getTexture(path)
    }
}
