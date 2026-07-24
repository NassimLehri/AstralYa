package com.astralya.screens

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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.astralya.AstralYaGame
import com.astralya.data.GameState
import com.astralya.entities.EnemyFactory
import com.astralya.entities.ItemFactory
import com.astralya.map.MapRegistry
import com.astralya.map.GameMap
import com.astralya.map.NPC
import com.astralya.map.Chest
import com.astralya.map.Portal
import com.astralya.map.Position
import com.astralya.utils.AnimationComponent
import com.astralya.utils.Direction
import com.astralya.utils.TimeSystem
import com.astralya.utils.ParticleManager

class ExplorationScreen(
    private val game: AstralYaGame,
    private val state: GameState
) : Screen {

    private val SPEED     = 200f
    private val TILE_SIZE = 32f

    private var playerX = state.playerX
    private var playerY = state.playerY
    private var isMoving = false

    private var currentMap: GameMap = MapRegistry.getMap(state.currentMapId) ?: MapRegistry.VILLAGE_DEPART

    private var pixelRegion: TextureRegion? = null
    private var portalTexture: Texture? = null
    private var chestClosedTexture: Texture? = null
    private var chestOpenTexture: Texture? = null
    private var mapBgTexture: Texture? = null
    
    private var mapRenderer: OrthogonalTiledMapRenderer? = null
    private var collisionLayer: TiledMapTileLayer? = null
    
    private val baseLayerIndices = mutableListOf<Int>()
    private val overLayerIndices = mutableListOf<Int>()

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
    private var stateTime = 0f
    private var playerDirection = Direction.DOWN

    private val tiledNpcs = mutableListOf<NPC>()
    private val tiledChests = mutableListOf<Chest>()
    private val tiledPortals = mutableListOf<Portal>()

    private val joystickBase   = Vector2(140f, 140f)
    private val joystickKnob   = Vector2(140f, 140f)
    private val joystickRadius = 100f
    private var isTouchingJoy  = false
    private val touchVec       = Vector3()

    private val actionButtonRect = Rectangle(660f, 60f, 100f, 100f)

    companion object {
        private val C_NPC_HINT = Color(1f, 1f, 0.4f, 1f)
        private val C_PORTAL   = Color(0.2f, 0.4f, 1f, 0.4f)
        private val C_JOY_BASE = Color(1f, 1f, 1f, 0.25f)
        private val C_JOY_KNOB = Color(1f, 1f, 1f, 0.50f)
        private val C_ACTION_BG = Color(1f, 0.8f, 0.2f, 0.4f)
        private val C_SHADOW   = Color(0f, 0f, 0f, 0.3f)
        private val C_GOLD     = Color(1f, 0.85f, 0.1f, 1f)
    }

    private val sb = StringBuilder(64)
    private var inputConsumed = false

    private val playerRect = Rectangle()
    private val otherRect  = Rectangle()

    private var dialogueActive = false
    private var dialogueLines: List<String> = emptyList()
    private var dialogueIndex  = 0

    private var encounterTimer = 0f
    private var stepsSinceEncounter = 0

    private var showMenu  = false
    private var menuIndex = 0
    private val menuItems = listOf("Inventaire", "Équipe", "Quêtes", "Sauvegarder", "Retour")

    override fun show() {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelRegion = TextureRegion(Texture(pixmap))
        pixmap.dispose()

        try {
            loadMap()
            loadShaders()
            loadParticles()
        } catch (e: Exception) {
            com.badlogic.gdx.Gdx.app.error("AstralYa", "CRASH INIT Exploration: ${e.message}")
        }

        // LOAD PLAYER LAYERS
        val layers = mutableListOf<Texture>()
        try { game.assetLoader.getTexture("sprites/nassim.png").let { layers.add(it) } } catch(e: Exception) {}
        
        if (layers.isNotEmpty()) {
            animation = AnimationComponent(layers)
        }

        portalTexture = try { game.assetLoader.getTexture("sprites/portal.png") } catch(e: Exception) { null }
        chestClosedTexture = try { game.assetLoader.getTexture("sprites/chest_closed.png") } catch(e: Exception) { null }
        chestOpenTexture = try { game.assetLoader.getTexture("sprites/chest_open.png") } catch(e: Exception) { null }
        
        playZoneMusic()
    }

    private fun loadMap() {
        mapRenderer?.dispose()
        tiledNpcs.clear()
        tiledChests.clear()
        tiledPortals.clear()
        npcAnimations.clear()
        particleManager.clear()
        baseLayerIndices.clear()
        overLayerIndices.clear()
        
        // Load Background Texture if specified
        mapBgTexture = currentMap.visualBg?.let { path ->
            try { game.assetLoader.getTexture(path) } catch(e: Exception) { null }
        }

        when (currentMap.id) {
            "grotte_cristal"  -> mapBaseTint.set(0.6f, 0.7f, 1.0f, 1f)
            "desert_oublie"   -> mapBaseTint.set(1.1f, 0.9f, 0.7f, 1f)
            "chateau_morvax"  -> mapBaseTint.set(0.7f, 0.5f, 0.8f, 1f)
            "temple_etoiles"  -> mapBaseTint.set(0.9f, 0.9f, 1.1f, 1f)
            else              -> mapBaseTint.set(1.0f, 1.0f, 1.0f, 1f)
        }
        
        if (currentMap.id == "foret_enchantee") {
            particleManager.spawn("fireflies", 400f, 300f)
        }

        // Indoor zoom adjustment
        if (currentMap.id.contains("interieur") || currentMap.id.contains("maison")) {
            game.camera.zoom = 0.8f
        } else {
            game.camera.zoom = 1.0f
        }

        val tiledMap = try { game.assetLoader.getTiledMap(currentMap.tilemapFile) } catch (e: Exception) { null }

        if (tiledMap != null) {
            mapRenderer = OrthogonalTiledMapRenderer(tiledMap, game.batch)
            collisionLayer = tiledMap.layers.get("Collisions") as? TiledMapTileLayer
            
            tiledMap.layers.forEachIndexed { index, layer ->
                if (layer is TiledMapTileLayer) {
                    val name = layer.name ?: ""
                    when {
                        name.equals("Collisions", true) -> { /* Skip */ }
                        name.startsWith("Over", true) || 
                        name.startsWith("Top", true)  || 
                        name.startsWith("Foreground", true) || 
                        name.startsWith("Roof", true) -> overLayerIndices.add(index)
                        else -> baseLayerIndices.add(index)
                    }
                }
            }
        }

        // LOAD NPC LAYERS
        currentMap.npcs.forEach { npc ->
            val nLayers = mutableListOf<Texture>()
            val baseTexPath = npc.spritePath ?: "sprites/male_walkcycle.png"
            try { nLayers.add(game.assetLoader.getTexture(baseTexPath)) } catch(e: Exception) {}
            
            // Only add extra layers for generic walkcycles, NOT for unique sprites like lwiz/yasmine
            if (baseTexPath.contains("male_walkcycle")) {
                try { nLayers.add(game.assetLoader.getTexture("sprites/male_pants.png")) } catch(e: Exception) {}
                try { nLayers.add(game.assetLoader.getTexture("sprites/hairmale.png")) } catch(e: Exception) {}
            } else if (baseTexPath.contains("female_walkcycle")) {
                try { nLayers.add(game.assetLoader.getTexture("sprites/hairfemale.png")) } catch(e: Exception) {}
            }

            if (nLayers.isNotEmpty()) {
                npcAnimations[npc.id] = AnimationComponent(nLayers)
            }
        }
    }

    private fun isCollision(x: Float, y: Float): Boolean {
        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        // Boundary check
        if (x < 16f || y < 16f || x > mapW - 16f || y > mapH - 16f) return true
        
        val layer = collisionLayer ?: return false
        val cellX = (x / TILE_SIZE).toInt()
        val cellY = (y / TILE_SIZE).toInt()
        if (cellX < 0 || cellY < 0 || cellX >= layer.width || cellY >= layer.height) return true
        return layer.getCell(cellX, cellY) != null
    }

    private fun canMoveTo(x: Float, y: Float): Boolean {
        val r = 10f 
        return !isCollision(x - r, y - r) && !isCollision(x + r, y - r) &&
               !isCollision(x - r, y + r) && !isCollision(x + r, y + r)
    }

    private fun loadShaders() {
        val vert = Gdx.files.internal("shaders/default.vert")
        val frag = Gdx.files.internal("shaders/post_process.frag")
        postProcessShader = ShaderProgram(vert, frag)
    }

    private fun loadParticles() {
        particleManager.loadEffect("fireflies", "particles/fireflies.p", "sprites")
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    private fun update(delta: Float) {
        inputConsumed = false
        timeSystem.update(delta)
        ambientColor = timeSystem.getAmbientColor(mapBaseTint)
        particleManager.update(delta)
        
        if (isMoving) stateTime += delta else stateTime = 0f

        if (dialogueActive) handleDialogueInput()
        else if (showMenu) handleMenuInput()
        else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
                showMenu = true
                return
            }
            handleMovement(delta)
            
            if (Gdx.input.justTouched()) {
                touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                game.viewport.camera = uiCamera
                val screenCoords = game.viewport.unproject(touchVec)
                game.viewport.camera = game.camera 
                
                if (actionButtonRect.contains(screenCoords.x, screenCoords.y)) {
                    if (!inputConsumed) checkNpcInteraction(force = true)
                    if (!inputConsumed) checkChests(force = true)
                }
            }

            if (!inputConsumed) checkNpcInteraction()
            if (!inputConsumed) checkChests()
            if (!inputConsumed) checkPortals()
            checkRandomEncounter(delta)
        }
        
        val cam = game.viewport.camera as OrthographicCamera
        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        val halfW = game.viewport.worldWidth / 2f
        val halfH = game.viewport.worldHeight / 2f
        
        val targetX = if (mapW > game.viewport.worldWidth) playerX.coerceIn(halfW, mapW - halfW) else mapW / 2f
        val targetY = if (mapH > game.viewport.worldHeight) playerY.coerceIn(halfH, mapH - halfH) else mapH / 2f
        
        cam.position.x = MathUtils.lerp(cam.position.x, targetX, 0.12f)
        cam.position.y = MathUtils.lerp(cam.position.y, targetY, 0.12f)
        cam.update()
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f; var dy = 0f
        isMoving = false; isTouchingJoy = false

        if (Gdx.input.isTouched) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.camera = uiCamera
            val screenCoords = game.viewport.unproject(touchVec)
            game.viewport.camera = game.camera
            
            if (screenCoords.x < 350f && screenCoords.y < 350f) {
                isTouchingJoy = true
                val dist = joystickBase.dst(screenCoords.x, screenCoords.y)
                val angle = MathUtils.atan2(screenCoords.y - joystickBase.y, screenCoords.x - joystickBase.x)
                val clampedDist = MathUtils.clamp(dist, 0f, joystickRadius)
                joystickKnob.set(joystickBase.x + MathUtils.cos(angle) * clampedDist, joystickBase.y + MathUtils.sin(angle) * clampedDist)
                val power = clampedDist / joystickRadius
                dx = MathUtils.cos(angle) * SPEED * delta * power
                dy = MathUtils.sin(angle) * SPEED * delta * power
                if (power > 0.2f) isMoving = true
            }
        } else {
            joystickKnob.set(joystickBase)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { dx = -SPEED * delta; isMoving = true; playerDirection = Direction.LEFT }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { dx =  SPEED * delta; isMoving = true; playerDirection = Direction.RIGHT }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    { dy =  SPEED * delta; isMoving = true; playerDirection = Direction.UP }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { dy = -SPEED * delta; isMoving = true; playerDirection = Direction.DOWN }
        
        if (isTouchingJoy && isMoving) {
            if (dx * dx > dy * dy) playerDirection = if (dx > 0) Direction.RIGHT else Direction.LEFT
            else playerDirection = if (dy > 0) Direction.UP else Direction.DOWN
        }

        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        val nextX = (playerX + dx).coerceIn(20f, mapW - 20f)
        if (canMoveTo(nextX, playerY)) playerX = nextX
        val nextY = (playerY + dy).coerceIn(20f, mapH - 20f)
        if (canMoveTo(playerX, nextY)) playerY = nextY

        state.playerX = playerX; state.playerY = playerY
    }

    private fun checkPortals() {
        playerRect.set(playerX - 16f, playerY - 16f, 32f, 32f)
        for (p in currentMap.portals) {
            otherRect.set(p.position.x - 24f, p.position.y - 24f, 48f, 48f)
            if (playerRect.overlaps(otherRect)) {
                val nextMap = MapRegistry.getMap(p.targetMapId)
                if (nextMap != null) {
                    currentMap = nextMap; state.currentMapId = nextMap.id
                    playerX = p.targetX; playerY = p.targetY
                    state.playerX = playerX; state.playerY = playerY
                    loadMap(); playZoneMusic(); return
                }
            }
        }
    }

    private fun checkNpcInteraction(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 50f, playerY - 50f, 100f, 100f)
        for (npc in currentMap.npcs) {
            otherRect.set(npc.position.x - 20f, npc.position.y - 20f, 40f, 40f)
            if (playerRect.overlaps(otherRect)) {
                dialogueLines = npc.dialogues; dialogueActive = true; dialogueIndex = 0
                npc.questId?.let { if (!state.isQuestCompleted(it)) state.startQuest(it) }
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
                state.addItem(chest.itemId, chest.quantity); state.openChest(chest.id)
                dialogueLines = listOf("Vous avez trouvé : ${ItemFactory.getById(chest.itemId)?.name ?: chest.itemId} x${chest.quantity}")
                dialogueActive = true; dialogueIndex = 0; inputConsumed = true; return
            }
        }
    }

    private fun handleDialogueInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            dialogueIndex++
            if (dialogueIndex >= dialogueLines.size) dialogueActive = false
        }
    }

    private fun handleMenuInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) menuIndex = (menuIndex+1)%menuItems.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))   menuIndex = (menuIndex-1+menuItems.size)%menuItems.size
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.camera = uiCamera
            val screenCoords = game.viewport.unproject(touchVec)
            game.viewport.camera = game.camera
            for (i in menuItems.indices) {
                val menuX = game.viewport.worldWidth/2f - 100f
                val menuY = game.viewport.worldHeight/2f + 100f - i * 50f
                if (screenCoords.x > menuX && screenCoords.x < menuX + 200f && screenCoords.y < menuY && screenCoords.y > menuY - 40f) {
                    if (menuIndex == i) executeMenuAction() else menuIndex = i
                    return
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) executeMenuAction()
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) showMenu = false
    }

    private fun executeMenuAction() {
        when(menuIndex) {
            0 -> { showMenu=false; game.setScreen(InventoryScreen(game, state, this)) }
            1 -> { showMenu=false; game.setScreen(PartyScreen(game, state, this)) }
            2 -> { showMenu=false; game.setScreen(QuestLogScreen(game, state, this)) }
            3 -> { showMenu=false; game.setScreen(SaveScreen(game, state, SaveScreen.Mode.SAVE, this)) }
            4 -> showMenu = false
        }
    }

    private fun checkRandomEncounter(delta: Float) {
        if (!isMoving || !currentMap.canEncounter) return
        encounterTimer += delta
        if (encounterTimer < 0.6f) return
        encounterTimer = 0f
        if (game.random.nextBool(currentMap.encounterRate * 0.1f)) {
            val enemies = EnemyFactory.randomEncounterGroup(currentMap.id, game.random)
            game.setScreen(BattleScreen(game, state, enemies, currentMap.id, this))
        }
    }

    private fun playZoneMusic() {
        if (!game.assetsLoaded) return
        try { game.audioManager.playMusic(game.assetLoader.getMusic(currentMap.musicFile)) } catch(e: Exception) {}
    }

    private fun draw() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        Gdx.gl.glClearColor(mapBaseTint.r * 0.1f, mapBaseTint.g * 0.1f, mapBaseTint.b * 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.batch.color = ambientColor
        
        mapBgTexture?.let {
            val mapW = currentMap.widthTiles * TILE_SIZE
            val mapH = currentMap.heightTiles * TILE_SIZE
            game.batch.begin()
            game.batch.draw(it, 0f, 0f, mapW, mapH)
            game.batch.end()
        }

        mapRenderer?.let {
            it.setView(game.viewport.camera as OrthographicCamera)
            if (mapBgTexture == null && baseLayerIndices.isNotEmpty()) it.render(baseLayerIndices.toIntArray())
            else if (mapBgTexture == null) it.render()
        }

        game.batch.begin()

        for (p in currentMap.portals) {
            if (portalTexture != null) {
                game.batch.setColor(1f, 1f, 1f, ambientColor.a)
                game.batch.draw(portalTexture, p.position.x - 32f, p.position.y - 32f, 64f, 64f)
            } else {
                game.batch.setColor(C_PORTAL)
                game.batch.draw(pixelRegion!!, p.position.x - 24f, p.position.y - 24f, 48f, 48f)
            }
            game.batch.setColor(1f, 1f, 1f, 0.3f * (0.5f + 0.5f * MathUtils.sin(stateTime * 5f)))
            game.batch.draw(pixelRegion!!, p.position.x - 30f, p.position.y - 30f, 60f, 60f)
            game.fonts.tiny.setColor(Color.CYAN)
            game.fonts.tiny.draw(game.batch, "PORTAIL", p.position.x - 30f, p.position.y + 40f)
        }

        for (chest in currentMap.chests) {
            val opened = state.isChestOpened(chest.id)
            game.batch.setColor(C_SHADOW)
            game.batch.draw(pixelRegion!!, chest.position.x - 14f, chest.position.y - 14f, 28f, 10f)
            game.batch.setColor(Color.WHITE)
            if (opened && chestOpenTexture != null) game.batch.draw(chestOpenTexture, chest.position.x - 24f, chest.position.y - 24f, 48f, 48f)
            else if (!opened && chestClosedTexture != null) game.batch.draw(chestClosedTexture, chest.position.x - 24f, chest.position.y - 24f, 48f, 48f)
            else {
                game.batch.setColor(if (opened) Color.GRAY else C_GOLD)
                game.batch.draw(pixelRegion!!, chest.position.x - 12f, chest.position.y - 10f, 24f, 20f)
            }
        }

        for (npc in currentMap.npcs) {
            val dist = Vector2.dst(playerX, playerY, npc.position.x, npc.position.y)
            game.batch.setColor(C_SHADOW.r, C_SHADOW.g, C_SHADOW.b, C_SHADOW.a * ambientColor.a)
            game.batch.draw(pixelRegion!!, npc.position.x - 14f, npc.position.y - 18f, 28f, 10f)
            game.batch.setColor(Color.WHITE)

            val npcAnim = npcAnimations[npc.id]
            if (npcAnim != null) {
                val dir = when {
                    playerY > npc.position.y + 20f -> Direction.UP
                    playerY < npc.position.y - 20f -> Direction.DOWN
                    playerX > npc.position.x -> Direction.RIGHT
                    else -> Direction.LEFT
                }
                val frames = npcAnim.getKeyFrames(stateTime, dir, dist < 50f)
                for (f in frames) game.batch.draw(f, npc.position.x - 32f, npc.position.y - 32f, 64f, 64f)
            } else {
                game.batch.setColor(Color.ORANGE)
                game.batch.draw(pixelRegion!!, npc.position.x - 12f, npc.position.y - 12f, 24f, 32f)
            }
            if (dist < 100f) {
                game.fonts.small.setColor(Color.WHITE)
                game.fonts.small.draw(game.batch, npc.name, npc.position.x - 40f, npc.position.y + 45f)
            }
        }

        game.batch.setColor(C_SHADOW.r, C_SHADOW.g, C_SHADOW.b, C_SHADOW.a * ambientColor.a)
        game.batch.draw(pixelRegion!!, playerX - 14f, playerY - 18f, 28f, 10f)
        game.batch.setColor(Color.WHITE)
        
        animation?.let {
            val frames = it.getKeyFrames(stateTime, playerDirection, isMoving)
            for (f in frames) game.batch.draw(f, playerX - 32f, playerY - 32f, 64f, 64f)
        }
        
        particleManager.draw(game.batch)
        game.batch.end()

        mapRenderer?.let { if (overLayerIndices.isNotEmpty()) it.render(overLayerIndices.toIntArray()) }

        val uiMatrix = game.batch.projectionMatrix.cpy().setToOrtho2D(0f, 0f, W, H)
        game.batch.projectionMatrix = uiMatrix
        game.shapeRenderer.projectionMatrix = uiMatrix
        game.batch.color = Color.WHITE

        if (postProcessShader != null) game.batch.shader = postProcessShader
        game.batch.begin()
        game.fonts.large.setColor(C_GOLD); game.fonts.large.draw(game.batch, currentMap.name, 25f, H - 25f)
        game.fonts.normal.setColor(Color.WHITE); sb.clear(); sb.append("Or: ").append(state.gold); game.fonts.normal.draw(game.batch, sb, 25f, H - 65f)
        game.fonts.normal.draw(game.batch, "[ MENU ]", W - 150f, H - 25f)

        if (dialogueActive && dialogueIndex < dialogueLines.size) {
            game.batch.setColor(0f, 0f, 0.2f, 0.9f); game.batch.draw(pixelRegion!!, 40f, 30f, W - 80f, 130f)
            game.fonts.normal.setColor(Color.WHITE); game.fonts.normal.draw(game.batch, dialogueLines[dialogueIndex], 60f, 130f, W - 120f, -1, true)
        }

        if (showMenu) {
            game.batch.setColor(0f, 0f, 0.1f, 0.85f); game.batch.draw(pixelRegion!!, W/2f - 120f, H/2f - 150f, 240f, 300f)
            for (i in menuItems.indices) {
                val sel = i == menuIndex
                game.fonts.medium.setColor(if (sel) Color.GOLD else Color.WHITE)
                game.fonts.medium.draw(game.batch, menuItems[i], W/2f - 100f, H/2f + 100f - i * 50f)
            }
        }
        game.batch.end()

        if (!dialogueActive && !showMenu) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            if (isTouchingJoy) {
                game.shapeRenderer.color = C_JOY_BASE; game.shapeRenderer.circle(joystickBase.x, joystickBase.y, joystickRadius)
                game.shapeRenderer.color = C_JOY_KNOB; game.shapeRenderer.circle(joystickKnob.x, joystickKnob.y, 35f)
            }
            game.shapeRenderer.color = C_ACTION_BG; game.shapeRenderer.circle(actionButtonRect.x + actionButtonRect.width/2f, actionButtonRect.y + actionButtonRect.height/2f, 50f)
            game.shapeRenderer.end()
            game.batch.begin(); game.fonts.medium.setColor(Color.WHITE); game.fonts.medium.draw(game.batch, "ACTION", actionButtonRect.x + 10f, actionButtonRect.y + 65f); game.batch.end()
        }
        game.fonts.resetColors(); game.batch.shader = null
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  { state.playerX = playerX; state.playerY = playerY }
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { pixelRegion?.texture?.dispose(); mapRenderer?.dispose(); postProcessShader?.dispose(); particleManager.dispose() }
}
