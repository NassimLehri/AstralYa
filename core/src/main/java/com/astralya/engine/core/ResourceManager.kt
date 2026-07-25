package com.astralya.engine.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.astralya.game.world.MapRegistry
import com.astralya.game.world.GameMap

/**
 * ResourceManager - Gestion centralisée des assets avec support du streaming par zone et AssetCatalog.
 */
class ResourceManager(private val manager: AssetManager) {

    private var currentLoadedZone: String? = null

    init {
        manager.setLoader(TiledMap::class.java, TmxMapLoader())
    }

    fun loadAll() {
        // Assets persistants via AssetCatalog
        manager.load(AssetCatalog.Sprites.NASSIM, Texture::class.java)
        manager.load(AssetCatalog.Sprites.YASMINE, Texture::class.java)
        manager.load(AssetCatalog.Sprites.LWIZ, Texture::class.java)
        manager.load(AssetCatalog.Sprites.UI_FRAME, Texture::class.java)
        manager.load(AssetCatalog.Sprites.PORTAL, Texture::class.java)
        manager.load(AssetCatalog.Sprites.CHEST_CLOSED, Texture::class.java)
        manager.load(AssetCatalog.Sprites.CHEST_OPEN, Texture::class.java)
        
        listOf("attack", "magic", "heal", "hit", "critical", "levelup", "chest", "menu_select", "menu_cancel", "portal", "boss_appear")
            .forEach { manager.load(AssetCatalog.Audio.sfx(it), Sound::class.java) }
    }

    fun loadZone(mapId: String, registry: MapRegistry) {
        if (currentLoadedZone == mapId) return
        val map = registry.getMap(mapId) ?: return
        
        val tmxPath = AssetCatalog.Maps.tmx(map.tilemapFile.removePrefix("maps/").removeSuffix(".tmx"))
        if (!manager.isLoaded(tmxPath)) manager.load(tmxPath, TiledMap::class.java)
        
        map.requiredAssets.forEach { path ->
            if (!manager.isLoaded(path)) {
                when {
                    path.endsWith(".png") -> manager.load(path, Texture::class.java)
                    path.endsWith(".ogg") -> manager.load(path, Music::class.java)
                }
            }
        }
        currentLoadedZone = mapId
    }

    fun unloadUnused(currentMapId: String, registry: MapRegistry) {
        val currentMap = registry.getMap(currentMapId) ?: return
        val allUnusedAssets = mutableSetOf<String>()
        
        registry.getAllMaps().forEach { map ->
            if (map.id != currentMapId) {
                allUnusedAssets.add(AssetCatalog.Maps.tmx(map.tilemapFile.removePrefix("maps/").removeSuffix(".tmx")))
                map.requiredAssets.forEach { asset ->
                    if (!currentMap.requiredAssets.contains(asset)) {
                        allUnusedAssets.add(asset)
                    }
                }
            }
        }
        
        allUnusedAssets.forEach { asset ->
            if (manager.isLoaded(asset)) manager.unload(asset)
        }
    }

    fun finishLoading() { manager.finishLoading() }
    fun update(): Boolean = manager.update()
    fun getProgress(): Float = manager.progress

    fun getTexture(name: String): Texture = manager.get("sprites/$name.png", Texture::class.java)
    fun getMusic(name: String): Music = manager.get("audio/music_$name.ogg", Music::class.java)
    fun getSound(name: String): Sound = manager.get("audio/sfx_$name.ogg", Sound::class.java)
    fun getMap(name: String): TiledMap = manager.get("maps/$name.tmx", TiledMap::class.java)
    
    fun <T> get(path: String, type: Class<T>): T = manager.get(path, type)
    fun dispose() { manager.dispose() }
}
