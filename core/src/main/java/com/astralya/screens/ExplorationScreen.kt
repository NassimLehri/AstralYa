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
import com.astralya.AstralYaGame
import com.astralya.data.GameState
import com.astralya.entities.EnemyFactory
import com.astralya.entities.ItemFactory
import com.astralya.map.MapRegistry
import com.astralya.map.GameMap
import com.astralya.map.NPC

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
    private var groundRegion: TextureRegion? = null

    // Joystick
    private val joystickBase   = Vector2(140f, 140f)
    private val joystickKnob   = Vector2(140f, 140f)
    private val joystickRadius = 100f
    private var isTouchingJoy  = false
    private val touchVec       = Vector3()

    companion object {
        private val C_NPC_HINT = Color(1f, 1f, 0.4f, 1f)
        private val C_PORTAL   = Color(0.2f, 0.4f, 1f, 0.4f)
        private val C_JOY_BASE = Color(1f, 1f, 1f, 0.25f)
        private val C_JOY_KNOB = Color(1f, 1f, 1f, 0.50f)
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
    private val menuItems = listOf("Inventaire", "Équipe", "Sauvegarder", "Retour")

    override fun show() {
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelRegion = TextureRegion(Texture(pixmap))
        pixmap.dispose()

        updateGroundTexture()
        playZoneMusic()
    }

    private fun updateGroundTexture() {
        val texName = when (currentMap.id) {
            "village_depart"  -> "sprites/battle_bg_village.png"
            "foret_enchantee" -> "sprites/battle_bg_foret.png"
            "grotte_cristal"  -> "sprites/battle_bg_grotte.png"
            "desert_oublie"   -> "sprites/battle_bg_desert.png"
            "temple_etoiles"  -> "sprites/battle_bg_temple.png"
            "cite_volante"    -> "sprites/battle_bg_cite.png"
            "chateau_morvax"  -> "sprites/battle_bg_chateau.png"
            else              -> "sprites/title_bg.png"
        }
        val tex = try { game.assetLoader.getTexture(texName) } catch (e: Exception) { null }
        if (tex != null) {
            groundRegion = TextureRegion(tex)
        }
    }

    override fun render(delta: Float) {
        update(delta)
        draw()
    }

    private fun update(delta: Float) {
        inputConsumed = false
        if (dialogueActive) handleDialogueInput()
        else if (showMenu) handleMenuInput()
        else {
            handleMovement(delta)
            if (!inputConsumed) checkNpcInteraction()
            if (!inputConsumed) checkChests()
            if (!inputConsumed) checkPortals()
            checkRandomEncounter(delta)
        }
    }

    private fun handleMovement(delta: Float) {
        var dx = 0f; var dy = 0f
        isMoving = false
        isTouchingJoy = false

        // Joystick tactile (gauche de l'écran)
        if (Gdx.input.isTouched) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchVec)

            if (touchVec.x < 350f && touchVec.y < 350f) {
                isTouchingJoy = true
                val dist = joystickBase.dst(touchVec.x, touchVec.y)
                val angle = MathUtils.atan2(touchVec.y - joystickBase.y, touchVec.x - joystickBase.x)
                val clampedDist = MathUtils.clamp(dist, 0f, joystickRadius)
                
                joystickKnob.set(joystickBase.x + MathUtils.cos(angle) * clampedDist, 
                                 joystickBase.y + MathUtils.sin(angle) * clampedDist)
                
                val power = clampedDist / joystickRadius
                dx = MathUtils.cos(angle) * SPEED * delta * power
                dy = MathUtils.sin(angle) * SPEED * delta * power
                if (power > 0.2f) isMoving = true
            } else if (Gdx.input.justTouched() && touchVec.x > game.viewport.worldWidth - 150f && touchVec.y > game.viewport.worldHeight - 80f) {
                showMenu = true
            }
        } else {
            joystickKnob.set(joystickBase)
        }

        // Support Clavier
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  { dx = -SPEED * delta; isMoving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { dx =  SPEED * delta; isMoving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    { dy =  SPEED * delta; isMoving = true }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  { dy = -SPEED * delta; isMoving = true }

        val mapW = currentMap.widthTiles * TILE_SIZE
        val mapH = currentMap.heightTiles * TILE_SIZE
        playerX = (playerX + dx).coerceIn(20f, mapW - 20f)
        playerY = (playerY + dy).coerceIn(20f, mapH - 20f)
        state.playerX = playerX; state.playerY = playerY
    }

    private fun checkPortals() {
        playerRect.set(playerX - 16f, playerY - 16f, 32f, 32f)
        for (p in currentMap.portals) {
            otherRect.set(p.position.x - 24f, p.position.y - 24f, 48f, 48f)
            if (playerRect.overlaps(otherRect)) {
                val nextMap = MapRegistry.getMap(p.targetMapId)
                if (nextMap != null) {
                    currentMap = nextMap
                    state.currentMapId = nextMap.id
                    playerX = p.targetX; playerY = p.targetY
                    state.playerX = playerX; state.playerY = playerY
                    updateGroundTexture()
                    playZoneMusic()
                    return
                }
            }
        }
    }

    private fun checkNpcInteraction() {
        if (!(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 50f, playerY - 50f, 100f, 100f)
        for (npc in currentMap.npcs) {
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

    private fun checkChests() {
        if (!(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) return
        playerRect.set(playerX - 40f, playerY - 40f, 80f, 80f)
        for (chest in currentMap.chests) {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            when(menuIndex) {
                0 -> { showMenu=false; game.setScreen(InventoryScreen(game, state, this)) }
                1 -> { showMenu=false; game.setScreen(PartyScreen(game, state, this)) }
                2 -> { showMenu=false; game.setScreen(SaveScreen(game, state, SaveScreen.Mode.SAVE, this)) }
                3 -> showMenu = false
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) showMenu = false
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

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined

        val camX = playerX - W / 2f; val camY = playerY - H / 2f

        game.batch.begin()
        // Sol texturé
        groundRegion?.let { reg ->
            game.batch.setColor(0.6f, 0.6f, 0.6f, 1f)
            val rows = (currentMap.heightTiles * TILE_SIZE / reg.regionHeight).toInt() + 1
            val cols = (currentMap.widthTiles * TILE_SIZE / reg.regionWidth).toInt() + 1
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val wx = c * reg.regionWidth - camX
                    val wy = r * reg.regionHeight - camY
                    if (wx > W || wy > H || wx < -reg.regionWidth || wy < -reg.regionHeight) continue
                    game.batch.draw(reg, wx, wy)
                }
            }
        }

        // Grille légère pour la structure
        game.batch.setColor(0f, 0f, 0f, 0.15f)
        for (tx in 0..currentMap.widthTiles) {
            game.batch.draw(pixelRegion!!, tx * TILE_SIZE - camX, -camY, 1f, currentMap.heightTiles * TILE_SIZE)
        }
        for (ty in 0..currentMap.heightTiles) {
            game.batch.draw(pixelRegion!!, -camX, ty * TILE_SIZE - camY, currentMap.widthTiles * TILE_SIZE, 1f)
        }

        // Portails
        game.batch.setColor(C_PORTAL)
        for (p in currentMap.portals) {
            game.batch.draw(pixelRegion!!, p.position.x - camX - 24f, p.position.y - camY - 24f, 48f, 48f)
        }

        // Coffres
        for (c in currentMap.chests) {
            val opened = state.isChestOpened(c.id)
            game.batch.setColor(if (opened) Color.GRAY else Color.GOLD)
            game.batch.draw(pixelRegion!!, c.position.x - camX - 12f, c.position.y - camY - 12f, 24f, 24f)
        }

        // PNJ
        for (npc in currentMap.npcs) {
            game.batch.setColor(C_SHADOW)
            game.batch.draw(pixelRegion!!, npc.position.x - camX - 12f, npc.position.y - camY - 18f, 24f, 8f)
            game.batch.setColor(Color.ORANGE)
            game.batch.draw(pixelRegion!!, npc.position.x - camX - 12f, npc.position.y - camY - 12f, 24f, 32f)
            
            if (Vector2.dst(playerX, playerY, npc.position.x, npc.position.y) < 100f) {
                game.fonts.small.setColor(Color.WHITE)
                game.fonts.small.draw(game.batch, npc.name, npc.position.x - camX - 40f, npc.position.y - camY + 45f)
            }
        }

        // Joueur (Nassim)
        game.batch.setColor(C_SHADOW)
        game.batch.draw(pixelRegion!!, playerX - camX - 14f, playerY - camY - 18f, 28f, 10f)
        game.batch.setColor(Color.WHITE)
        val heroTex = try { game.assetLoader.getTexture("sprites/nassim.png") } catch(e: Exception) { null }
        if (heroTex != null) {
            game.batch.draw(heroTex, playerX - camX - 24f, playerY - camY - 24f, 48f, 48f)
        } else {
            game.batch.setColor(Color.CYAN)
            game.batch.draw(pixelRegion!!, playerX - camX - 15f, playerY - camY - 15f, 30f, 30f)
        }

        // HUD Textuel
        game.batch.setColor(Color.WHITE)
        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, currentMap.name, 25f, H - 25f)
        game.fonts.normal.setColor(Color.WHITE)
        sb.setLength(0); sb.append("Or: ").append(state.gold)
        game.fonts.normal.draw(game.batch, sb, 25f, H - 65f)
        
        game.fonts.normal.draw(game.batch, "[ MENU ]", W - 150f, H - 25f)

        // Dialogues
        if (dialogueActive && dialogueIndex < dialogueLines.size) {
            game.batch.setColor(0f, 0f, 0.2f, 0.9f)
            game.batch.draw(pixelRegion!!, 40f, 30f, W - 80f, 130f)
            game.fonts.normal.setColor(Color.WHITE)
            game.fonts.normal.draw(game.batch, dialogueLines[dialogueIndex], 60f, 130f, W - 120f, -1, true)
        }

        game.batch.end()

        // Joystick
        if (isTouchingJoy) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            game.shapeRenderer.color = C_JOY_BASE
            game.shapeRenderer.circle(joystickBase.x, joystickBase.y, joystickRadius)
            game.shapeRenderer.color = C_JOY_KNOB
            game.shapeRenderer.circle(joystickKnob.x, joystickKnob.y, 35f)
            game.shapeRenderer.end()
        }

        game.fonts.resetColors()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  { state.playerX = playerX; state.playerY = playerY }
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { pixelRegion?.texture?.dispose() }
}
