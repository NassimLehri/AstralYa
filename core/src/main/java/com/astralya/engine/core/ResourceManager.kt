package com.astralya.engine.core

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.BaseTiledMapLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.astralya.game.world.MapRegistry
import com.astralya.game.world.GameMap

/**
 * ResourceManager - Gestion centralisée des assets avec support du streaming par zone et AssetCatalog.
 */
class ResourceManager(private val manager: AssetManager) {

    private var currentLoadedZone: String? = null
    private val loadedAtlases = mutableListOf<String>()
    
    private val pixelParam = TextureLoader.TextureParameter().apply {
        minFilter = TextureFilter.Nearest
        magFilter = TextureFilter.Nearest
    }

    init {
        manager.setLoader(TiledMap::class.java, TmxMapLoader(manager.fileHandleResolver))
    }

    fun loadAll() {
        // Prefer loading any atlases placed under assets/atlases
        try {
            val dir = com.badlogic.gdx.Gdx.files.internal("atlases")
            if (dir.exists()) {
                dir.list()?.filter { it.extension().equals("atlas", true) }?.forEach { at ->
                    val path = "atlases/${at.name()}"
                    manager.load(path, com.badlogic.gdx.graphics.g2d.TextureAtlas::class.java)
                    loadedAtlases.add(path)
                }
            }
        } catch (_: Exception) {}

        // Assets persistants via AssetCatalog
        manager.load(AssetCatalog.Sprites.NASSIM, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.YASMINE, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.LWIZ, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.UI_FRAME, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.PORTAL, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.CHEST_CLOSED, Texture::class.java, pixelParam)
        manager.load(AssetCatalog.Sprites.CHEST_OPEN, Texture::class.java, pixelParam)
        
        listOf("attack", "magic", "heal", "hit", "critical", "levelup", "chest", "menu_select", "menu_cancel", "portal", "boss_appear")
            .forEach { loadSoundFallback(it) }
    }

    private fun loadSoundFallback(name: String) {
        try {
            val base = "audio/sfx_$name.ogg"
            val resolved = resolveAudioPathFrom(base) ?: "audio/sfx_$name.mp3"
            val fh = com.badlogic.gdx.Gdx.files.internal(resolved)
            if (fh.exists()) manager.load(resolved, Sound::class.java)
        } catch (_: Exception) {}
    }

