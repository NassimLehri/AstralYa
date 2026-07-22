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

/**
 * ExplorationScreen "BEAU" et STABLE :
 * - Utilisation de textures de fond pour le sol (tiling).
 * - Personnage Nassim texturé.
 * - Joystick visuel clair et réactif.
 * - Ombres portées sous les personnages.
 * - HUD amélioré.
 */
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
    private var heroTexture: Texture? = null
    private var mapRenderer: OrthogonalTiledMapRenderer? = null
    private var collisionLayer: TiledMapTileLayer? = null
    
    // Modern Visuals
    private var postProcessShader: ShaderProgram? = null
    private var ambientColor = Color(1f, 1f, 1f, 1f)
    private val mapBaseTint = Color(1f, 1f, 1f, 1f)
    
    // Time & Particles
    private val timeSystem = TimeSystem()
    private val particleManager = ParticleManager()

    // Animations
    private var animation: AnimationComponent? = null
    private var stateTime = 0f
    private var playerDirection = Direction.DOWN

    // Tiled Objects
    private val tiledNpcs = mutableListOf<NPC>()
    private val tiledChests = mutableListOf<Chest>()
    private val tiledPortals = mutableListOf<Portal>()

    // Joystick
    private val joystickBase   = Vector2(140f, 140f)
    private val joystickKnob   = Vector2(140f, 140f)
    private val joystickRadius = 100f
    private var isTouchingJoy  = false
    private val touchVec       = Vector3()

    // Action Button
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

        loadMap()
        loadShaders()
        loadParticles()
        heroTexture = try { game.assetLoader.getTexture("sprites/nassim.png") } catch(e: Exception) { null }
        heroTexture?.let { animation = AnimationComponent(it) }
        playZoneMusic()
    }

    private fun loadMap() {
        mapRenderer?.dispose()
        tiledNpcs.clear()
        tiledChests.clear()
        tiledPortals.clear()
        particleManager.clear()

        // Update Ambient Light base tint based on map ID
        when (currentMap.id) {
            "grotte_cristal"  -> mapBaseTint.set(0.6f, 0.7f, 1.0f, 1f)
            "desert_oublie"   -> mapBaseTint.set(1.1f, 0.9f, 0.7f, 1f)
            "chateau_morvax"  -> mapBaseTint.set(0.7f, 0.5f, 0.8f, 1f)
            "temple_etoiles"  -> mapBaseTint.set(0.9f, 0.9f, 1.1f, 1f)
            else              -> mapBaseTint.set(1.0f, 1.0f, 1.0f, 1f)
        }
        
        // Spawn fireflies in forest
        if (currentMap.id == "foret_enchantee") {
            particleManager.spawn("fireflies", 400f, 300f)
        }

        val tiledMap = try {
            game.assetLoader.getTiledMap(currentMap.tilemapFile)
        } catch (e: Exception) {
            null
        }

        if (tiledMap != null) {
            mapRenderer = OrthogonalTiledMapRenderer(tiledMap, game.batch)
            collisionLayer = tiledMap.layers.get("Collisions") as? TiledMapTileLayer
            
            // Parse NPCs
            tiledMap.layers.get("NPCs")?.objects?.forEach { obj ->
                val x = obj.properties.get("x", Float::class.java) ?: 0f
                val y = obj.properties.get("y", Float::class.java) ?: 0f
                val name = obj.name ?: "Inconnu"
                val dialogues = mutableListOf<String>()
                for (i in 1..5) {
                    obj.properties.get("dialogue$i", String::class.java)?.let { dialogues.add(it) }
                }
                val questId = obj.properties.get("questId", String::class.java)
                tiledNpcs.add(NPC(obj.name ?: "npc_${obj.hashCode()}", name, Position(x, y), dialogues, questId))
            }

            // Parse Chests
            tiledMap.layers.get("Chests")?.objects?.forEach { obj ->
                val x = obj.properties.get("x", Float::class.java) ?: 0f
                val y = obj.properties.get("y", Float::class.java) ?: 0f
                val itemId = obj.properties.get("itemId", String::class.java) ?: "herbe_soin"
                val quantity = obj.properties.get("quantity", Int::class.java) ?: 1
                tiledChests.add(Chest(obj.name ?: "chest_${obj.hashCode()}", Position(x, y), itemId, quantity))
            }

            // Parse Portals
            tiledMap.layers.get("Portals")?.objects?.forEach { obj ->
                val x = obj.properties.get("x", Float::class.java) ?: 0f
                val y = obj.properties.get("y", Float::class.java) ?: 0f
                val targetMapId = obj.properties.get("targetMapId", String::class.java) ?: "village_depart"
                val targetX = obj.properties.get("targetX", Float::class.java) ?: 100f
                val targetY = obj.properties.get("targetY", Float::class.java) ?: 100f
                tiledPortals.add(Portal(obj.name ?: "portal_${obj.hashCode()}", Position(x, y), targetMapId, targetX, targetY))
            }
        } else {
            collisionLayer = null
        }
    }

    private fun isCollision(x: Float, y: Float): Boolean {
        val layer = collisionLayer ?: return false
        val cellX = (x / TILE_SIZE).toInt()
        val cellY = (y / TILE_SIZE).toInt()
        
        if (cellX < 0 || cellY < 0 || cellX >= layer.width || cellY >= layer.height) return true
        
        return layer.getCell(cellX, cellY) != null
    }

    private fun canMoveTo(x: Float, y: Float): Boolean {
        // On vérifie les 4 coins d'une boîte de collision réduite (16x16 centrée sur les pieds)
        val r = 10f // Rayon de la boîte
        return !isCollision(x - r, y - r) &&
               !isCollision(x + r, y - r) &&
               !isCollision(x - r, y + r) &&
               !isCollision(x + r, y + r)
    }

    private fun loadShaders() {
        val vert = Gdx.files.internal("shaders/default.vert")
        val frag = Gdx.files.internal("shaders/post_process.frag")
        postProcessShader = ShaderProgram(vert, frag)
        if (!postProcessShader!!.isCompiled) {
            Gdx.app.error("AstralYa", "ERREUR SHADER: ${postProcessShader!!.log}")
            postProcessShader = null
        }
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
            
            // Interaction tactile (bouton Action)
            if (Gdx.input.justTouched()) {
                val screenX = Gdx.input.x.toFloat() * (game.viewport.worldWidth / Gdx.graphics.width)
                val screenY = (Gdx.graphics.height - Gdx.input.y.toFloat()) * (game.viewport.worldHeight / Gdx.graphics.height)
                
                if (actionButtonRect.contains(screenX, screenY)) {
                    if (!inputConsumed) checkNpcInteraction(force = true)
                    if (!inputConsumed) checkChests(force = true)
                }
            }

            if (!inputConsumed) checkNpcInteraction()
            if (!inputConsumed) checkChests()
            if (!inputConsumed) checkPortals()
            checkRandomEncounter(delta)
        }
        
        // Mise à jour de la caméra pour suivre le joueur avec limites (LISSAGE)
        val cam = game.viewport.camera as OrthographicCamera
        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        val halfW = game.viewport.worldWidth / 2f
        val halfH = game.viewport.worldHeight / 2f
        
        val targetX = playerX.coerceIn(halfW, mapW - halfW)
        val targetY = playerY.coerceIn(halfH, mapH - halfH)
        
        // Interpolation linéaire pour un suivi fluide
        cam.position.x = MathUtils.lerp(cam.position.x, targetX, 0.12f)
        cam.position.y = MathUtils.lerp(cam.position.y, targetY, 0.12f)
        cam.update()
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f; var dy = 0f
        isMoving = false
        isTouchingJoy = false

        // Joystick tactile (gauche de l'écran) - Utilisation de coordonnées écran brutes pour l'UI
        if (Gdx.input.isTouched) {
            // On projette sur un plan 2D statique pour l'UI
            val screenX = Gdx.input.x.toFloat() * (game.viewport.worldWidth / Gdx.graphics.width)
            val screenY = (Gdx.graphics.height - Gdx.input.y.toFloat()) * (game.viewport.worldHeight / Gdx.graphics.height)

            if (screenX < 350f && screenY < 350f) {
                isTouchingJoy = true
                val dist = joystickBase.dst(screenX, screenY)
                val angle = MathUtils.atan2(screenY - joystickBase.y, screenX - joystickBase.x)
                val clampedDist = MathUtils.clamp(dist, 0f, joystickRadius)
                
                joystickKnob.set(joystickBase.x + MathUtils.cos(angle) * clampedDist, 
                                 joystickBase.y + MathUtils.sin(angle) * clampedDist)
                
                val power = clampedDist / joystickRadius
                dx = MathUtils.cos(angle) * SPEED * delta * power
                dy = MathUtils.sin(angle) * SPEED * delta * power
                if (power > 0.2f) isMoving = true
            } else if (Gdx.input.justTouched() && screenX > game.viewport.worldWidth - 150f && screenY > game.viewport.worldHeight - 80f) {
                showMenu = true
            }
        } else {
            joystickKnob.set(joystickBase)
        }

        // Support Clavier
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { dx = -SPEED * delta; isMoving = true; playerDirection = Direction.LEFT }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { dx =  SPEED * delta; isMoving = true; playerDirection = Direction.RIGHT }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    { dy =  SPEED * delta; isMoving = true; playerDirection = Direction.UP }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { dy = -SPEED * delta; isMoving = true; playerDirection = Direction.DOWN }
        
        // Direction Joystick
        if (isTouchingJoy && isMoving) {
            if (dx * dx > dy * dy) {
                playerDirection = if (dx > 0) Direction.RIGHT else Direction.LEFT
            } else {
                playerDirection = if (dy > 0) Direction.UP else Direction.DOWN
            }
        }

        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        
        // Test X
        val nextX = (playerX + dx).coerceIn(20f, mapW - 20f)
        if (canMoveTo(nextX, playerY)) {
            playerX = nextX
        }
        
        // Test Y
        val nextY = (playerY + dy).coerceIn(20f, mapH - 20f)
        if (canMoveTo(playerX, nextY)) {
            playerY = nextY
        }

        state.playerX = playerX; state.playerY = playerY
    }

    private fun checkPortals() {
        playerRect.set(playerX - 16f, playerY - 16f, 32f, 32f)
        val portals = if (tiledPortals.isNotEmpty()) tiledPortals else currentMap.portals
        for (p in portals) {
            otherRect.set(p.position.x - 24f, p.position.y - 24f, 48f, 48f)
            if (playerRect.overlaps(otherRect)) {
                val nextMap = MapRegistry.getMap(p.targetMapId)
                if (nextMap != null) {
                    currentMap = nextMap
                    state.currentMapId = nextMap.id
                    playerX = p.targetX; playerY = p.targetY
                    state.playerX = playerX; state.playerY = playerY
                    loadMap()
                    playZoneMusic()
                    return
                }
            }
        }
    }

    private fun checkNpcInteraction(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 50f, playerY - 50f, 100f, 100f)
        val npcs = if (tiledNpcs.isNotEmpty()) tiledNpcs else currentMap.npcs
        for (npc in npcs) {
            otherRect.set(npc.position.x - 20f, npc.position.y - 20f, 40f, 40f)
            if (playerRect.overlaps(otherRect)) {
                dialogueLines = npc.dialogues
                dialogueActive = true; dialogueIndex = 0
                npc.questId?.let { if (!state.isQuestCompleted(it)) state.startQuest(it) }
                inputConsumed = true
                return
            }
        }
    }

    private fun checkChests(force: Boolean = false) {
        if (!force && !(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 40f, playerY - 40f, 80f, 80f)
        val chests = if (tiledChests.isNotEmpty()) tiledChests else currentMap.chests
        for (chest in chests) {
            if (state.isChestOpened(chest.id)) continue
            otherRect.set(chest.position.x - 16f, chest.position.y - 16f, 32f, 32f)
            if (playerRect.overlaps(otherRect)) {
                state.addItem(chest.itemId, chest.quantity); state.openChest(chest.id)
                dialogueLines = listOf("Vous avez trouvé : ${ItemFactory.getById(chest.itemId)?.name ?: chest.itemId} x${chest.quantity}")
                dialogueActive = true; dialogueIndex = 0
                inputConsumed = true
                return
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
        
        // Touch in menu
        if (Gdx.input.justTouched()) {
            val screenX = Gdx.input.x.toFloat() * (game.viewport.worldWidth / Gdx.graphics.width)
            val screenY = (Gdx.graphics.height - Gdx.input.y.toFloat()) * (game.viewport.worldHeight / Gdx.graphics.height)
            val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
            
            for (i in menuItems.indices) {
                val menuX = W/2f - 100f
                val menuY = H/2f + 100f - i * 50f
                if (screenX > menuX && screenX < menuX + 200f && screenY < menuY && screenY > menuY - 40f) {
                    if (menuIndex == i) {
                        executeMenuAction()
                        return
                    } else {
                        menuIndex = i
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            executeMenuAction()
        }
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
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Rendu du monde avec Ambient Light
        game.batch.projectionMatrix = game.viewport.camera.combined
        game.batch.color = ambientColor
        
        mapRenderer?.let {
            it.setView(game.viewport.camera as OrthographicCamera)
            it.render()
        }

        game.batch.begin()

        // Grille légère pour la structure (en coordonnées monde)
        game.batch.setColor(0f, 0f, 0f, 0.15f)
        for (tx in 0..currentMap.widthTiles) {
            game.batch.draw(pixelRegion!!, tx * TILE_SIZE, 0f, 1f, currentMap.heightTiles * TILE_SIZE)
        }
        for (ty in 0..currentMap.heightTiles) {
            game.batch.draw(pixelRegion!!, 0f, ty * TILE_SIZE, currentMap.widthTiles * TILE_SIZE, 1f)
        }

        // Portails
        game.batch.setColor(C_PORTAL)
        val portals = if (tiledPortals.isNotEmpty()) tiledPortals else currentMap.portals
        for (p in portals) {
            game.batch.draw(pixelRegion!!, p.position.x - 24f, p.position.y - 24f, 48f, 48f)
        }

        // Coffres
        val chests = if (tiledChests.isNotEmpty()) tiledChests else currentMap.chests
        for (c in chests) {
            val opened = state.isChestOpened(c.id)
            game.batch.setColor(if (opened) Color.GRAY else Color.GOLD)
            game.batch.draw(pixelRegion!!, c.position.x - 12f, c.position.y - 12f, 24f, 24f)
            
            // Marqueur d'interaction pour coffre
            if (!opened && Vector2.dst(playerX, playerY, c.position.x, c.position.y) < 70f) {
                game.fonts.medium.setColor(Color.YELLOW)
                game.fonts.medium.draw(game.batch, "[!]", c.position.x - 10f, c.position.y + 35f + MathUtils.sin(stateTime * 6f) * 4f)
            }
        }

        // PNJ
        val npcs = if (tiledNpcs.isNotEmpty()) tiledNpcs else currentMap.npcs
        for (npc in npcs) {
            game.batch.setColor(C_SHADOW)
            game.batch.draw(pixelRegion!!, npc.position.x - 12f, npc.position.y - 18f, 24f, 8f)
            game.batch.setColor(Color.ORANGE)
            game.batch.draw(pixelRegion!!, npc.position.x - 12f, npc.position.y - 12f, 24f, 32f)
            
            val dist = Vector2.dst(playerX, playerY, npc.position.x, npc.position.y)
            if (dist < 100f) {
                game.fonts.small.setColor(Color.WHITE)
                game.fonts.small.draw(game.batch, npc.name, npc.position.x - 40f, npc.position.y + 45f)
            }
            // Marqueur d'interaction pour PNJ
            if (dist < 80f) {
                game.fonts.medium.setColor(Color.YELLOW)
                game.fonts.medium.draw(game.batch, "[!]", npc.position.x - 10f, npc.position.y + 65f + MathUtils.sin(stateTime * 6f) * 4f)
            }
        }

        // Joueur (Nassim)
        game.batch.setColor(C_SHADOW.r * ambientColor.r, C_SHADOW.g * ambientColor.g, C_SHADOW.b * ambientColor.b, C_SHADOW.a)
        game.batch.draw(pixelRegion!!, playerX - 14f, playerY - 18f, 28f, 10f)
        game.batch.setColor(Color.WHITE)
        
        animation?.let {
            val frame = it.getKeyFrame(stateTime, playerDirection, isMoving)
            game.batch.draw(frame, playerX - 32f, playerY - 32f, 64f, 64f)
        } ?: run {
            if (heroTexture != null) {
                game.batch.draw(heroTexture, playerX - 24f, playerY - 24f, 48f, 48f)
            } else {
                game.batch.setColor(Color.CYAN)
                game.batch.draw(pixelRegion!!, playerX - 15f, playerY - 15f, 30f, 30f)
            }
        }
        
        particleManager.draw(game.batch)
        
        game.batch.end()

        // Rendu de l'UI (en coordonnées écran fixes)
        val uiMatrix = game.batch.projectionMatrix.cpy().setToOrtho2D(0f, 0f, W, H)
        game.batch.projectionMatrix = uiMatrix
        game.shapeRenderer.projectionMatrix = uiMatrix
        game.batch.color = Color.WHITE // Reset color for UI

        // Apply Post-Process Shader if compiled
        val oldShader = game.batch.shader
        if (postProcessShader != null) {
            game.batch.shader = postProcessShader
        }
        
        game.batch.begin()
        // HUD Textuel
        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, currentMap.name, 25f, H - 25f)
        game.fonts.normal.setColor(Color.WHITE)
        sb.setLength(0); sb.append("Or: ").append(state.gold)
        game.fonts.normal.draw(game.batch, sb, 25f, H - 65f)
        
        game.fonts.tiny.setColor(Color.WHITE)
        game.fonts.tiny.draw(game.batch, timeSystem.getTimeString(), 25f, H - 95f)
        
        game.fonts.normal.draw(game.batch, "[ MENU ]", W - 150f, H - 25f)

        // Dialogues
        if (dialogueActive && dialogueIndex < dialogueLines.size) {
            game.batch.setColor(0f, 0f, 0.2f, 0.9f)
            game.batch.draw(pixelRegion!!, 40f, 30f, W - 80f, 130f)
            game.fonts.normal.setColor(Color.WHITE)
            game.fonts.normal.draw(game.batch, dialogueLines[dialogueIndex], 60f, 130f, W - 120f, -1, true)
        }

        // In-game Menu
        if (showMenu) {
            game.batch.setColor(0f, 0f, 0.1f, 0.85f)
            game.batch.draw(pixelRegion!!, W/2f - 120f, H/2f - 150f, 240f, 300f)
            for (i in menuItems.indices) {
                val sel = i == menuIndex
                game.fonts.medium.setColor(if (sel) Color.GOLD else Color.WHITE)
                game.fonts.medium.draw(game.batch, menuItems[i], W/2f - 100f, H/2f + 100f - i * 50f)
            }
        }
        game.batch.end()

        // UI Shapes (Joystick & Action Button)
        if (!dialogueActive && !showMenu) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            
            // Joystick
            if (isTouchingJoy) {
                game.shapeRenderer.color = C_JOY_BASE
                game.shapeRenderer.circle(joystickBase.x, joystickBase.y, joystickRadius)
                game.shapeRenderer.color = C_JOY_KNOB
                game.shapeRenderer.circle(joystickKnob.x, joystickKnob.y, 35f)
            }
            
            // Action Button
            game.shapeRenderer.color = C_ACTION_BG
            game.shapeRenderer.circle(actionButtonRect.x + actionButtonRect.width/2f, actionButtonRect.y + actionButtonRect.height/2f, 50f)
            game.shapeRenderer.end()
            
            game.batch.begin()
            game.fonts.medium.setColor(Color.WHITE)
            game.fonts.medium.draw(game.batch, "ACTION", actionButtonRect.x + 10f, actionButtonRect.y + 65f)
            game.batch.end()
        }

        game.fonts.resetColors()
        game.batch.shader = oldShader // Restore original shader
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  { state.playerX = playerX; state.playerY = playerY }
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { 
        pixelRegion?.texture?.dispose() 
        mapRenderer?.dispose()
        postProcessShader?.dispose()
        particleManager.dispose()
    }
}
