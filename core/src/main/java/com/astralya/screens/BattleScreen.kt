package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.astralya.AstralYaGame
import com.astralya.combat.*
import com.astralya.data.GameState
import com.astralya.entities.*
import com.astralya.map.MapRegistry
import com.astralya.screens.MainMenuScreen

/**
 * FIX REVIEW #1 — navigate() corrigé : utilise currentIdx passé en paramètre
 * FIX REVIEW #8 — level-up notifié à l'UI via applyCombatRewards()
 * FIX PERF #2   — toutes les Color() en companion object
 * FIX PERF #3   — StringBuilder réutilisé pour HUD
 * FIX PERF #4   — for(i in indices) au lieu de forEachIndexed
 * FIX PERF #6   — CombatSystem reçoit game.random
 * FIX PERF #8   — game.fonts au lieu de font.data.setScale()
 */
class BattleScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    private val enemies: List<Enemy>,
    private val mapId: String,
    private val returnScreen: Screen
) : Screen {

    // FIX PERF #6 — CombatSystem avec GameRandom injecté
    private val combat = CombatSystem(game.random)
    private val party  = state.party.filter { it.isAlive }
    private val isValid = party.isNotEmpty()

    private var battleState = combat.initCombat(party, enemies)

    private enum class Phase {
        PLAYER_ACTION, PLAYER_TARGET, PLAYER_SKILL, PLAYER_SKILL_TARGET,
        ANIMATING, ENEMY_TURN, LEVEL_UP, VICTORY, GAME_OVER
    }

    private var phase      = Phase.PLAYER_ACTION
    private var heroIndex  = 0
    private var actionIdx  = 0
    private var skillIdx   = 0
    private var targetIdx  = 0
    private var heroesDone = 0

    private val actionItems = listOf("Attaquer", "Compétence", "Objet", "Fuir")
    private val battleLog   = ArrayDeque<String>(6)
    private var animTimer   = 0f
    private var elapsed     = 0f

    private var pendingResults   = emptyList<ActionResult>()
    private var resultIdx        = 0
    private var levelUpHeroes    = emptyList<Hero>()   // FIX REVIEW #8
    private var levelUpShowTimer = 0f

    // FIX PERF #7 — calculés une fois par frame dans render()
    private var aliveEnemies: List<Enemy> = emptyList()
    private var aliveParty:   List<Hero>  = emptyList()

    // FIX PERF #2 — couleurs constantes
    companion object {
        private val C_BG1       = Color(0.08f, 0.04f, 0.16f, 1f)
        private val C_PANEL_L   = Color(0.05f, 0.05f, 0.18f, 0.92f)
        private val C_PANEL_R   = Color(0.05f, 0.08f, 0.20f, 0.92f)
        private val C_ACTIVE_HL = Color(0.18f, 0.18f, 0.48f, 0.88f)
        private val C_HP_BG     = Color(0.28f, 0f,    0f,    0.85f)
        private val C_MP_BG     = Color(0f,    0f,    0.3f,  0.8f)
        private val C_MP_FG     = Color(0.1f,  0.3f,  1f,    1f)
        private val C_LOG_BG    = Color(0f,    0f,    0f,    0.5f)
        private val C_GOLD      = Color(1f, 0.85f, 0.05f, 1f)  // valeur fixe, jamais mutée
        private val C_WHITE     = Color(1f, 1f, 1f, 1f)
        private val C_GRAY      = Color(0.35f, 0.35f, 0.35f, 1f)
        private val C_TXT_HP    = Color(0.45f, 1f,    0.45f, 1f)
        private val C_TXT_MP    = Color(0.35f, 0.55f, 1f,    1f)
        private val C_TXT_STATUS= Color(1f,    0.35f, 0.35f, 1f)
        private val C_TXT_ACT   = Color(1f,    0.88f, 0.3f,  1f)
        private val C_TXT_SKILL = Color(0.68f, 0.82f, 1f,    1f)
        private val C_TXT_ENE   = Color(1f,    0.85f, 0.5f,  1f)
        private val C_TXT_UNS   = Color(0.68f, 0.68f, 0.78f, 1f)
        private val C_ENEMY_RED = Color(1f,    0.38f, 0.38f, 1f)
        private val C_LEVELUP   = Color(1f,    0.9f,  0.1f,  1f)
    }

    // FIX PERF #3 — StringBuilder réutilisé
    private val sb = StringBuilder(64)
    // FIX #2 — propriété d'instance, pas companion (évite état partagé entre instances)
    private val cursorColor = Color()
    // Couleur temporaire réutilisée pour HP ennemis
    private val tmpColor = Color()

    // Modern Visuals
    private var postProcessShader: ShaderProgram? = null
    private val ambientColor = Color(1f, 1f, 1f, 1f)
    
    // Polish Visuals
    private var shakeTimer = 0f
    private var shakeIntensity = 0f
    private val flashTimers = mutableMapOf<Any, Float>()
    
    private data class FloatingText(val text: String, val color: Color, var x: Float, var y: Float, var life: Float)
    private val floatingTexts = mutableListOf<FloatingText>()

    private fun addLog(msg: String) {
        if (battleLog.size >= 6) battleLog.removeFirst()
        battleLog.addLast(msg)
    }

    override fun show() {
        if (!isValid) { returnToExploration(); return }
        loadShaders()
        applyAmbientColor()
        runCatching { game.audioManager.playMusic(game.assetLoader.getMusic("audio/music_battle.ogg")) }
        addLog("⚔️ Combat !")
        enemies.forEach { addLog("${it.name} apparaît !") }
    }

    private fun loadShaders() {
        val vert = Gdx.files.internal("shaders/default.vert")
        val frag = Gdx.files.internal("shaders/post_process.frag")
        postProcessShader = ShaderProgram(vert, frag)
        if (!postProcessShader!!.isCompiled) {
            Gdx.app.error("AstralYa", "战斗着色器加载失败: ${postProcessShader!!.log}")
            postProcessShader = null
        }
    }

    private fun applyAmbientColor() {
        when (mapId) {
            "grotte_cristal"  -> ambientColor.set(0.6f, 0.7f, 1.0f, 1f)
            "desert_oublie"   -> ambientColor.set(1.1f, 0.9f, 0.7f, 1f)
            "chateau_morvax"  -> ambientColor.set(0.7f, 0.5f, 0.8f, 1f)
            else              -> ambientColor.set(1.0f, 1.0f, 1.0f, 1f)
        }
    }

    override fun render(delta: Float) {
        if (!isValid) return
        elapsed += delta

        // FIX PERF #7 — calculé UNE FOIS par frame
        aliveEnemies = battleState.enemies.filter { it.isAlive }
        aliveParty   = party.filter { it.isAlive }

        update(delta)
        draw()
    }

    // ── Update ────────────────────────────────────────────────────────────────

    private fun update(delta: Float) {
        // Update Polish Visuals
        if (shakeTimer > 0) shakeTimer -= delta
        val it = flashTimers.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            entry.setValue(entry.value - delta)
            if (entry.value <= 0) it.remove()
        }
        val ftIt = floatingTexts.iterator()
        while (ftIt.hasNext()) {
            val ft = ftIt.next()
            ft.life -= delta
            ft.y += 40f * delta
            if (ft.life <= 0) ftIt.remove()
        }

        when (phase) {
            Phase.PLAYER_ACTION       -> handleActionMenu()
            Phase.PLAYER_TARGET       -> handleTargetMenu()
            Phase.PLAYER_SKILL        -> handleSkillMenu()
            Phase.PLAYER_SKILL_TARGET -> handleSkillTargetMenu()
            Phase.ANIMATING -> {
                animTimer += delta
                if (animTimer >= 0.7f) { animTimer = 0f; showNextResult() }
            }
            Phase.ENEMY_TURN -> {
                animTimer += delta
                if (animTimer >= 1.1f) { animTimer = 0f; doEnemyTurn() }
            }
            Phase.LEVEL_UP -> {
                // FIX REVIEW #8 — affiche 2s par héros ayant level-up
                levelUpShowTimer += delta
                if (levelUpShowTimer >= 2f || confirm()) {
                    levelUpShowTimer = 0f
                    returnToExploration()
                }
            }
            Phase.VICTORY, Phase.GAME_OVER -> {
                animTimer += delta
                if (animTimer >= 2.8f || Gdx.input.justTouched() || confirm()) {
                    if (phase == Phase.VICTORY) returnToExploration()
                    else { game.setScreen(MainMenuScreen(game)); dispose() }
                }
            }
        }
    }

    // ── FIX REVIEW #1 — navigate() corrigé ───────────────────────────────────
    // Prend currentIdx ET setter : n'utilise plus actionIdx à la place
    // FIX #5 — inline élimine l'allocation du lambda à chaque appel (60x/sec)
    private inline fun navigate(currentIdx: Int, size: Int, setter: (Int) -> Unit) {
        if (size == 0) return
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
            setter((currentIdx + 1) % size)
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            setter((currentIdx - 1 + size) % size)
    }

    private fun confirm() = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                            Gdx.input.isKeyJustPressed(Input.Keys.Z)
    private fun cancel()  = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
                            Gdx.input.isKeyJustPressed(Input.Keys.X)

    private fun handleActionMenu() {
        // FIX REVIEW #1 — passe actionIdx comme currentIdx
        navigate(actionIdx, actionItems.size) { actionIdx = it }
        if (confirm()) when (actionIdx) {
            0 -> { phase = Phase.PLAYER_TARGET; targetIdx = 0 }
            1 -> { phase = Phase.PLAYER_SKILL;  skillIdx  = 0 }
            2 -> useItemInBattle()
            3 -> tryFlee()
        }
    }

    private fun handleTargetMenu() {
        navigate(targetIdx, aliveEnemies.size) { targetIdx = it }
        if (confirm()) {
            val hero   = currentHero ?: return
            val target = aliveEnemies.getOrNull(targetIdx) ?: return
            startAnim(combat.executeHeroAction(CombatAction.Attack(hero, target), battleState))
        }
        if (cancel()) phase = Phase.PLAYER_ACTION
    }

    private fun handleSkillMenu() {
        val skills = currentHero?.skills ?: return
        navigate(skillIdx, skills.size) { skillIdx = it }
        if (confirm()) {
            val hero  = currentHero ?: return
            val skill = skills.getOrNull(skillIdx) ?: return
            if (skill.hitAll) startAnim(combat.executeHeroAction(
                CombatAction.UseSkill(hero, skill, null), battleState))
            else { phase = Phase.PLAYER_SKILL_TARGET; targetIdx = 0 }
        }
        if (cancel()) phase = Phase.PLAYER_ACTION
    }

    private fun handleSkillTargetMenu() {
        navigate(targetIdx, aliveEnemies.size) { targetIdx = it }
        if (confirm()) {
            val hero   = currentHero ?: return
            val skill  = hero.skills.getOrNull(skillIdx) ?: return
            val target = aliveEnemies.getOrNull(targetIdx) ?: return
            startAnim(combat.executeHeroAction(CombatAction.UseSkill(hero, skill, target), battleState))
        }
        if (cancel()) phase = Phase.PLAYER_SKILL
    }

    private val currentHero: Hero?
        get() = party.filter { it.isAlive }.getOrNull(heroIndex)

    private fun startAnim(results: List<ActionResult>) {
        pendingResults = results; resultIdx = 0
        phase = Phase.ANIMATING; animTimer = 0f
        showNextResult()
    }

    private fun showNextResult() {
        if (resultIdx < pendingResults.size) {
            val result = pendingResults[resultIdx]
            addLog(result.message)
            
            // Trigger Visual Effects based on result
            if (result.damageDealt > 0 || result.healingDone > 0) {
                applyResultVisuals(result)
            }
            
            resultIdx++
        } else {
            battleState = combat.checkCombatEnd(battleState)
            when {
                battleState.playerWon -> { onVictory() }
                battleState.isOver    -> { phase = Phase.GAME_OVER; animTimer = 0f }
                else                  -> advanceTurn()
            }
        }
    }

    private fun applyResultVisuals(result: ActionResult) {
        // This is a simplified way to guess targets for visuals since ActionResult 
        // doesn't store the target object directly. In a real scenario, we'd pass the target.
        // For now, we'll use the current targetIdx for Player Action results.
        
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight
        
        if (result.damageDealt > 0) {
            shakeTimer = if (result.critical) 0.4f else 0.2f
            shakeIntensity = if (result.critical) 8f else 4f
            
            // Estimate target position for floating text
            val tx: Float
            val ty: Float
            if (phase == Phase.ANIMATING) {
                // If hero attacking enemy
                val eIdx = targetIdx.coerceIn(0, aliveEnemies.size - 1)
                tx = W * 0.20f + eIdx * (W / (aliveEnemies.size + 1).coerceAtLeast(1))
                ty = H * 0.65f
                aliveEnemies.getOrNull(eIdx)?.let { flashTimers[it] = 0.15f }
            } else {
                // Enemy turn (hero taking damage)
                val hIdx = heroIndex.coerceIn(0, party.size - 1)
                tx = 50f
                ty = H * 0.31f - hIdx * 80f
                party.getOrNull(hIdx)?.let { flashTimers[it] = 0.15f }
            }
            
            floatingTexts.add(FloatingText(
                result.damageDealt.toString(), 
                if (result.critical) Color.ORANGE else Color.RED, 
                tx, ty + 20f, 1.0f
            ))
            game.assetLoader.getSound(if (result.critical) "audio/sfx_critical.ogg" else "audio/sfx_hit.ogg").play()
        }
        
        if (result.healingDone > 0) {
            floatingTexts.add(FloatingText(
                "+${result.healingDone}", 
                Color.GREEN, 
                W * 0.25f, H * 0.3f, 1.0f
            ))
            game.assetLoader.getSound("audio/sfx_heal.ogg").play()
        }
    }

    private fun advanceTurn() {
        heroesDone++
        val alive = party.filter { it.isAlive }
        if (heroesDone >= alive.size) {
            combat.applyEndOfTurnEffects(battleState).forEach { addLog(it) }
            heroesDone = 0; heroIndex = 0
            phase = Phase.ENEMY_TURN; animTimer = 0f
        } else {
            heroIndex = (heroIndex + 1) % party.size
            var safety = 0
            while (!party[heroIndex].isAlive && safety < party.size) {
                heroIndex = (heroIndex + 1) % party.size; safety++
            }
            if (!party[heroIndex].isAlive) {
                heroesDone = 0; heroIndex = 0
                phase = Phase.ENEMY_TURN; animTimer = 0f
            } else {
                phase = Phase.PLAYER_ACTION; actionIdx = 0
            }
        }
    }

    private fun doEnemyTurn() {
        aliveEnemies.forEach { enemy ->
            combat.executeEnemyTurn(enemy, battleState).forEach { addLog(it.message) }
        }
        battleState = combat.checkCombatEnd(battleState)
        when {
            battleState.playerWon -> onVictory()
            battleState.isOver    -> { phase = Phase.GAME_OVER; animTimer = 0f }
            else                  -> { heroIndex = 0; heroesDone = 0; phase = Phase.PLAYER_ACTION }
        }
    }

    private fun tryFlee() {
        if (combat.canFlee(party, enemies)) {
            addLog("L'équipe prend la fuite !"); returnToExploration()
        } else {
            addLog("Impossible de fuir !"); phase = Phase.ENEMY_TURN; animTimer = 0f
        }
    }

    private fun useItemInBattle() {
        val item = state.inventory.keys
            .mapNotNull { ItemFactory.getById(it) }
            .firstOrNull { it.hpRestore > 0 }
        if (item != null) {
            val target = aliveParty.minByOrNull { it.currentHp } ?: return
            target.heal(item.hpRestore); state.removeItem(item.id)
            addLog("${currentHero?.name} utilise ${item.name} sur ${target.name}.")
            advanceTurn()
        } else { addLog("Aucun objet utilisable !"); phase = Phase.PLAYER_ACTION }
    }

    private fun onVictory() {
        val rewards = combat.calculateRewards(enemies)
        // FIX REVIEW #8 — applyCombatRewards retourne les héros ayant level-up
        levelUpHeroes = state.applyCombatRewards(
            rewards.experience, rewards.gold, rewards.items, game.random)
        addLog("Victoire !  +${rewards.experience} EXP  +${rewards.gold} Or")
        enemies.filter { it.isBoss }.forEach { state.defeatBoss(it.id) }

        if (levelUpHeroes.isNotEmpty()) {
            levelUpHeroes.forEach { addLog("${it.name} monte au niveau ${it.level} !") }
            phase = Phase.LEVEL_UP; levelUpShowTimer = 0f
        } else {
            phase = Phase.VICTORY; animTimer = 0f
        }
    }

    private fun returnToExploration() {
        runCatching {
            game.audioManager.playMusic(
                game.assetLoader.getMusic(
                    MapRegistry.getMap(state.currentMapId)?.musicFile ?: "audio/music_village.ogg"))
        }
        game.setScreen(returnScreen); dispose()
    }

    // ── Rendu ─────────────────────────────────────────────────────────────────

    private fun draw() {
        val W = game.viewport.worldWidth
        val H = game.viewport.worldHeight

        Gdx.gl.glClearColor(0.05f, 0.02f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = game.viewport.camera.combined
        game.shapeRenderer.projectionMatrix = game.viewport.camera.combined
        
        // Screen Shake
        if (shakeTimer > 0) {
            val shakeX = (Math.random().toFloat() - 0.5f) * 2f * shakeIntensity
            val shakeY = (Math.random().toFloat() - 0.5f) * 2f * shakeIntensity
            game.viewport.camera.translate(shakeX, shakeY, 0f)
            game.viewport.camera.update()
            game.batch.projectionMatrix = game.viewport.camera.combined
            game.shapeRenderer.projectionMatrix = game.viewport.camera.combined
        }

        // Appliquer l'ambiance au rendu du monde (fonds)
        game.batch.color = ambientColor

        val shape = game.shapeRenderer

        // ── Passe shape (Filled) ──────────────────────────────────────────────
        shape.begin(ShapeRenderer.ShapeType.Filled)

        // Fond par défaut si texture absente
        shape.color = C_BG1; shape.rect(0f, 0f, W, H)
        shape.end()

        // Background Image
        game.batch.begin()
        game.batch.draw(game.assetLoader.getBattleBackground(mapId), 0f, H * 0.35f, W, H * 0.65f)
        game.batch.end()

        shape.begin(ShapeRenderer.ShapeType.Filled)
        // FIX PERF #4 — for classique
        for (i in aliveEnemies.indices) {
            val enemy = aliveEnemies[i]
            val ex    = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1))
            val ey    = H * 0.65f
            val size  = if (enemy.isBoss) 88f else 52f

            // On ne dessine plus le rectangle de couleur ici, on le fera avec une texture dans le batch
            // Sauf pour le curseur et la barre de vie

            if (enemy.isBoss) { shape.color = C_GOLD; shape.triangle(ex-22f, ey+size/2f, ex, ey+size/2f+22f, ex+22f, ey+size/2f) }

            if ((phase == Phase.PLAYER_TARGET || phase == Phase.PLAYER_SKILL_TARGET) && i == targetIdx) {
                cursorColor.set(1f, 1f, 0f, 0.55f + MathUtils.sin(elapsed * 5f) * 0.35f)
                shape.color = cursorColor
                shape.rect(ex - size/2f - 5f, ey - size/2f - 5f, size + 10f, size + 10f)
            }

            val hpR = enemy.currentHp.toFloat() / enemy.maxHp.coerceAtLeast(1)
            shape.color = C_HP_BG;  shape.rect(ex - 36f, ey + size/2f + 8f, 72f, 9f)
            tmpColor.set(hpR * 0.9f, (1f-hpR)*0.1f + hpR*0.6f, 0f, 1f)
            shape.color = tmpColor; shape.rect(ex - 36f, ey + size/2f + 8f, 72f * hpR, 9f)
        }

        shape.color = C_PANEL_L; shape.rect(0f, 0f, W * 0.56f, H * 0.35f)
        shape.color = C_PANEL_R; shape.rect(W * 0.56f, 0f, W * 0.44f, H * 0.35f)

        // FIX PERF #4
        for (i in party.indices) {
            val hero = party[i]
            val bx   = 20f; val by = H * 0.31f - i * 80f
            if (i == heroIndex && phase == Phase.PLAYER_ACTION) {
                shape.color = C_ACTIVE_HL; shape.rect(2f, by - 32f, W * 0.55f - 4f, 76f)
            }
            val hpR = hero.currentHp.toFloat() / hero.maxHp.coerceAtLeast(1)
            val mpR = hero.currentMp.toFloat() / hero.maxMp.coerceAtLeast(1)
            shape.color = C_HP_BG;  shape.rect(bx+48f, by+10f, 118f, 10f)
            tmpColor.set(hpR*0.8f, hpR*0.6f, 0.1f, 1f)
            shape.color = tmpColor; shape.rect(bx+48f, by+10f, 118f*hpR, 10f)
            shape.color = C_MP_BG;  shape.rect(bx+48f, by-4f, 118f, 8f)
            shape.color = C_MP_FG;  shape.rect(bx+48f, by-4f, 118f*mpR, 8f)
        }

        shape.color = C_LOG_BG
        shape.rect(W * 0.57f, H * 0.35f, W * 0.43f - 4f, H * 0.22f)
        shape.end()

        // UI & Post-Processing
        game.batch.color = Color.WHITE
        val oldShader = game.batch.shader
        if (postProcessShader != null) game.batch.shader = postProcessShader

        // ── Passe batch (images et texte) ─────────────────────────────────────
        game.batch.begin()
        game.batch.setColor(C_WHITE)

        // Ennemis
        for (i in aliveEnemies.indices) {
            val enemy = aliveEnemies[i]
            val ex = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1))
            val ey = H * 0.65f
            val size = if (enemy.isBoss) 120f else 64f
            
            // Hit Flash
            if (flashTimers.containsKey(enemy)) game.batch.setColor(Color.WHITE)
            else game.batch.setColor(C_WHITE)
            
            game.batch.draw(game.assetLoader.getEnemyTexture(enemy.id), ex - size/2f, ey - size/2f, size, size)
            game.batch.setColor(C_WHITE)
            
            game.fonts.small.setColor(C_TXT_ENE)
            game.fonts.small.draw(game.batch, enemy.name, ex - 48f, ey - size/2f - 6f)
            game.fonts.tiny.setColor(C_WHITE)
            sb.clear(); sb.append(enemy.currentHp).append('/').append(enemy.maxHp)
            game.fonts.tiny.draw(game.batch, sb, ex - 28f, ey - size/2f - 20f)
        }

        // Héros
        for (i in party.indices) {
            val hero = party[i]
            val bx = 20f; val by = H * 0.31f - i * 80f
            
            // Texture du héros
            val baseColor = if (hero.isAlive) Color.WHITE else Color.GRAY
            if (flashTimers.containsKey(hero)) game.batch.setColor(Color.WHITE)
            else game.batch.setColor(baseColor)
            
            // Active Hero Bounce
            var offsetY = 0f
            if (i == heroIndex && hero.isAlive && phase == Phase.PLAYER_ACTION) {
                offsetY = MathUtils.sin(elapsed * 6f) * 4f
            }

            val heroTex = game.assetLoader.getTexture("sprites/${hero.id.name.lowercase()}.png")
            game.batch.draw(heroTex, bx, by - 26f + offsetY, 38f, 48f)
            game.batch.setColor(Color.WHITE)

            game.fonts.normal.setColor(if (hero.isAlive) C_WHITE else C_GRAY)
            game.fonts.normal.draw(game.batch, hero.name, bx+48f, by+28f)
            game.fonts.tiny.setColor(C_TXT_HP)
            sb.clear(); sb.append("HP ").append(hero.currentHp).append('/').append(hero.maxHp)
            game.fonts.tiny.draw(game.batch, sb, bx+48f, by+8f)
            game.fonts.tiny.setColor(C_TXT_MP)
            sb.clear(); sb.append("MP ").append(hero.currentMp).append('/').append(hero.maxMp)
            game.fonts.tiny.draw(game.batch, sb, bx+48f, by-6f)
            if (hero.statusEffect != StatusEffect.NONE) {
                game.fonts.tiny.setColor(C_TXT_STATUS)
                game.fonts.tiny.draw(game.batch, "[${hero.statusEffect}]", bx+172f, by+8f)
            }
        }

        val ax = W * 0.58f; val ay = H * 0.31f
        when (phase) {
            Phase.PLAYER_ACTION -> {
                game.fonts.small.setColor(C_TXT_ACT)
                game.fonts.small.draw(game.batch, "[ ${currentHero?.name ?: "—"} ]", ax, ay+14f)
                for (i in actionItems.indices) {
                    val sel = i == actionIdx
                    val fnt = if (sel) game.fonts.normal else game.fonts.small
                    fnt.setColor(if (sel) C_WHITE else C_TXT_UNS)
                    fnt.draw(game.batch, "${if (sel) "►" else " "} ${actionItems[i]}", ax, ay-6f-i*38f)
                }
            }
            Phase.PLAYER_SKILL, Phase.PLAYER_SKILL_TARGET -> {
                val skills = currentHero?.skills ?: emptyList()
                game.fonts.small.setColor(C_TXT_SKILL)
                game.fonts.small.draw(game.batch, "Compétences :", ax, ay+14f)
                for (i in skills.indices) {
                    val sk  = skills[i]; val sel = i == skillIdx
                    val hasMP = (currentHero?.currentMp ?: 0) >= sk.mpCost
                    val fnt = if (sel) game.fonts.normal else game.fonts.small
                    fnt.setColor(if (!hasMP) C_GRAY else if (sel) C_WHITE else C_TXT_UNS)
                    sb.clear(); sb.append(if (sel) "► " else "  ").append(sk.name)
                        .append(" (").append(sk.mpCost).append("MP)")
                    fnt.draw(game.batch, sb, ax, ay-2f-i*34f)
                }
            }
            Phase.ENEMY_TURN -> {
                game.fonts.normal.setColor(C_ENEMY_RED)
                game.fonts.normal.draw(game.batch, "Tour des ennemis...", ax, ay)
            }
            Phase.LEVEL_UP -> {
                // FIX REVIEW #8 — affichage level-up
                game.fonts.large.setColor(C_LEVELUP)
                game.fonts.large.draw(game.batch, "NIVEAU SUPÉRIEUR !", W/2f-120f, H/2f+40f)
                for (i in levelUpHeroes.indices) {
                    game.fonts.medium.setColor(C_WHITE)
                    sb.clear(); sb.append(levelUpHeroes[i].name)
                        .append(" → Niveau ").append(levelUpHeroes[i].level)
                    game.fonts.medium.draw(game.batch, sb, W/2f-100f, H/2f-i*36f)
                }
            }
            Phase.VICTORY -> {
                game.fonts.title.setColor(C_LEVELUP)
                game.fonts.title.draw(game.batch, "VICTOIRE !", W/2f-100f, H/2f+20f)
            }
            Phase.GAME_OVER -> {
                game.fonts.title.setColor(C_ENEMY_RED)
                game.fonts.title.draw(game.batch, "GAME OVER", W/2f-90f, H/2f+20f)
            }
            else -> {}
        }

        // Log de combat
        val logY = H * 0.35f + H * 0.20f
        val logList = battleLog.toList()
        for (i in logList.indices.reversed()) {
            val alpha = (1f - i * 0.14f).coerceAtLeast(0.2f)
            game.fonts.tiny.setColor(0.88f, 0.88f, 1f, alpha)
            game.fonts.tiny.draw(game.batch, logList[logList.size - 1 - i],
                W * 0.58f, logY - i * 20f)
        }
        
        // Floating Texts
        for (ft in floatingTexts) {
            ft.color.a = ft.life.coerceIn(0f, 1f)
            game.fonts.medium.setColor(ft.color)
            game.fonts.medium.draw(game.batch, ft.text, ft.x, ft.y)
        }

        game.batch.setColor(C_WHITE)
        game.batch.end()
        
        // Reset camera position after shake
        if (shakeTimer > 0) {
            game.viewport.camera.position.set(W / 2f, H / 2f, 0f)
            game.viewport.camera.update()
        }
        
        game.fonts.resetColors()
        game.batch.shader = oldShader
    }


    override fun resize(w: Int, h: Int) {
        game.viewport.update(w, h, true)
    }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() {
        postProcessShader?.dispose()
    }
}