    fun loadZone(mapId: String, registry: MapRegistry) {
        if (currentLoadedZone == mapId) return
        val map = registry.getMap(mapId) ?: return
        
        val tmxPath = AssetCatalog.Maps.tmx(map.tilemapFile.removePrefix("maps/").removeSuffix(".tmx"))
        // TmxMapLoader will load its textures based on internal logic, but usually uses the default filter.
        // We can force nearest after load if needed, but often TmxMapLoader uses TextureLoader internally.
        if (!manager.isLoaded(tmxPath)) {
            val tmxParam = BaseTiledMapLoader.Parameters().apply {
                textureMinFilter = TextureFilter.Nearest
                textureMagFilter = TextureFilter.Nearest
                flipY = true
            }
            // Some libGDX versions expose texture wrap settings on the parameters (textureWrap/textureWrapU/textureWrapV).
            // Try to set them via reflection to ClampToEdge so the loader won't create repeat-wrapped textures.
            try {
                val clamp = com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge
                val cls = tmxParam::class.java
                try { // try single `textureWrap`
                    val f = cls.getDeclaredField("textureWrap")
                    f.isAccessible = true
                    f.set(tmxParam, clamp)
                } catch (_: Exception) {}
                try { // try `textureWrapU` and `textureWrapV`
                    val fu = cls.getDeclaredField("textureWrapU")
                    val fv = cls.getDeclaredField("textureWrapV")
                    fu.isAccessible = true; fv.isAccessible = true
                    fu.set(tmxParam, clamp); fv.set(tmxParam, clamp)
                } catch (_: Exception) {}
            } catch (_: Exception) {}
            manager.load(tmxPath, TiledMap::class.java, tmxParam)
        }
        
        map.requiredAssets.forEach { path ->
            if (!manager.isLoaded(path)) {
                when {
                    path.endsWith(".png") -> manager.load(path, Texture::class.java, pixelParam)
                    path.endsWith(".ogg") || path.endsWith(".mp3") -> {
                        // prefer ogg over mp3 if both present
                        val audio = resolveAudioPathFrom(path)
                        if (audio != null) manager.load(audio, Music::class.java)
                    }
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

    fun isLoaded(path: String): Boolean = manager.isLoaded(path)
    fun finishLoading() { manager.finishLoading() }
    fun update(): Boolean = manager.update()
    fun getProgress(): Float = manager.progress

    fun getTexture(name: String): Texture {
        // Try texture atlas first
        val base = name
        for (atlasPath in loadedAtlases) {
            if (manager.isLoaded(atlasPath)) {
                try {
                    val atlas = manager.get(atlasPath, com.badlogic.gdx.graphics.g2d.TextureAtlas::class.java)
                    val region = atlas.findRegion(base)
                    if (region != null) return region.texture
                } catch (_: Exception) {}
            }
        }

        val path = "sprites/$name.png"
        return if (manager.isLoaded(path)) manager.get(path, Texture::class.java)
        else {
            com.badlogic.gdx.Gdx.app.error("ResourceManager", "Texture non chargée : $path")
            manager.get(AssetCatalog.Sprites.UI_FRAME, Texture::class.java) // Fallback
        }
    }

    fun getMusic(name: String): Music {
        val candidates = listOf("audio/music_$name.ogg", "audio/music_$name.mp3")
        val chosen = candidates.firstOrNull { manager.isLoaded(it) }
        if (chosen != null) return manager.get(chosen, Music::class.java)
        com.badlogic.gdx.Gdx.app.error("ResourceManager", "Musique non chargée : audio/music_$name (ogg/mp3)")
        throw com.badlogic.gdx.utils.GdxRuntimeException("Musique critique manquante : audio/music_$name")
    }

    fun getSound(name: String): Sound {
        val candidates = listOf("audio/sfx_$name.ogg", "audio/sfx_$name.mp3")
        val chosen = candidates.firstOrNull { manager.isLoaded(it) }
        if (chosen != null) return manager.get(chosen, Sound::class.java)
        com.badlogic.gdx.Gdx.app.error("ResourceManager", "SFX non chargé : audio/sfx_$name (ogg/mp3)")
        throw com.badlogic.gdx.utils.GdxRuntimeException("SFX critique manquant : audio/sfx_$name")
    }

    fun getMap(name: String): TiledMap {
        val path = "maps/$name.tmx"
        return if (manager.isLoaded(path)) manager.get(path, TiledMap::class.java)
        else {
            com.badlogic.gdx.Gdx.app.error("ResourceManager", "Carte non chargée : $path")
            throw com.badlogic.gdx.utils.GdxRuntimeException("Carte critique manquante : $path")
        }
    }

    /**
     * After a zone's assets have been loaded (AssetManager.finishLoading()),
     * ensure map-related textures (requiredAssets and tileset textures) are
     * using ClampToEdge wrap to avoid accidental tiling when stretched.
     */
    fun clampZoneTextures(mapId: String, registry: MapRegistry) {
        val map = registry.getMap(mapId) ?: return
        // Clamp any declared required assets
        map.requiredAssets.forEach { path ->
            try {
                if (path.endsWith(".png", true) && manager.isLoaded(path)) {
                    val tex = manager.get(path, com.badlogic.gdx.graphics.Texture::class.java)
                    tex.setWrap(com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge, com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge)
                }
            } catch (_: Exception) {}
        }

        // Also clamp textures referenced by the TMX (tilesets) if the TMX is loaded
        try {
            val tmxPath = AssetCatalog.Maps.tmx(map.tilemapFile.removePrefix("maps/").removeSuffix(".tmx"))
            if (manager.isLoaded(tmxPath)) {
                val tiledMap = manager.get(tmxPath, TiledMap::class.java)
                // Iterate tilesets and clamp tile textures when possible (use reflection to be robust across libGDX versions)
                for (tileset in tiledMap.tileSets) {
                    try {
                        val it = tileset.iterator()
                        while (it.hasNext()) {
                            val t = it.next()
                            try {
                                var region: TextureRegion? = null
                                try {
                                    val m = t::class.java.getMethod("getTextureRegion")
                                    region = m.invoke(t) as? TextureRegion
                                } catch (_: Exception) {}
                                if (region == null) {
                                    try {
                                        val m2 = t::class.java.getMethod("getRegions")
                                        val arr = m2.invoke(t) as? Array<*>
                                        region = arr?.getOrNull(0) as? TextureRegion
                                    } catch (_: Exception) {}
                                }
                                val tex = region?.texture
                                tex?.setWrap(com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge, com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge)
                            } catch (_: Exception) {}
                        }
                    } catch (_: Exception) {}
                }

                // Also apply subtle tile variations to reduce obvious repetition for large uniform areas
                try { applyTileVariations(tiledMap) } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun applyTileVariations(tiledMap: TiledMap) {
        // Map of base GID -> candidate variant GIDs (simple heuristic)
        val variations = mapOf(
            18 to intArrayOf(18, 19, 20, 21, 22), // deep water variants
            1 to intArrayOf(1,2,3,4,5,6) // grass variants
        )
        val rand = java.util.Random()
        for (layer in tiledMap.layers) {
            if (layer is com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
                val tileLayer = layer as com.badlogic.gdx.maps.tiled.TiledMapTileLayer
                for (x in 0 until tileLayer.width) {
                    for (y in 0 until tileLayer.height) {
                        val cell = tileLayer.getCell(x, y) ?: continue
                        val tile = cell.tile ?: continue
                        val gid = tile.id.toInt()
                        val candidates = variations[gid] ?: continue
                        // small probability to swap to a variant to avoid uniform patterns
                        if (candidates.size > 1 && rand.nextFloat() < 0.18f) {
                            val pick = candidates[rand.nextInt(candidates.size)]
                            if (pick != gid) {
                                try {
                                    val newTile = tiledMap.tileSets.getTile(pick)
                                    if (newTile != null) cell.tile = newTile
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        }
    }
    
    fun <T> get(path: String, type: Class<T>): T = manager.get(path, type)
    fun dispose() { manager.dispose() }

    private fun resolveAudioPathFrom(path: String): String? {
        // path may be audio/music_name.ogg or maps entries; normalize base name
        try {
            val base = path.substringAfterLast('/').substringBeforeLast('.')
            val dir = path.substringBeforeLast('/', "audio")
            val ogg = "$dir/$base.ogg"
            val mp3 = "$dir/$base.mp3"
            val oggFH = com.badlogic.gdx.Gdx.files.internal(ogg)
            if (oggFH.exists()) return ogg
            val mp3FH = com.badlogic.gdx.Gdx.files.internal(mp3)
            if (mp3FH.exists()) return mp3
        } catch (_: Exception) {}
        return null
    }
}
