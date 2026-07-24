package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.astralya.AstralYaGame
import com.astralya.data.GameState
import com.astralya.entities.ItemFactory
import com.astralya.entities.ItemType
import com.astralya.map.MapRegistry
import kotlinx.coroutines.*
import java.util.Locale

// ════════════════════════════════════════════════════════════════
// INVENTORY SCREEN
// FIX PERF #2 #3 #4 #8 + Scaling
// ════════════════════════════════════════════════════════════════

class InventoryScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    private val returnScreen: Screen
) : Screen {

    private var selectedIdx = 0
    private var catIdx      = 0
    private val categories  = listOf("Tout", "Consommables", "Armes", "Armures", "Clés")

    private var itemCache: List<Pair<String, Int>> = emptyList()
    private var cacheValid = false
    private var scrollIdx  = 0
    private var elapsed    = 0f

    private val sb = StringBuilder(64)

    companion object {
        private val C_HEADER   = Color(0.1f, 0.1f, 0.3f, 1f)
        private val C_GOLD     = Color(1f, 0.85f, 0.2f, 1f)
        private val C_WHITE    = Color(1f, 1f, 1f, 1f)
        private val C_SEL      = Color(1f, 1f, 1f, 1f)
        private val C_UNSEL    = Color(0.72f, 0.72f, 0.88f, 1f)
        private val C_CAT_SEL  = Color(1f, 1f, 1f, 1f)
        private val C_CAT_UNS  = Color(0.58f, 0.58f, 0.78f, 1f)
        private val C_EMPTY    = Color(0.5f, 0.5f, 0.6f, 1f)
        private val C_DESC     = Color(0.65f, 0.82f, 1f, 1f)
        private val C_STAT     = Color(0.45f, 1f, 0.45f, 1f)
        private val C_VALUE    = Color(0.58f, 0.58f, 0.68f, 1f)
        private val C_HINT     = Color(0.45f, 0.45f, 0.55f, 1f)
    }

    private fun refreshCache() {
        val filter = when (catIdx) {
            1 -> ItemType.CONSUMABLE; 2 -> ItemType.WEAPON
            3 -> ItemType.ARMOR;      4 -> ItemType.KEY_ITEM
            else -> null
        }
        itemCache = state.inventory.entries
            .filter { (id, _) -> filter == null || ItemFactory.getById(id)?.type == filter }
            .map { it.key to it.value }
        cacheValid = true
    }

    override fun show() { 
        game.viewport.camera.position.set(400f, 240f, 0f)
        game.viewport.camera.update()
        refreshCache() 
        elapsed = 0f
    }

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()
        draw()
    }

    private val touchPos = com.badlogic.gdx.math.Vector3()

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.X) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(returnScreen); dispose(); return
        }
        
        // Touch interaction
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchPos)
            val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
            
            // Back button (bottom right)
            if (touchPos.x > W - 150f && touchPos.y < 50f) {
                game.setScreen(returnScreen); dispose(); return
            }
            
            // Categories (top)
            if (touchPos.y > H - 78f) {
                for (i in categories.indices) {
                    val catX = 22f + i * (W / categories.size)
                    if (touchPos.x > catX && touchPos.x < catX + (W/categories.size)) {
                        catIdx = i; cacheValid = false; selectedIdx = 0
                        break
                    }
                }
            }
            
            // Items list
            if (touchPos.y < H - 100f && touchPos.y > 100f) {
                for (i in itemCache.indices) {
                    val itemY = H - 108f - i * 36f
                    if (touchPos.y < itemY && touchPos.y > itemY - 30f) {
                        if (selectedIdx == i) useSelected()
                        else selectedIdx = i
                        break
                    }
                }
            }
        }

        val prevCat = catIdx
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            catIdx = (catIdx - 1 + categories.size) % categories.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            catIdx = (catIdx + 1) % categories.size
        if (catIdx != prevCat) { cacheValid = false; selectedIdx = 0 }
        if (!cacheValid) refreshCache()

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIdx = (selectedIdx + 1).coerceAtMost((itemCache.size - 1).coerceAtLeast(0))
            if (selectedIdx >= scrollIdx + 8) scrollIdx = (selectedIdx - 7).coerceAtLeast(0)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIdx = (selectedIdx - 1).coerceAtLeast(0)
            if (selectedIdx < scrollIdx) scrollIdx = selectedIdx
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            useSelected(); cacheValid = false
        }
    }

    private fun useSelected() {
        val (itemId, _) = itemCache.getOrNull(selectedIdx) ?: return
        val item = ItemFactory.getById(itemId) ?: return
        if (item.type != ItemType.CONSUMABLE) return
        val target = state.party.filter { it.isAlive }.minByOrNull { it.currentHp } ?: return
        when {
            item.hpRestore > 0 -> { target.heal(item.hpRestore);    state.removeItem(itemId) }
            item.mpRestore > 0 -> { target.restoreMp(item.mpRestore); state.removeItem(itemId) }
        }
    }

    private fun draw() {
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined

        // Fond sombre
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color(0.04f, 0.04f, 0.12f, 1f)
        game.shapeRenderer.rect(0f, 0f, W, H)
        
        // Header
        game.shapeRenderer.color = C_HEADER
        game.shapeRenderer.rect(0f, H - 78f, W, 78f)
        game.shapeRenderer.end()

        game.batch.begin()
        
        // UI Frame
        game.batch.draw(game.assetLoader.getTexture("sprites/ui_frame.png"), 10f, 10f, W - 20f, H - 20f)

        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, "INVENTAIRE", 35f, H - 35f)

        game.fonts.normal.setColor(C_GOLD)
        sb.clear(); sb.append("Or: ").append(state.gold)
        game.fonts.normal.draw(game.batch, sb, W - 175f, H - 20f)

        for (i in categories.indices) {
            val sel = i == catIdx
            val pulse = if (sel) 0.7f + MathUtils.sin(elapsed * 5f) * 0.3f else 1f
            val fnt = if (sel) game.fonts.normal else game.fonts.small
            fnt.setColor(pulse, pulse, if (sel) 0.4f else pulse, 1f)
            fnt.draw(game.batch, categories[i], 22f + i * (W / categories.size), H - 56f)
        }

        if (itemCache.isEmpty()) {
            game.fonts.normal.setColor(C_EMPTY)
            game.fonts.normal.draw(game.batch, "Aucun objet.", 38f, H - 115f)
        } else {
            val visibleCount = 8
            for (i in scrollIdx until (scrollIdx + visibleCount).coerceAtMost(itemCache.size)) {
                val (itemId, qty) = itemCache[i]
                val item = ItemFactory.getById(itemId) ?: continue
                val sel  = i == selectedIdx
                val pulse = if (sel) 0.8f + MathUtils.sin(elapsed * 8f) * 0.2f else 1f
                val fnt  = if (sel) game.fonts.normal else game.fonts.small
                
                val itemY = H - 108f - (i - scrollIdx) * 36f
                
                fnt.setColor(pulse, pulse, if (sel) 0.2f else pulse, 1f)
                sb.clear()
                sb.append(if (sel) "► " else "  ").append(item.name).append("  ×").append(qty)
                fnt.draw(game.batch, sb, 22f, itemY)
                if (sel) {
                    game.fonts.tiny.setColor(C_DESC)
                    game.fonts.tiny.draw(game.batch, item.description, 22f, itemY - 14f)
                }
            }
            
            // Scroll Indicator
            if (itemCache.size > visibleCount) {
                game.fonts.tiny.setColor(C_HINT)
                if (scrollIdx > 0) game.fonts.tiny.draw(game.batch, "▲", W - 40f, H - 90f)
                if (scrollIdx + visibleCount < itemCache.size) game.fonts.tiny.draw(game.batch, "▼", W - 40f, 160f)
            }
        }

        val selItem = itemCache.getOrNull(selectedIdx)?.first?.let { ItemFactory.getById(it) }
        if (selItem != null) {
            game.fonts.small.setColor(C_STAT)
            sb.clear()
            if (selItem.attackBonus  > 0) sb.append("ATK +").append(selItem.attackBonus).append("  ")
            if (selItem.defenseBonus > 0) sb.append("DEF +").append(selItem.defenseBonus).append("  ")
            if (selItem.magicBonus   > 0) sb.append("MAG +").append(selItem.magicBonus).append("  ")
            if (selItem.hpRestore    > 0) sb.append("HP +").append(selItem.hpRestore).append("  ")
            if (selItem.mpRestore    > 0) sb.append("MP +").append(selItem.mpRestore).append("  ")
            if (sb.isNotEmpty()) game.fonts.small.draw(game.batch, sb, 22f, 58f)

            game.fonts.small.setColor(C_VALUE)
            sb.clear(); sb.append("Valeur: ").append(selItem.value).append(" Or")
            game.fonts.small.draw(game.batch, sb, 22f, 36f)
        }

        game.fonts.tiny.setColor(C_HINT)
        game.fonts.tiny.draw(game.batch, "Appuyez sur un objet pour l'utiliser", 22f, 14f)
        
        game.fonts.normal.setColor(C_WHITE)
        game.fonts.normal.draw(game.batch, "[ RETOUR ]", W - 150f, 35f)

        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() {}
}

