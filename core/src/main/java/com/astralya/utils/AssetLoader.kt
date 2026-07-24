package com.astralya.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader

class AssetLoader(private val manager: AssetManager) {

    init {
        manager.setLoader(TiledMap::class.java, TmxMapLoader())
    }

    // ── Chargement des assets ─────────────────────────────────────────────────

    fun loadAll() {
        // Textures individuelles - Héros
        manager.load("sprites/male_walkcycle.png", Texture::class.java)
        manager.load("sprites/female_walkcycle.png", Texture::class.java)
        manager.load("sprites/nassim.png", Texture::class.java)
        manager.load("sprites/yasmine.png", Texture::class.java)
        manager.load("sprites/lwiz.png", Texture::class.java)

        // Ennemis
        manager.load("sprites/enemy_slime.png", Texture::class.java)
        manager.load("sprites/enemy_loup.png", Texture::class.java)
        manager.load("sprites/enemy_golem.png", Texture::class.java)
        manager.load("sprites/boss_morvax.png", Texture::class.java)
        
        // Nouveaux monstres
        manager.load("sprites/bat.png", Texture::class.java)
        manager.load("sprites/bee.png", Texture::class.java)
        manager.load("sprites/ghost.png", Texture::class.java)
        manager.load("sprites/slime.png", Texture::class.java)
        manager.load("sprites/snake.png", Texture::class.java)
        manager.load("sprites/eyeball.png", Texture::class.java)
        manager.load("sprites/big_worm.png", Texture::class.java)
        manager.load("sprites/small_worm.png", Texture::class.java)
        manager.load("sprites/pumpking.png", Texture::class.java)
        manager.load("sprites/man_eater_flower.png", Texture::class.java)

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

        // UI et Sprites Interactifs
        manager.load("sprites/ui_frame.png", Texture::class.java)
        manager.load("sprites/cursor.png", Texture::class.java)
        manager.load("sprites/effects.png", Texture::class.java)

        // Assets Optionnels
        safeLoad("sprites/male_pants.png", Texture::class.java)
        safeLoad("sprites/hairmale.png", Texture::class.java)
        safeLoad("sprites/hairfemale.png", Texture::class.java)
        safeLoad("sprites/soldier.png", Texture::class.java)
        safeLoad("sprites/soldier_altcolor.png", Texture::class.java)
        safeLoad("sprites/princess.png", Texture::class.java)
        safeLoad("sprites/portal.png", Texture::class.java)
        safeLoad("sprites/chest_closed.png", Texture::class.java)
        safeLoad("sprites/chest_open.png", Texture::class.java)
        safeLoad("sprites/_map_village_bg.png", Texture::class.java)
        safeLoad("sprites/_map_foret_bg.png", Texture::class.java)

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

        // Maps Tiled
        manager.load("maps/village.tmx", TiledMap::class.java)
        manager.load("maps/foret.tmx", TiledMap::class.java)
        manager.load("maps/grotte.tmx", TiledMap::class.java)
        manager.load("maps/desert.tmx", TiledMap::class.java)
        manager.load("maps/temple.tmx", TiledMap::class.java)
        manager.load("maps/cite_volante.tmx", TiledMap::class.java)
        manager.load("maps/chateau.tmx", TiledMap::class.java)
        manager.load("maps/maison_interieur.tmx", TiledMap::class.java)
        manager.load("maps/chateau_etage_2.tmx", TiledMap::class.java)
    }

    fun update(): Boolean = manager.update()

    private fun <T> safeLoad(path: String, type: Class<T>) {
        // Sur Android, exists() peut être lent. On tente le load direct, 
        // l'AssetManager gérera l'erreur si le fichier manque.
        try {
            manager.load(path, type)
        } catch (e: Exception) {
            com.badlogic.gdx.Gdx.app.error("AstralYa", "Erreur safeLoad : $path")
        }
    }

    val progress: Float get() = manager.progress

    fun <T> get(path: String, type: Class<T>): T = manager.get(path, type)

    fun getTexture(path: String): Texture = try {
        if (manager.isLoaded(path)) {
            manager.get(path, Texture::class.java)
        } else {
            // Si pas chargé, on tente de le finir proprement (si mis en file d'attente)
            if (manager.getAssetNames().contains(path, false)) {
                manager.finishLoadingAsset<Texture>(path)
                manager.get(path, Texture::class.java)
            } else {
                throw Exception("Asset non en file d'attente")
            }
        }
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Texture absente : $path")
        throw e
    }

    fun getAtlas(path: String): TextureAtlas = try {
        if (!manager.isLoaded(path)) manager.finishLoadingAsset<TextureAtlas>(path)
        manager.get(path, TextureAtlas::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Atlas non trouvé ou non chargé: $path")
        throw e
    }

    fun getMusic(path: String): Music = try {
        if (!manager.isLoaded(path)) manager.finishLoadingAsset<Music>(path)
        manager.get(path, Music::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Musique non trouvée ou non chargée: $path")
        throw e
    }

    fun getSound(path: String): Sound = try {
        if (!manager.isLoaded(path)) manager.finishLoadingAsset<Sound>(path)
        manager.get(path, Sound::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Son non trouvé ou non chargé: $path")
        throw e
    }

    fun getTiledMap(path: String): TiledMap = try {
        if (!manager.isLoaded(path)) manager.finishLoadingAsset<TiledMap>(path)
        manager.get(path, TiledMap::class.java)
    } catch (e: Exception) {
        com.badlogic.gdx.Gdx.app.error("AstralYa", "ERREUR ASSET: Map Tiled non trouvée ou non chargée: $path")
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

    fun getEnemyTexture(enemyId: String): Texture {
        val path = when {
            enemyId.contains("slime_vert")   -> "sprites/slime.png"
            enemyId.contains("loup")         -> "sprites/enemy_loup.png"
            enemyId.contains("golem")        -> "sprites/enemy_golem.png"
            enemyId.contains("chauve_souris") -> "sprites/bat.png"
            enemyId.contains("serpent")      -> "sprites/snake.png"
            enemyId.contains("ghost") || enemyId.contains("ombre") -> "sprites/ghost.png"
            enemyId.contains("oeil")         -> "sprites/eyeball.png"
            enemyId.contains("bee") || enemyId.contains("fee") -> "sprites/bee.png"
            enemyId.contains("ver_geant")    -> "sprites/big_worm.png"
            enemyId.contains("citrouille")   -> "sprites/pumpking.png"
            enemyId.contains("fleur")        -> "sprites/man_eater_flower.png"
            enemyId == "morvax"              -> "sprites/boss_morvax.png"
            else                             -> "sprites/enemy_slime.png" // Fallback
        }
        return getTexture(path)
    }
}
