package com.astralya.ui.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapGroupLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.astralya.AstralYaGame
import com.astralya.game.save.GameStateManager
import com.astralya.game.entities.EnemyFactory
import com.astralya.game.entities.ItemFactory
import com.astralya.game.world.*
import com.astralya.ui.components.*
import com.astralya.engine.utils.*
import com.astralya.engine.core.*
import java.util.Locale
import kotlin.math.pow

class ExplorationScreen(
    private val game: AstralYaGame,
    private val state: GameStateManager
) : Screen {

    private val speed     = 200f
    private val tileSize = 32f

    private var playerX = state.playerX
    private var playerY = state.playerY
    private var isMoving = false

    private var currentMap: GameMap = game.mapRegistry.getMap(state.currentMapId) ?: game.mapRegistry.VILLAGE_DEPART

    private var pixelRegion: TextureRegion? = null
    private var portalTexture: Texture? = null
    private var chestClosedTexture: Texture? = null
    private var chestOpenTexture: Texture? = null
    private var mapBgTexture: Texture? = null
    
    private var mapRenderer: OrthogonalTiledMapRenderer? = null
    private var tiledMap: com.badlogic.gdx.maps.tiled.TiledMap? = null
    private var collisionLayer: TiledMapTileLayer? = null
    
    private val uiCamera = OrthographicCamera(800f, 480f).apply {
        position.set(400f, 240f, 0f)
        update()
    }
    
    private var postProcessShader: ShaderProgram? = null
    private var ambientColor = Color(1f, 1f, 1f, 1f)
    private val mapBaseTint = Color(1f, 1f, 1f, 1f)
    
    private val timeSystem = TimeSystem()
    private val particleManager = ParticleManager()

    private var animation: AnimationComponent? = null
    private val npcAnimations = mutableMapOf<String, AnimationComponent>()
    private val npcInstances = mutableListOf<NPCInstance>()
    private val renderables = mutableListOf<RenderableEntity>() 
    private var stateTime = 0f
    private var playerDirection = Direction.DOWN

    private val tiledNpcs = mutableListOf<NPC>()
    private val tiledChests = mutableListOf<Chest>()
    private val tiledPortals = mutableListOf<Portal>()
    
    private data class TmxVisualObject(val x: Float, val y: Float, val region: TextureRegion)
    private val tmxVisibleObjects = mutableListOf<TmxVisualObject>()

    private val joystickBase   = Vector2(140f, 140f)
    private val joystickKnob   = Vector2(140f, 140f)
    private val joystickRadius = 100f
    private var isTouchingJoy  = false
    private val touchVec       = Vector3()

    private val actionButtonRect = Rectangle(660f, 60f, 100f, 100f)

    companion object {
        private val C_PORTAL   = Color(0.2f, 0.4f, 1f, 0.4f)
        private val C_JOY_BASE = Color(1f, 1f, 1f, 0.25f)
        private val C_JOY_KNOB = Color(1f, 1f, 1f, 0.50f)
        private val C_ACTION_BG = Color(1f, 0.8f, 0.2f, 0.4f)
        private val C_SHADOW   = Color(0f, 0f, 0f, 0.3f)
        private val C_GOLD     = Color(1f, 0.85f, 0.1f, 1f)
        
        private val globalOriginalVisibility = java.util.WeakHashMap<com.badlogic.gdx.maps.tiled.TiledMap, Map<MapLayer, Boolean>>()
    }

    private val sb = StringBuilder(64)
    private val tmpColor = Color()
    private var inputConsumed = false

    private val playerRect = Rectangle()
    private val otherRect  = Rectangle()

    private var showMenu  = false
    private var menuIndex = 0
    private val menuKeys = listOf("menu.inventory", "menu.party", "menu.quests", "menu.save", "menu.return")

    private var showMiniMap = true

    private var encounterTimer = 0f
    private var isTransitioning = false
    private var transitionAlpha = 0f
    private var targetPortal: Portal? = null
    
    private var roofAlpha = 1f
    private var transitionCooldown = 0f
    
    private var lastMapId: String? = null
    private var frameCounter: Long = 0

    private val dialogueBox = DialogueBox()
    private val notificationSystem = NotificationSystem()
    private val profiler = DebugProfiler(game)

    override fun show() {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelRegion = TextureRegion(Texture(pixmap))
        pixmap.dispose()

        try {
            game.resourceManager.loadZone(currentMap.id, game.mapRegistry)
            game.resourceManager.finishLoading()
            loadMap()
            loadParticles()
            
            game.uiManager.clear()
            game.uiManager.addHUD(notificationSystem)
            game.uiManager.addHUD(dialogueBox)
            game.uiManager.addHUD(profiler)
            
            dialogueBox.onFinished = {
                npcInstances.forEach { it.isInteracting = false }
            }
        } catch (e: Exception) {
            Gdx.app.error("AstralYa", "CRASH INIT Exploration: ${e.message}")
        }

        val charLayers = mutableListOf<Texture>()
        try { charLayers.add(game.resourceManager.getTexture("nassim")) } catch(_: Exception) {}
        if (charLayers.isNotEmpty()) animation = AnimationComponent(charLayers)

        portalTexture = try { game.resourceManager.getTexture("portal") } catch(_: Exception) { null }
        chestClosedTexture = try { game.resourceManager.getTexture("chest_closed") } catch(_: Exception) { null }
        chestOpenTexture = try { game.resourceManager.getTexture("chest_open") } catch(_: Exception) { null }
        
        playZoneMusic()
    }

    private val originalVisibility = mutableMapOf<MapLayer, Boolean>()

    private fun loadMap() {
        if (lastMapId == currentMap.id && mapRenderer != null) return
        lastMapId = currentMap.id
        
        mapRenderer?.dispose()
        tiledNpcs.clear(); tiledChests.clear(); tiledPortals.clear(); tmxVisibleObjects.clear()
        originalVisibility.clear(); npcAnimations.clear(); npcInstances.clear(); particleManager.clear()
        
        mapBgTexture = currentMap.visualBg?.let { path ->
            try {
                val tex = game.resourceManager.get(path, Texture::class.java)
                // Ensure background texture does not repeat unexpectedly when stretched across the map
                try { tex.setWrap(com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge, com.badlogic.gdx.graphics.Texture.TextureWrap.ClampToEdge) } catch (_: Exception) {}
                tex
            } catch(_: Exception) { null }
        }

        when (currentMap.id) {
            "grotte_cristal"  -> mapBaseTint.set(0.6f, 0.7f, 1.0f, 1f)
            "desert_oublie"   -> mapBaseTint.set(1.1f, 0.9f, 0.7f, 1f)
            "chateau_morvax"  -> mapBaseTint.set(0.7f, 0.5f, 0.8f, 1f)
            "temple_etoiles"  -> mapBaseTint.set(0.9f, 0.9f, 1.1f, 1f)
            else              -> mapBaseTint.set(1.0f, 1.0f, 1.0f, 1f)
        }
        
        if (currentMap.id == "foret_enchantee") particleManager.spawn("fireflies", 400f, 300f)
        game.camera.zoom = if (currentMap.id.contains("interieur") || currentMap.id.contains("maison")) 0.8f else 1.0f

        tiledMap = try { game.resourceManager.getMap(currentMap.tilemapFile.removePrefix("maps/").removeSuffix(".tmx")) } catch (_: Exception) { null }
        val map = tiledMap

        if (map != null) {
            mapRenderer = OrthogonalTiledMapRenderer(map, 1f, game.batch)
            collisionLayer = map.layers.get("Collisions") as? TiledMapTileLayer
            
            // DATA AUDIT: Check first row of GIDs
            val firstLayer = map.layers.get(0) as? TiledMapTileLayer
            if (firstLayer != null) {
                val sbAudit = StringBuilder()
                for (x in 0 until Math.min(10, firstLayer.width)) {
                    val gid = firstLayer.getCell(x, 0)?.tile?.id ?: 0
                    sbAudit.append("$gid,")
                }
                Gdx.app.log("ASTRA_DATA_AUDIT", "Layer 0 Row 0 GIDs: $sbAudit")
            }

            if (!globalOriginalVisibility.containsKey(map)) {
                val backup = mutableMapOf<MapLayer, Boolean>()
                backupVisibilityRecursive(map.layers, backup)
                globalOriginalVisibility[map] = backup
            }
            originalVisibility.clear()
            originalVisibility.putAll(globalOriginalVisibility[map] ?: emptyMap())

            // Centralized clamp: ask ResourceManager to clamp map-related textures now that the map is loaded
            try { game.resourceManager.clampZoneTextures(currentMap.id, game.mapRegistry) } catch (_: Exception) {}

            map.layers.forEach { layer ->
                if (layer.name?.contains("Visual", true) == true || layer.name?.contains("Decoration", true) == true) {
                    layer.objects.forEach { obj -> if (obj is TextureMapObject) tmxVisibleObjects.add(TmxVisualObject(obj.x, obj.y, obj.textureRegion)) }
                }
            }
        }
    }

    private fun isCollision(x: Float, y: Float): Boolean {
        val mapW = currentMap.widthTiles * tileSize; val mapH = currentMap.heightTiles * tileSize
        if (x < 16f || y < 16f || x > mapW - 16f || y > mapH - 16f) return true
        val r = 8f
        return checkPoint(x, y) || checkPoint(x - r, y) || checkPoint(x + r, y) || checkPoint(x, y - r) || checkPoint(x, y + r)
    }

    private fun checkPoint(x: Float, y: Float): Boolean {
        tiledMap?.layers?.get("ElevationTriggers")?.objects?.forEach { obj ->
            val objElev = obj.properties.get("elevation", -1, Int::class.java)
            if (objElev != -1 && obj is com.badlogic.gdx.maps.objects.RectangleMapObject && obj.rectangle.contains(x, y)) {
                if (state.elevation != objElev) state.elevation = objElev
            }
        }
        for (door in currentMap.doors) {
            if (!(door.requiredSwitchIds.isEmpty() || door.requiredSwitchIds.all { state.getSwitchState(it) })) {
                otherRect.set(door.position.x - 16f, door.position.y - 16f, 32f, 32f)
                if (otherRect.contains(x, y)) return true
            }
        }
        tiledMap?.layers?.get("CollisionObjects")?.objects?.forEach { obj ->
            val objElev = obj.properties.get("elevation", state.elevation, Int::class.java)
            if (objElev == state.elevation) {
                if (obj is com.badlogic.gdx.maps.objects.RectangleMapObject && obj.rectangle.contains(x, y)) return true
                if (obj is com.badlogic.gdx.maps.objects.PolygonMapObject && obj.polygon.contains(x, y)) return true
            }
        }
        for (inst in npcInstances) {
            otherRect.set(inst.x - 16f, inst.y - 16f, 32f, 32f)
            if (otherRect.contains(x, y)) return true
        }
        val layer = collisionLayer ?: return false
        val cellX = (x / tileSize).toInt(); val cellY = (y / tileSize).toInt()
        if (cellX < 0 || cellY < 0 || cellX >= layer.width || cellY >= layer.height) return true
        val cell = layer.getCell(cellX, cellY) ?: return false
        return cell.tile.properties.get("elevation", state.elevation, Int::class.java) == state.elevation
    }

    private fun canMoveTo(x: Float, y: Float) = !isCollision(x, y)

    private fun loadParticles() {
        particleManager.loadEffect("fireflies", "particles/fireflies.p", "sprites")
    }

    override fun render(delta: Float) { update(delta); draw(); if (isTransitioning) drawTransition(delta) }

    private fun update(delta: Float) {
        if (isTransitioning) return
        if (transitionCooldown > 0f) transitionCooldown -= delta
        npcInstances.forEach { inst ->
            state.npcStates[inst.data.id] = com.astralya.game.save.NPCState(inst.data.id, inst.x, inst.y, inst.currentTaskIndex, inst.taskTimer)
        }
        com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile.updateAnimationBaseTime()
        updateRoofFading()
        game.uiManager.update(delta); game.weatherSystem.update(delta, game.random); game.shakeManager.update(delta, game.camera)
        if (game.weatherSystem.getParticleName() != null && particleManager.activeCount() < 50) {
            particleManager.spawn(game.weatherSystem.getParticleName()!!, playerX, playerY + 300f)
        }
        inputConsumed = false; timeSystem.update(delta); ambientColor = timeSystem.getAmbientColor(mapBaseTint); particleManager.update(delta)
        animation?.update(delta)
        for (inst in npcInstances) { inst.update(delta); npcAnimations[inst.data.id]?.update(delta) }
        if (isMoving) stateTime += delta else stateTime = 0f
        if (showMenu) handleMenuInput()
        else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) { showMenu = true; return }
            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) showMiniMap = !showMiniMap
            if (game.uiManager.handleInput()) return
            if (Gdx.input.justTouched()) {
                touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                game.viewport.camera = uiCamera; val sc = game.viewport.unproject(touchVec); game.viewport.camera = game.camera
                if (sc.x > game.viewport.worldWidth - 180f && sc.y > game.viewport.worldHeight - 60f) { showMenu = true; return }
            }
            handleMovement(delta)
            if (Gdx.input.justTouched()) {
                touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                game.viewport.camera = uiCamera; val sc = game.viewport.unproject(touchVec); game.viewport.camera = game.camera 
                if (actionButtonRect.contains(sc.x, sc.y)) {
                    if (!inputConsumed) checkNpcInteraction(force = true)
                    if (!inputConsumed) checkChests(force = true)
                    if (!inputConsumed) checkSwitches(force = true)
                    if (!inputConsumed) checkEncounters()
                }
            }
            if (!inputConsumed) { checkNpcInteraction(); checkChests(); checkSwitches(); if (transitionCooldown <= 0f) checkPortals(); checkEncounters(); checkRandomEncounter(delta) }
        }
        val cam = game.viewport.camera as OrthographicCamera
        val mapW = currentMap.widthTiles * tileSize; val mapH = currentMap.heightTiles * tileSize
        val halfW = game.viewport.worldWidth / 2f; val halfH = game.viewport.worldHeight / 2f
        val targetX = if (mapW > game.viewport.worldWidth) playerX.coerceIn(halfW, mapW - halfW) else mapW / 2f
        val targetY = if (mapH > game.viewport.worldHeight) playerY.coerceIn(halfH, mapH - halfH) else mapH / 2f
        val lerpFactor = (1f - 0.001.pow(delta.toDouble()).toFloat()).coerceIn(0.1f, 0.8f)
        cam.position.x = MathUtils.lerp(cam.position.x, targetX, lerpFactor)
        cam.position.y = MathUtils.lerp(cam.position.y, targetY, lerpFactor)
        cam.position.x = Math.round(cam.position.x).toFloat(); cam.position.y = Math.round(cam.position.y).toFloat(); cam.update()
    }

    private fun updateRoofFading() {
        val isUnder = checkUnderRoofRecursive(tiledMap?.layers)
        roofAlpha = MathUtils.lerp(roofAlpha, if (isUnder) 0.2f else 1.0f, 0.15f)
    }

    private fun checkUnderRoofRecursive(layers: com.badlogic.gdx.maps.MapLayers?): Boolean {
        layers?.forEach { layer ->
            if (!layer.isVisible) return@forEach
            if (layer is MapGroupLayer) { if (checkUnderRoofRecursive(layer.layers)) return true }
            else if (layer is TiledMapTileLayer && (layer.name?.contains("Roof", true) == true || layer.name?.contains("Top", true) == true)) {
                val cx = (playerX / tileSize).toInt(); val cy = (playerY / tileSize).toInt()
                if (cx in 0 until layer.width && cy in 0 until layer.height && layer.getCell(cx, cy) != null) return true
            }
        }
        return false
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f; var dy = 0f; isMoving = false; isTouchingJoy = false
        if (Gdx.input.isTouched) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.camera = uiCamera; val sc = game.viewport.unproject(touchVec); game.viewport.camera = game.camera
            if (sc.x < 350f && sc.y < 350f) {
                isTouchingJoy = true
                val dist = joystickBase.dst(sc.x, sc.y); val angle = MathUtils.atan2(sc.y - joystickBase.y, sc.x - joystickBase.x)
                val clampedDist = MathUtils.clamp(dist, 0f, joystickRadius)
                joystickKnob.set(joystickBase.x + MathUtils.cos(angle) * clampedDist, joystickBase.y + MathUtils.sin(angle) * clampedDist)
                val power = clampedDist / joystickRadius
                dx = MathUtils.cos(angle) * speed * delta * power; dy = MathUtils.sin(angle) * speed * delta * power
                if (power > 0.2f) isMoving = true
            }
        } else joystickKnob.set(joystickBase)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { dx = -speed * delta; isMoving = true; playerDirection = Direction.LEFT }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { dx =  speed * delta; isMoving = true; playerDirection = Direction.RIGHT }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    { dy =  speed * delta; isMoving = true; playerDirection = Direction.UP }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { dy = -speed * delta; isMoving = true; playerDirection = Direction.DOWN }
        if (isTouchingJoy && isMoving) playerDirection = if (dx * dx > dy * dy) (if (dx > 0) Direction.RIGHT else Direction.LEFT) else (if (dy > 0) Direction.UP else Direction.DOWN)
        val mapW = currentMap.widthTiles * tileSize; val mapH = currentMap.heightTiles * tileSize
        val nx = (playerX + dx).coerceIn(20f, mapW - 20f); if (canMoveTo(nx, playerY)) playerX = nx
        val ny = (playerY + dy).coerceIn(20f, mapH - 20f); if (canMoveTo(playerX, ny)) playerY = ny
        state.playerX = playerX; state.playerY = playerY
    }

    private fun checkPortals() {
        playerRect.set(playerX - 16f, playerY - 16f, 32f, 32f)
        for (p in currentMap.portals) {
            otherRect.set(p.position.x - 24f, p.position.y - 24f, 48f, 48f)
            if (playerRect.overlaps(otherRect)) { startMapTransition(p); return }
        }
    }

    private fun startMapTransition(portal: Portal) { isTransitioning = true; transitionAlpha = 0f; targetPortal = portal }

    private fun drawTransition(delta: Float) {
        if (targetPortal != null) { transitionAlpha += delta * 2f; if (transitionAlpha >= 1f) { transitionAlpha = 1f; completeMapTransition() } }
        else { transitionAlpha -= delta * 2f; if (transitionAlpha <= 0f) { transitionAlpha = 0f; isTransitioning = false } }
        Gdx.gl.glEnable(GL20.GL_BLEND); game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); game.shapeRenderer.color = Color(0f, 0f, 0f, transitionAlpha)
        game.shapeRenderer.rect(0f, 0f, game.viewport.worldWidth, game.viewport.worldHeight); game.shapeRenderer.end()
    }

    private fun completeMapTransition() {
        val p = targetPortal ?: return
        val nextMap = game.mapRegistry.getMap(p.targetMapId)
        if (nextMap != null) {
            game.resourceManager.loadZone(nextMap.id, game.mapRegistry); game.resourceManager.finishLoading(); game.resourceManager.unloadUnused(nextMap.id, game.mapRegistry)
            currentMap = nextMap; state.currentMapId = nextMap.id; loadMap() 
            var fx = p.targetX; var fy = p.targetY
            if (isCollision(fx, fy)) {
                val off = listOf(-32f, 32f, 0f)
                outer@for(ox in off) for(oy in off) { if (ox == 0f && oy == 0f) continue; if (!isCollision(fx + ox, fy + oy)) { fx += ox; fy += oy; break@outer } }
            }
            playerX = fx; playerY = fy; state.playerX = fx; state.playerY = fy; transitionCooldown = 1.0f; playZoneMusic()
        }
        targetPortal = null
    }

    private fun checkNpcInteraction(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 50f, playerY - 50f, 100f, 100f)
        for (inst in npcInstances) {
            otherRect.set(inst.x - 20f, inst.y - 20f, 40f, 40f)
            if (playerRect.overlaps(otherRect)) {
                val branch = inst.data.branches.find { it.questId != null && state.questProgress[it.questId]?.status == it.requiredStatus } ?: inst.data.branches.firstOrNull()
                if (branch != null) { inst.isInteracting = true; dialogueBox.show(branch.lines) }
                inst.data.questTriggerId?.let { if (!state.isQuestCompleted(it)) state.startQuest(it) }
                inputConsumed = true; return
            }
        }
    }

    private fun checkChests(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 40f, playerY - 40f, 80f, 80f)
        for (chest in currentMap.chests) {
            if (state.isChestOpened(chest.id)) continue
            otherRect.set(chest.position.x - 16f, chest.position.y - 16f, 32f, 32f)
            if (playerRect.overlaps(otherRect)) {
                if (state.addItem(chest.itemId, chest.quantity)) { state.openChest(chest.id); notificationSystem.show("Objet obtenu : ${ItemFactory.getById(chest.itemId)?.name ?: chest.itemId} x${chest.quantity}") }
                else notificationSystem.show("Inventaire trop lourd !")
                inputConsumed = true; return
            }
        }
    }

    private fun checkSwitches(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 40f, playerY - 40f, 80f, 80f)
        for (sw in currentMap.switches) {
            otherRect.set(sw.position.x - 20f, sw.position.y - 20f, 40f, 40f)
            if (playerRect.overlaps(otherRect)) {
                val ns = !state.getSwitchState(sw.id); state.setSwitchState(sw.id, ns); game.audioManager.playSound(game.resourceManager.getSound("menu_select")); notificationSystem.show(if (ns) "Mécanisme activé" else "Mécanisme désactivé"); inputConsumed = true; return
            }
        }
    }

    private fun checkEncounters() {
        if (isTransitioning) return
        playerRect.set(playerX - 16f, playerY - 16f, 32f, 32f)
        for (enc in currentMap.fixedEncounters) {
            if (state.defeatedEncounters.contains(enc.id)) continue
            otherRect.set(enc.position.x - enc.triggerRadius, enc.position.y - enc.triggerRadius, enc.triggerRadius * 2, enc.triggerRadius * 2)
            if (playerRect.overlaps(otherRect)) {
                val actual = enc.enemyIds.mapNotNull { game.dataManager.getEnemy(it) }
                if (actual.isNotEmpty()) { state.defeatedEncounters.add(enc.id); game.screenManager.setScreen(BattleScreen(game, state, actual, currentMap.id, this)); inputConsumed = true; return }
            }
        }
    }

    private fun handleMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) menuIndex = (menuIndex+1)%menuKeys.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))   menuIndex = (menuIndex-1+menuKeys.size)%menuKeys.size
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f); game.viewport.camera = uiCamera; val sc = game.viewport.unproject(touchVec); game.viewport.camera = game.camera
            for (i in menuKeys.indices) {
                val mx = game.viewport.worldWidth/2f - 100f; val my = game.viewport.worldHeight/2f + 100f - i * 50f
                if (sc.x > mx && sc.x < mx + 200f && sc.y < my && sc.y > my - 40f) { if (menuIndex == i) executeMenuAction() else menuIndex = i; return }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.Z)) executeMenuAction()
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) showMenu = false
    }

    private fun executeMenuAction() {
        when(menuIndex) {
            0 -> { showMenu=false; game.screenManager.setScreen(InventoryScreen(game, state, this)) }
            1 -> { showMenu=false; game.screenManager.setScreen(PartyScreen(game, state, this)) }
            2 -> { showMenu=false; game.screenManager.setScreen(QuestLogScreen(game, state, this)) }
            3 -> { showMenu=false; game.screenManager.setScreen(SaveScreen(game, state, SaveScreen.Mode.SAVE, this)) }
            4 -> showMenu = false
        }
    }

    private fun checkRandomEncounter(delta: Float) {
        if (!isMoving || !currentMap.canEncounter) return
        encounterTimer += delta; if (encounterTimer < 0.6f) return
        encounterTimer = 0f; if (game.random.nextBool(currentMap.encounterRate * 0.1f)) {
            val enemies = EnemyFactory.randomEncounterGroup(currentMap.id, game.random); game.screenManager.setScreen(BattleScreen(game, state, enemies, currentMap.id, this))
        }
    }

    private fun playZoneMusic() {
        if (!game.assetsLoaded) return
        val mn = currentMap.musicFile.removePrefix("audio/music_").removeSuffix(".ogg")
        try { game.audioManager.playMusic(game.resourceManager.getMusic(mn)) } catch(_: Exception) {}
        currentMap.ambientFile?.let { path ->
            val an = path.removePrefix("audio/music_").removeSuffix(".ogg")
            try { game.audioManager.startAmbient(game.resourceManager.getMusic(an)) } catch(_: Exception) {}
        } ?: game.audioManager.stopAmbient()
    }

    private fun draw() {
        frameCounter++
        val worldW = game.viewport.worldWidth; val worldH = game.viewport.worldHeight; val weatherTint = game.weatherSystem.getWeatherTint()
        Gdx.gl.glClearColor(mapBaseTint.r * 0.1f * weatherTint.r, mapBaseTint.g * 0.1f * weatherTint.g, mapBaseTint.b * 0.2f * weatherTint.b, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val cam = game.viewport.camera as OrthographicCamera; game.batch.projectionMatrix = cam.combined; tmpColor.set(ambientColor).mul(weatherTint); game.batch.color = tmpColor
        mapBgTexture?.let { val mapW = currentMap.widthTiles * tileSize; val mapH = currentMap.heightTiles * tileSize; game.batch.begin(); game.batch.draw(it, 0f, 0f, mapW, mapH); game.batch.end() }

        // --- RENDU MAP ---
        mapRenderer?.let { renderer ->
            renderer.setView(cam)
            
            // Pass 1 : BELOW
            val layersBelow = mutableListOf<Int>()
            tiledMap?.layers?.forEachIndexed { i, layer ->
                val name = layer.name?.lowercase() ?: ""
                val isTech = name.contains("collision") || name.contains("trigger") || name.contains("debug") || name.contains("object") || name.contains("logic")
                val isForeground = name.contains("roof") || name.contains("top") || name.contains("over")
                if (!isTech && !isForeground) layersBelow.add(i)
            }
            game.batch.color = Color.WHITE 
            renderer.render(layersBelow.toIntArray())
            
            // Pass Entities
            game.batch.begin()
            drawEntitiesSorted(game.batch); particleManager.draw(game.batch); game.batch.end()

            // Pass 2 : ABOVE
            val layersAbove = mutableListOf<Int>()
            tiledMap?.layers?.forEachIndexed { i, layer ->
                val name = layer.name?.lowercase() ?: ""
                val isForeground = name.contains("roof") || name.contains("top") || name.contains("over")
                if (isForeground) layersAbove.add(i)
            }
            tmpColor.set(ambientColor).mul(weatherTint); tmpColor.a = roofAlpha * ambientColor.a
            game.batch.color = tmpColor
            renderer.render(layersAbove.toIntArray())
            game.batch.color = Color.WHITE
        }

        // --- UI ---
        game.batch.shader = null; val uiMatrix = game.batch.projectionMatrix.cpy().setToOrtho2D(0f, 0f, worldW, worldH); game.batch.projectionMatrix = uiMatrix; game.shapeRenderer.projectionMatrix = uiMatrix; game.batch.color = Color.WHITE; game.batch.begin()
        game.fonts.large.color = C_GOLD; game.fonts.large.draw(game.batch, currentMap.name, 27f, worldH - 27f); game.fonts.large.draw(game.batch, currentMap.name, 25f, worldH - 25f)
        game.fonts.normal.setColor(Color.WHITE); sb.setLength(0); sb.append(game.localization.format("ui.gold", state.gold)); game.fonts.normal.draw(game.batch, sb, 25f, worldH - 65f)
        if (showMenu) {
            game.batch.setColor(0f, 0f, 0.1f, 0.85f); game.batch.draw(pixelRegion!!, worldW/2f - 120f, worldH/2f - 150f, 240f, 300f)
            for (i in menuKeys.indices) { val sel = i == menuIndex; game.fonts.medium.setColor(if (sel) Color.GOLD else Color.WHITE); game.fonts.medium.draw(game.batch, game.localization.get(menuKeys[i]), worldW/2f - 100f, worldH/2f + 100f - i * 50f) }
        }
        game.batch.end(); game.uiManager.draw(game.batch, game.shapeRenderer)
        if (!showMenu && !dialogueBox.isVisible) {
            Gdx.gl.glEnable(GL20.GL_BLEND); game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            if (isTouchingJoy) { game.shapeRenderer.color = C_JOY_BASE; game.shapeRenderer.circle(joystickBase.x, joystickBase.y, joystickRadius); game.shapeRenderer.color = C_JOY_KNOB; game.shapeRenderer.circle(joystickKnob.x, joystickKnob.y, 35f) }
            game.shapeRenderer.color = C_ACTION_BG; game.shapeRenderer.circle(actionButtonRect.x + actionButtonRect.width/2f, actionButtonRect.y + actionButtonRect.height/2f, 50f); game.shapeRenderer.end()
        }
        if (showMiniMap && !showMenu && !dialogueBox.isVisible) drawMiniMap(worldW, worldH)
    }

    private fun drawMiniMap(w: Float, h: Float) {
        val mmW = 120f; val mmH = 120f * (currentMap.heightTiles.toFloat() / currentMap.widthTiles).coerceIn(0.5f, 2.0f); val mmX = w - mmW - 20f; val mmY = h - mmH - 60f
        Gdx.gl.glEnable(GL20.GL_BLEND); game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); game.shapeRenderer.color = Color(0f, 0f, 0.1f, 0.7f); game.shapeRenderer.rect(mmX, mmY, mmW, mmH)
        val sx = mmW / (currentMap.widthTiles * tileSize); val sy = mmH / (currentMap.heightTiles * tileSize)
        collisionLayer?.let { l -> for (ty in 0 until l.height) for (tx in 0 until l.width) { if (l.getCell(tx, ty) != null) { game.shapeRenderer.color = Color(0.3f, 0.3f, 0.5f, 0.6f); game.shapeRenderer.rect(mmX + tx * tileSize * sx, mmY + ty * tileSize * sy, tileSize * sx, tileSize * sy) } } }
        game.shapeRenderer.color = Color.YELLOW; for (inst in npcInstances) game.shapeRenderer.circle(mmX + inst.x * sx, mmY + inst.y * sy, 2f)
        game.shapeRenderer.color = Color.WHITE; game.shapeRenderer.circle(mmX + playerX * sx, mmY + playerY * sy, 3f); game.shapeRenderer.end()
    }

    private fun backupVisibilityRecursive(layers: com.badlogic.gdx.maps.MapLayers, dest: MutableMap<MapLayer, Boolean>) {
        layers.forEach { layer -> dest[layer] = layer.isVisible; if (layer is MapGroupLayer) backupVisibilityRecursive(layer.layers, dest) }
    }

    private fun updateLayersVisibility(layers: com.badlogic.gdx.maps.MapLayers?, belowPlayer: Boolean) {
        layers?.forEach { layer ->
            val isOrig = originalVisibility[layer] ?: true
            val name = layer.name?.lowercase(Locale.ROOT) ?: ""
            val isTech = name.contains("collision") || name.contains("trigger") || name.contains("debug") || name.contains("object") || name.contains("logic")
            if (layer is MapGroupLayer) { if (isTech) layer.isVisible = false else { layer.isVisible = isOrig; updateLayersVisibility(layer.layers, belowPlayer) } }
            else {
                val elev = layer.properties.get("elevation", 0, Int::class.java); val isRoof = name.contains("roof") || name.contains("top") || name.contains("over")
                val shouldRender = if (belowPlayer) (elev <= state.elevation && !isRoof && !isTech) else ((elev > state.elevation || isRoof) && !isTech)
                layer.isVisible = isOrig && shouldRender
            }
        }
    }

    private fun drawEntitiesSorted(batch: com.badlogic.gdx.graphics.g2d.SpriteBatch) {
        renderables.clear(); renderables.add(RenderableEntity(playerX, playerY, animation, playerDirection, isMoving, null, null))
        for (inst in npcInstances) renderables.add(RenderableEntity(inst.x, inst.y, npcAnimations[inst.data.id], inst.currentDir, inst.isMoving, inst.data.name, null))
        for (obj in tmxVisibleObjects) renderables.add(RenderableEntity(obj.x + obj.region.regionWidth/2f, obj.y, null, Direction.DOWN, false, null, obj.region))
        renderables.sortByDescending { it.y }
        for (r in renderables) {
            if (r.region != null) batch.draw(r.region, r.x - r.region.regionWidth/2f, r.y)
            else {
                batch.setColor(C_SHADOW.r, C_SHADOW.g, C_SHADOW.b, C_SHADOW.a * ambientColor.a); batch.draw(pixelRegion!!, r.x - 14f, r.y - 18f, 28f, 10f); batch.setColor(Color.WHITE)
                r.anim?.let { it.setState(if (r.isMoving) EntityState.WALK else EntityState.IDLE, r.dir); val frames = it.getKeyFrames(); for (f in frames) batch.draw(f, r.x - 32f, r.y - 32f, 64f, 64f) }
            }
        }
    }

    private data class RenderableEntity(val x: Float, val y: Float, val anim: AnimationComponent?, val dir: Direction, val isMoving: Boolean, val name: String?, val region: TextureRegion?)

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  { state.playerX = playerX; state.playerY = playerY }
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { pixelRegion?.texture?.dispose(); mapRenderer?.dispose(); particleManager.dispose() }
}