// ════════════════════════════════════════════════════════════════
// PARTY SCREEN
// FIX PERF #2 #3 #4 #8 + Scaling
// ════════════════════════════════════════════════════════════════

class PartyScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    private val returnScreen: Screen
) : Screen {

    private var selHero = 0
    private var elapsed = 0f
    private val sb = StringBuilder(64)
    private val touchPos = com.badlogic.gdx.math.Vector3()

    companion object {
        private val C_GOLD    = Color(1f, 0.85f, 0.2f, 1f)
        private val C_WHITE   = Color(1f, 1f, 1f, 1f)
        private val C_UNSEL   = Color(0.68f, 0.68f, 0.82f, 1f)
        private val C_HP_BG   = Color(0.25f, 0.05f, 0.05f, 0.9f)
        private val C_HP_FG   = Color(1f, 0.45f, 0.45f, 1f)
        private val C_MP_BG   = Color(0.05f, 0.05f, 0.25f, 0.9f)
        private val C_MP_FG   = Color(0.35f, 0.55f, 1f, 1f)
        private val C_STATS   = Color(0.82f, 0.82f, 1f, 1f)
        private val C_SKILL_T = Color(1f, 0.85f, 0.35f, 1f)
        private val C_SKILL   = Color(0.65f, 0.82f, 1f, 1f)
        private val C_EQUIP   = Color(0.82f, 0.82f, 0.48f, 1f)
        private val C_EXP     = Color(0.45f, 1f, 0.45f, 1f)
        private val C_HINT    = Color(0.45f, 0.45f, 0.55f, 1f)
    }

    override fun show() { 
        game.viewport.camera.position.set(400f, 240f, 0f)
        game.viewport.camera.update()
        elapsed = 0f 
    }

    override fun render(delta: Float) {
        elapsed += delta
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.X) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(returnScreen); dispose(); return
        }
        
        // Touch interaction
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchPos)
            val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
            
            // Back button
            if (touchPos.x > W - 150f && touchPos.y < 50f) {
                game.setScreen(returnScreen); dispose(); return
            }
            
            // Hero selection
            for (i in state.party.indices) {
                val heroX = 28f + i * (W / 3.2f)
                if (touchPos.x > heroX && touchPos.x < heroX + (W/3.2f)) {
                    selHero = i
                    break
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            selHero = (selHero + 1) % state.party.size.coerceAtLeast(1)
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            selHero = (selHero - 1 + state.party.size.coerceAtLeast(1)) %
                      state.party.size.coerceAtLeast(1)

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined

        // Fond sombre
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color(0.04f, 0.04f, 0.12f, 1f)
        game.shapeRenderer.rect(0f, 0f, W, H)
        
        // Barres HP/MP
        for (i in state.party.indices) {
            val hero = state.party[i]
            val x    = 28f + i * (W / 3.2f)
            val hpR = hero.currentHp.toFloat() / hero.maxHp.coerceAtLeast(1)
            val mpR = hero.currentMp.toFloat() / hero.maxMp.coerceAtLeast(1)
            
            game.shapeRenderer.color = C_HP_BG
            game.shapeRenderer.rect(x, H - 130f, 120f, 8f)
            game.shapeRenderer.color = C_HP_FG
            game.shapeRenderer.rect(x, H - 130f, 120f * hpR, 8f)
            
            game.shapeRenderer.color = C_MP_BG
            game.shapeRenderer.rect(x, H - 150f, 120f, 6f)
            game.shapeRenderer.color = C_MP_FG
            game.shapeRenderer.rect(x, H - 150f, 120f * mpR, 6f)
        }
        game.shapeRenderer.end()

        game.batch.begin()
        
        // UI Frame
        game.batch.draw(game.assetLoader.getTexture("sprites/ui_frame.png"), 10f, 10f, W - 20f, H - 20f)

        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, "ÉQUIPE", 35f, H - 35f)

        for (i in state.party.indices) {
            val hero = state.party[i]
            val x    = 28f + i * (W / 3.2f)
            val sel  = i == selHero
            val pulse = if (sel) 0.8f + MathUtils.sin(elapsed * 6f) * 0.2f else 1f
            val fnt  = if (sel) game.fonts.medium else game.fonts.normal
            fnt.setColor(pulse, pulse, if (sel) 0.3f else pulse, 1f)
            fnt.draw(game.batch, "${if (sel) "►" else " "} ${hero.name}", x, H - 76f)

            game.fonts.small.setColor(C_EXP)
            sb.clear(); sb.append("Nv.").append(hero.level)
                .append("  ").append(hero.experience).append('/').append(hero.expToNextLevel)
                .append(" EXP")
            game.fonts.small.draw(game.batch, sb, x, H - 106f)

            // Les barres sont déja dessinées en-dessous
            game.fonts.tiny.setColor(C_HP_FG)
            sb.clear(); sb.append(hero.currentHp).append('/').append(hero.maxHp)
            game.fonts.tiny.draw(game.batch, sb, x + 50f, H - 114f)

            game.fonts.tiny.setColor(C_MP_FG)
            sb.clear(); sb.append(hero.currentMp).append('/').append(hero.maxMp)
            game.fonts.tiny.draw(game.batch, sb, x + 50f, H - 138f)

            game.fonts.small.setColor(C_STATS)
            sb.clear(); sb.append("ATK ").append(hero.totalAttack())
            game.fonts.small.draw(game.batch, sb, x, H - 178f)
            sb.clear(); sb.append("DEF ").append(hero.totalDefense())
            game.fonts.small.draw(game.batch, sb, x, H - 196f)
            sb.clear(); sb.append("MAG ").append(hero.totalMagic())
            game.fonts.small.draw(game.batch, sb, x, H - 214f)
            sb.clear(); sb.append("AGI ").append(hero.agility)
            game.fonts.small.draw(game.batch, sb, x, H - 232f)

            if (sel) {
                game.fonts.small.setColor(C_SKILL_T)
                game.fonts.small.draw(game.batch, "── Compétences ──", x, H - 265f)
                for (j in hero.skills.indices) {
                    game.fonts.tiny.setColor(C_SKILL)
                    sb.clear(); sb.append("• ").append(hero.skills[j].name)
                        .append(" (").append(hero.skills[j].mpCost).append("MP)")
                    game.fonts.tiny.draw(game.batch, sb, x, H - 285f - j * 22f)
                }
                game.fonts.tiny.setColor(C_EQUIP)
                sb.clear(); sb.append("Arme: ").append(hero.weapon?.name ?: "—")
                game.fonts.tiny.draw(game.batch, sb, x, H - 380f)
                sb.clear(); sb.append("Armure: ").append(hero.armor?.name ?: "—")
                game.fonts.tiny.draw(game.batch, sb, x, H - 400f)
                sb.clear(); sb.append("Acc: ").append(hero.accessory?.name ?: "—")
                game.fonts.tiny.draw(game.batch, sb, x, H - 420f)
            }
        }

        game.fonts.tiny.setColor(C_HINT)
        game.fonts.tiny.draw(game.batch, "Appuyez sur un héros pour voir ses détails", 22f, 14f)
        
        game.fonts.normal.setColor(C_WHITE)
        game.fonts.normal.draw(game.batch, "[ RETOUR ]", W - 150f, 35f)
        
        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() {}
}

// ════════════════════════════════════════════════════════════════
// SAVE SCREEN
// FIX REVIEW #2 + Scaling
// ════════════════════════════════════════════════════════════════

class SaveScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    val mode: Mode,
    private val returnScreen: Screen? = null
) : Screen {

    enum class Mode { SAVE, LOAD }

    private var selSlot   = 0
    private val SLOTS     = 3
    private var statusMsg = ""
    private var saving    = false
    private var elapsed   = 0f
    private val touchPos = com.badlogic.gdx.math.Vector3()

    private var saveDetails: Array<com.astralya.data.entities.GameSaveEntity?> = arrayOfNulls(SLOTS)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val sb = StringBuilder(64)

    companion object {
        private val C_GOLD    = Color(1f, 0.85f, 0.2f, 1f)
        private val C_WHITE   = Color(1f, 1f, 1f, 1f)
        private val C_SEL     = Color(1f, 1f, 1f, 1f)
        private val C_UNSEL   = Color(0.68f, 0.68f, 0.82f, 1f)
        private val C_DETAILS = Color(0.5f, 0.7f, 1f, 1f)
        private val C_OK      = Color(0.3f, 1f, 0.3f, 1f)
        private val C_ERR     = Color(1f, 0.3f, 0.3f, 1f)
        private val C_HINT    = Color(0.45f, 0.45f, 0.55f, 1f)
    }

    override fun show() {
        game.viewport.camera.position.set(400f, 240f, 0f)
        game.viewport.camera.update()
        elapsed = 0f
        refreshSaves()
    }

    private fun refreshSaves() {
        scope.launch {
            for (i in 0 until SLOTS) {
                saveDetails[i] = game.repository.loadGame(i)
            }
        }
    }

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()
        draw()
    }

    private fun handleInput() {
        if (saving) return
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(returnScreen ?: MainMenuScreen(game)); dispose(); return
        }
        
        // Touch interaction
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchPos)
            val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
            
            // Back button (Visible area at the bottom right)
            if (touchPos.x > W - 180f && touchPos.y < 60f) {
                game.setScreen(returnScreen ?: MainMenuScreen(game)); dispose(); return
            }
            
            // Slot selection
            for (i in 0 until SLOTS) {
                val slotY = H - 118f - i * 68f
                if (touchPos.y < slotY && touchPos.y > slotY - 50f) {
                    selSlot = i
                    if (mode == Mode.SAVE) doSave() else doLoad()
                    break
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            selSlot = (selSlot + 1) % SLOTS
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            selSlot = (selSlot - 1 + SLOTS) % SLOTS
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            if (mode == Mode.SAVE) doSave() else doLoad()
        }
    }

    private fun doSave() {
        saving = true; statusMsg = "Sauvegarde en cours..."
        scope.launch {
            runCatching {
                val save = com.astralya.data.entities.GameSaveEntity(
                    slot            = selSlot,
                    saveName        = "Slot ${selSlot + 1}",
                    currentMapId    = state.currentMapId,
                    playerX         = state.playerX,
                    playerY         = state.playerY,
                    gold            = state.gold,
                    playtimeSeconds = state.playtimeSeconds,
                    crystalsFound   = state.crystalsFound.joinToString(","),
                    defeatedBosses  = state.defeatedBosses.joinToString(",")
                )
                game.repository.saveGame(save)
                state.party.forEach { hero ->
                    game.repository.saveHero(
                        com.astralya.data.entities.HeroEntity(
                            id = hero.id.name, name = hero.name,
                            heroClass = hero.role.name,
                            level = hero.level, experience = hero.experience,
                            maxHp = hero.maxHp, currentHp = hero.currentHp,
                            maxMp = hero.maxMp, currentMp = hero.currentMp,
                            attack = hero.attack, defense = hero.defense,
                            agility = hero.agility, magic = hero.magic
                        )
                    )
                }
                Gdx.app.postRunnable { 
                    statusMsg = "✅ Sauvegarde réussie !"
                    saving = false
                    refreshSaves()
                }
            }.onFailure { e ->
                Gdx.app.postRunnable { statusMsg = "❌ Erreur : ${e.message ?: "Inconnue"}"; saving = false }
            }
        }
    }

    private fun doLoad() {
        saving = true; statusMsg = "Chargement..."
        scope.launch {
            runCatching {
                val save = game.repository.loadGame(selSlot)
                if (save != null) {
                    Gdx.app.postRunnable {
                        state.currentMapId    = save.currentMapId
                        state.playerX         = save.playerX
                        state.playerY         = save.playerY
                        state.gold            = save.gold
                        state.playtimeSeconds = save.playtimeSeconds
                        save.crystalsFound.split(",").filter { it.isNotBlank() }.forEach { state.crystalsFound.add(it) }
                        save.defeatedBosses.split(",").filter { it.isNotBlank() }.forEach { state.defeatedBosses.add(it) }
                        statusMsg = "✅ Partie chargée !"
                        saving    = false
                        game.setScreen(ExplorationScreen(game, state))
                        dispose()
                    }
                } else {
                    Gdx.app.postRunnable { statusMsg = "Aucune sauvegarde dans ce slot."; saving = false }
                }
            }.onFailure { e ->
                Gdx.app.postRunnable { statusMsg = "❌ Erreur : ${e.message ?: "Inconnue"}"; saving = false }
            }
        }
    }

    private fun draw() {
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.03f, 0.03f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined

        game.batch.begin()
        
        // UI Frame
        game.batch.draw(game.assetLoader.getTexture("sprites/ui_frame.png"), 10f, 10f, W - 20f, H - 20f)

        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, if (mode == Mode.SAVE) "SAUVEGARDER" else "CHARGER", 35f, H - 35f)

        for (i in 0 until SLOTS) {
            val sel = i == selSlot
            val pulse = if (sel) 0.8f + MathUtils.sin(elapsed * 6f) * 0.2f else 1f
            val fnt = if (sel) game.fonts.medium else game.fonts.normal
            fnt.setColor(pulse, pulse, if (sel) 0.3f else pulse, 1f)
            
            val slotY = H - 118f - i * 68f
            sb.clear(); sb.append(if (sel) "► " else "  ").append("Slot ").append(i + 1)
            fnt.draw(game.batch, sb, 38f, slotY)
            
            // Details
            val save = saveDetails[i]
            if (save != null) {
                game.fonts.tiny.setColor(C_DETAILS)
                val hours = save.playtimeSeconds / 3600
                val mins  = (save.playtimeSeconds % 3600) / 60
                val mapName = MapRegistry.getMap(save.currentMapId)?.name ?: save.currentMapId
                sb.clear(); sb.append(mapName).append(" | Or: ").append(save.gold)
                    .append(" | ").append(String.format(Locale.US, "%02d:%02d", hours, mins))
                game.fonts.tiny.draw(game.batch, sb, 160f, slotY - 10f)
            } else {
                game.fonts.tiny.setColor(C_UNSEL)
                game.fonts.tiny.draw(game.batch, "— Emplacement Vide —", 160f, slotY - 10f)
            }
        }

        if (statusMsg.isNotBlank()) {
            val col = when {
                statusMsg.startsWith("✅") -> C_OK
                statusMsg.startsWith("❌") -> C_ERR
                else                       -> C_WHITE
            }
            game.fonts.normal.setColor(col)
            game.fonts.normal.draw(game.batch, statusMsg, 38f, H - 340f)
        }

        game.fonts.tiny.setColor(C_HINT)
        game.fonts.tiny.draw(game.batch, "↑↓ Sélectionner  |  ENTRÉE Confirmer  |  ESC Retour", 24f, 14f)

        game.fonts.medium.setColor(Color.WHITE)
        game.fonts.medium.draw(game.batch, "[ RETOUR ]", W - 180f, 45f)

        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { scope.cancel() }
}

// ════════════════════════════════════════════════════════════════
// OPTIONS SCREEN
// FIX PERF #2 #3 #8 + Scaling
// ════════════════════════════════════════════════════════════════

class OptionsScreen(private val game: AstralYaGame) : Screen {

    private var selIdx  = 0
    private var elapsed = 0f
    private val options = listOf("Volume Musique", "Volume SFX", "Musique", "SFX", "Retour")
    private val sb = StringBuilder(32)
    private val touchPos = com.badlogic.gdx.math.Vector3()

    companion object {
        private val C_GOLD  = Color(1f, 0.85f, 0.2f, 1f)
        private val C_WHITE = Color(1f, 1f, 1f, 1f)
        private val C_SEL   = Color(1f, 1f, 1f, 1f)
        private val C_UNSEL = Color(0.68f, 0.68f, 0.82f, 1f)
    }

    override fun show() { 
        game.viewport.camera.position.set(400f, 240f, 0f)
        game.viewport.camera.update()
        elapsed = 0f 
    }

    override fun render(delta: Float) {
        elapsed += delta
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(MainMenuScreen(game)); dispose(); return
        }
        
        // Touch interaction
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            game.viewport.unproject(touchPos)
            val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
            
            for (i in options.indices) {
                val optY = H - 100f - i * 58f
                if (touchPos.y < optY && touchPos.y > optY - 45f) {
                    selIdx = i
                    if (i == 4) { game.setScreen(MainMenuScreen(game)); dispose(); return }
                    else if (i == 2) game.audioManager.isMusicEnabled = !game.audioManager.isMusicEnabled
                    else if (i == 3) game.audioManager.isSfxEnabled = !game.audioManager.isSfxEnabled
                    else if (touchPos.x > W / 2f) { // Simple right side touch to increase
                        if (i == 0) game.audioManager.musicVolume += 0.1f
                        else if (i == 1) game.audioManager.sfxVolume += 0.1f
                    } else { // Left side touch to decrease
                        if (i == 0) game.audioManager.musicVolume -= 0.1f
                        else if (i == 1) game.audioManager.sfxVolume -= 0.1f
                    }
                    break
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
            selIdx = (selIdx + 1) % options.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            selIdx = (selIdx - 1 + options.size) % options.size
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            when (selIdx) {
                2 -> game.audioManager.isMusicEnabled = !game.audioManager.isMusicEnabled
                3 -> game.audioManager.isSfxEnabled   = !game.audioManager.isSfxEnabled
                4 -> { game.setScreen(MainMenuScreen(game)); dispose(); return }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) when (selIdx) {
            0 -> game.audioManager.musicVolume += 0.1f
            1 -> game.audioManager.sfxVolume += 0.1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) when (selIdx) {
            0 -> game.audioManager.musicVolume -= 0.1f
            1 -> game.audioManager.sfxVolume -= 0.1f
        }

        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined

        game.batch.begin()
        
        // UI Frame
        game.batch.draw(game.assetLoader.getTexture("sprites/ui_frame.png"), 10f, 10f, W - 20f, H - 20f)

        game.fonts.large.setColor(C_GOLD)
        game.fonts.large.draw(game.batch, "OPTIONS", 35f, H - 35f)

        for (i in options.indices) {
            val sel = i == selIdx
            val pulse = if (sel) 0.8f + MathUtils.sin(elapsed * 6f) * 0.2f else 1f
            val fnt = if (sel) game.fonts.medium else game.fonts.normal
            fnt.setColor(pulse, pulse, if (sel) 0.3f else pulse, 1f)
            sb.clear()
            sb.append(if (sel) "► " else "  ").append(options[i])
            when (i) {
                0 -> sb.append(" : ").append((game.audioManager.musicVolume * 10).toInt()).append("/10")
                1 -> sb.append(" : ").append((game.audioManager.sfxVolume   * 10).toInt()).append("/10")
                2 -> sb.append(" : ").append(if (game.audioManager.isMusicEnabled) "ON" else "OFF")
                3 -> sb.append(" : ").append(if (game.audioManager.isSfxEnabled)   "ON" else "OFF")
            }
            fnt.draw(game.batch, sb, 38f, H - 100f - i * 58f)
        }
        
        game.fonts.tiny.setColor(C_UNSEL)
        game.fonts.tiny.draw(game.batch, "Appuyez pour sélectionner, re-appuyez pour changer", 24f, 14f)

        game.fonts.medium.setColor(Color.WHITE)
        game.fonts.medium.draw(game.batch, "[ RETOUR ]", W - 180f, 45f)

        game.batch.setColor(C_WHITE)
        game.batch.end()
        game.fonts.resetColors()
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() {}
}
