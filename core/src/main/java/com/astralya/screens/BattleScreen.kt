package com.astralya.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.astralya.AstralYaGame
import com.astralya.combat.*
import com.astralya.data.GameState
import com.astralya.entities.*
import com.astralya.map.MapRegistry
import com.astralya.screens.MainMenuScreen

class BattleScreen(
    private val game: AstralYaGame,
    private val state: GameState,
    private val enemies: List<Enemy>,
    private val mapId: String,
    private val returnScreen: Screen
) : Screen {

    private val combat = CombatSystem(game.random)
    private val party  = state.party.filter { it.isAlive }
    private val isValid = party.isNotEmpty()

    private var battleState = combat.initCombat(party, enemies)

    private enum class Phase {
        PLAYER_ACTION, PLAYER_TARGET, PLAYER_SKILL, PLAYER_SKILL_TARGET,
        PLAYER_SUMMON,
        ANIMATING, ENEMY_TURN, LEVEL_UP, VICTORY, GAME_OVER
    }

    private var phase      = Phase.PLAYER_ACTION
    private var heroIndex  = 0
    private var actionIdx  = 0
    private var skillIdx   = 0
    private var targetIdx  = 0
    private var summonIdx  = 0
    private var heroesDone = 0

    private var actionItems = listOf("Attaquer", "Compétence", "Invocation", "Objet", "Fuir")
    private val battleLog   = ArrayDeque<String>(6)
    private var animTimer   = 0f
    private var elapsed     = 0f

    private var pendingResults   = emptyList<ActionResult>()
    private var resultIdx        = 0
    private var levelUpHeroes    = emptyList<Hero>()
    private var levelUpShowTimer = 0f

    private var aliveEnemies: List<Enemy> = emptyList()
    private var aliveParty:   List<Hero>  = emptyList()

    private val touchVec = Vector3()

    companion object {
        private val C_BG1       = Color(0.08f, 0.04f, 0.16f, 1f)
        private val C_PANEL_L   = Color(0.05f, 0.05f, 0.18f, 0.92f)
        private val C_PANEL_R   = Color(0.05f, 0.08f, 0.20f, 0.92f)
        private val C_ACTIVE_HL = Color(0.18f, 0.18f, 0.48f, 0.88f)
        private val C_HP_BG     = Color(0.28f, 0f,    0f,    0.85f)
        private val C_MP_BG     = Color(0f,    0f,    0.3f,  0.8f)
        private val C_MP_FG     = Color(0.1f,  0.3f,  1f,    1f)
        private val C_LOG_BG    = Color(0f,    0f,    0f,    0.5f)
        private val C_GOLD      = Color(1f, 0.85f, 0.05f, 1f)
        private val C_WHITE     = Color(1f, 1f, 1f, 1f)
        private val C_GRAY      = Color(0.35f, 0.35f, 0.35f, 1f)
        private val C_TXT_HP    = Color(0.45f, 1f,    0.45f, 1f)
        private val C_TXT_MP    = Color(0.35f, 0.55f, 1f,    1f)
        private val C_TXT_ACT   = Color(1f,    0.88f, 0.3f,  1f)
        private val C_TXT_SKILL = Color(0.68f, 0.82f, 1f,    1f)
        private val C_TXT_ENE   = Color(1f,    0.85f, 0.5f,  1f)
        private val C_TXT_UNS   = Color(0.68f, 0.68f, 0.78f, 1f)
        private val C_ENEMY_RED = Color(1f,    0.38f, 0.38f, 1f)
        private val C_LEVELUP   = Color(1f,    0.9f,  0.1f,  1f)
    }

    private val sb = StringBuilder(64)
    private val cursorColor = Color()
    private val tmpColor = Color()

    private var postProcessShader: ShaderProgram? = null
    private val ambientColor = Color(1f, 1f, 1f, 1f)
    
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
        aliveEnemies = battleState.enemies.filter { it.isAlive }
        aliveParty   = party.filter { it.isAlive }
        update(delta)
        draw()
    }

    private fun update(delta: Float) {
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
            Phase.PLAYER_SUMMON       -> handleSummonMenu()
            Phase.ANIMATING -> {
                animTimer += delta
                if (animTimer >= 0.7f) { animTimer = 0f; showNextResult() }
            }
            Phase.ENEMY_TURN -> {
                animTimer += delta
                if (animTimer >= 1.1f) { animTimer = 0f; doEnemyTurn() }
            }
            Phase.LEVEL_UP -> {
                levelUpShowTimer += delta
                if (levelUpShowTimer >= 2f || confirm()) { levelUpShowTimer = 0f; returnToExploration() }
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

    private fun navigate(currentIdx: Int, size: Int, setter: (Int) -> Unit) {
        if (size == 0) return
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) setter((currentIdx + 1) % size)
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) setter((currentIdx - 1 + size) % size)
    }

    private fun confirm() = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.Z)
    private fun cancel()  = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.X)

    private fun handleActionMenu() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        val ax = W * 0.58f; val ay = H * 0.31f
        
        val h = currentHero
        val items = if (h != null && h.unlockedSummons.isNotEmpty()) 
            listOf("Attaquer", "Compétence", "Invocation", "Objet", "Fuir")
        else 
            listOf("Attaquer", "Compétence", "Objet", "Fuir")
        
        actionItems = items

        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val sc = game.viewport.unproject(touchVec)
            for (i in actionItems.indices) {
                if (sc.x > ax && sc.x < W && sc.y < ay - i*38f && sc.y > ay - (i+1)*38f) {
                    actionIdx = i; executeAction()
                }
            }
        }

        navigate(actionIdx, actionItems.size) { actionIdx = it }
        if (confirm()) executeAction()
    }

    private fun executeAction() {
        val hasSummon = currentHero?.unlockedSummons?.isNotEmpty() ?: false
        
        if (hasSummon) {
            when (actionIdx) {
                0 -> { phase = Phase.PLAYER_TARGET; targetIdx = 0 }
                1 -> { phase = Phase.PLAYER_SKILL;  skillIdx  = 0 }
                2 -> { phase = Phase.PLAYER_SUMMON; summonIdx = 0 }
                3 -> useItemInBattle()
                4 -> tryFlee()
            }
        } else {
            when (actionIdx) {
                0 -> { phase = Phase.PLAYER_TARGET; targetIdx = 0 }
                1 -> { phase = Phase.PLAYER_SKILL;  skillIdx  = 0 }
                2 -> useItemInBattle()
                3 -> tryFlee()
            }
        }
    }

    private fun handleSummonMenu() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        val ax = W * 0.58f; val ay = H * 0.31f
        val h = currentHero ?: return
        
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val sc = game.viewport.unproject(touchVec)
            for (i in h.unlockedSummons.indices) {
                if (sc.x > ax && sc.x < W && sc.y < ay - i*34f && sc.y > ay - (i+1)*34f) {
                    summonIdx = i; val s = h.unlockedSummons[i]
                    if (h.currentMp >= s.mpCost) {
                        startAnim(combat.executeHeroAction(CombatAction.UseSummon(h, s, aliveEnemies), battleState))
                    }
                }
            }
        }
        
        navigate(summonIdx, h.unlockedSummons.size) { summonIdx = it }
        if (confirm()) {
            val s = h.unlockedSummons.getOrNull(summonIdx) ?: return
            if (h.currentMp >= s.mpCost) {
                startAnim(combat.executeHeroAction(CombatAction.UseSummon(h, s, aliveEnemies), battleState))
            }
        }
        if (cancel()) phase = Phase.PLAYER_ACTION
    }

    private fun handleTargetMenu() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val sc = game.viewport.unproject(touchVec)
            for (i in aliveEnemies.indices) {
                val ex = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1))
                val ey = H * 0.65f
                if (Rectangle(ex-40f, ey-40f, 80f, 80f).contains(sc.x, sc.y)) {
                    targetIdx = i; val h = currentHero; val t = aliveEnemies[i]
                    if (h != null) startAnim(combat.executeHeroAction(CombatAction.Attack(h, t), battleState))
                }
            }
        }
        navigate(targetIdx, aliveEnemies.size) { targetIdx = it }
        if (confirm()) {
            val hero = currentHero ?: return; val target = aliveEnemies.getOrNull(targetIdx) ?: return
            startAnim(combat.executeHeroAction(CombatAction.Attack(hero, target), battleState))
        }
        if (cancel()) phase = Phase.PLAYER_ACTION
    }

    private fun handleSkillMenu() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        val ax = W * 0.58f; val ay = H * 0.31f
        val h = currentHero ?: return
        val availableSkills = h.getAvailableSkills()
        
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val sc = game.viewport.unproject(touchVec)
            for (i in availableSkills.indices) {
                if (sc.x > ax && sc.x < W && sc.y < ay - i*34f && sc.y > ay - (i+1)*34f) {
                    skillIdx = i; val sk = availableSkills[i]
                    if (h.currentMp >= sk.mpCost) {
                        if (sk.hitAll) startAnim(combat.executeHeroAction(CombatAction.UseSkill(h, sk, null), battleState))
                        else { phase = Phase.PLAYER_SKILL_TARGET; targetIdx = 0 }
                    }
                }
            }
        }
        navigate(skillIdx, availableSkills.size) { skillIdx = it }
        if (confirm()) {
            val sk = availableSkills.getOrNull(skillIdx) ?: return
            if (h.currentMp >= sk.mpCost) {
                if (sk.hitAll) startAnim(combat.executeHeroAction(CombatAction.UseSkill(h, sk, null), battleState))
                else { phase = Phase.PLAYER_SKILL_TARGET; targetIdx = 0 }
            }
        }
        if (cancel()) phase = Phase.PLAYER_ACTION
    }

    private fun handleSkillTargetMenu() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        if (Gdx.input.justTouched()) {
            touchVec.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val sc = game.viewport.unproject(touchVec)
            for (i in aliveEnemies.indices) {
                val ex = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1))
                val ey = H * 0.65f
                if (Rectangle(ex-40f, ey-40f, 80f, 80f).contains(sc.x, sc.y)) {
                    targetIdx = i; val h = currentHero; val sk = h?.getAvailableSkills()?.getOrNull(skillIdx); val t = aliveEnemies[i]
                    if (h != null && sk != null) startAnim(combat.executeHeroAction(CombatAction.UseSkill(h, sk, t), battleState))
                }
            }
        }
        navigate(targetIdx, aliveEnemies.size) { targetIdx = it }
        if (confirm()) {
            val h = currentHero ?: return; val sk = h.getAvailableSkills().getOrNull(skillIdx) ?: return; val t = aliveEnemies.getOrNull(targetIdx) ?: return
            startAnim(combat.executeHeroAction(CombatAction.UseSkill(h, sk, t), battleState))
        }
        if (cancel()) phase = Phase.PLAYER_SKILL
    }

    private val currentHero: Hero? get() = party.getOrNull(heroIndex)

    private fun startAnim(results: List<ActionResult>) { pendingResults = results; resultIdx = 0; phase = Phase.ANIMATING; animTimer = 0f; showNextResult() }

    private fun showNextResult() {
        if (resultIdx < pendingResults.size) {
            val res = pendingResults[resultIdx]; addLog(res.message)
            if (res.damageDealt > 0 || res.healingDone > 0) applyResultVisuals(res)
            resultIdx++
        } else {
            battleState = combat.checkCombatEnd(battleState)
            if (battleState.playerWon) onVictory() else if (battleState.isOver) { phase = Phase.GAME_OVER; animTimer = 0f } else advanceTurn()
        }
    }

    private fun applyResultVisuals(res: ActionResult) {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        if (res.damageDealt > 0 || res.isSummon) {
            shakeTimer = if (res.isSummon) 0.8f else if (res.critical) 0.4f else 0.2f
            shakeIntensity = if (res.isSummon) 12f else if (res.critical) 8f else 4f
            
            val tx: Float; val ty: Float
            if (phase == Phase.ANIMATING || phase == Phase.PLAYER_SUMMON) {
                val eIdx = targetIdx.coerceIn(0, aliveEnemies.size - 1); tx = W * 0.20f + eIdx * (W / (aliveEnemies.size + 1).coerceAtLeast(1)); ty = H * 0.65f
                aliveEnemies.getOrNull(eIdx)?.let { flashTimers[it] = 0.15f }
                if (res.isSummon) aliveEnemies.forEach { flashTimers[it] = 0.3f }
            } else {
                val hIdx = heroIndex.coerceIn(0, party.size - 1); tx = 50f; ty = H * 0.31f - hIdx * 80f
                party.getOrNull(hIdx)?.let { flashTimers[it] = 0.15f }
            }
            
            if (res.damageDealt > 0) {
                floatingTexts.add(FloatingText(res.damageDealt.toString(), if (res.critical) Color.ORANGE else Color.RED, tx, ty + 20f, 1.0f))
                game.assetLoader.getSound(if (res.critical) "audio/sfx_critical.ogg" else "audio/sfx_hit.ogg").play()
            }
            if (res.isSummon) {
                game.assetLoader.getSound("audio/sfx_boss_appear.ogg").play()
            }
        }
        if (res.healingDone > 0) {
            floatingTexts.add(FloatingText("+${res.healingDone}", Color.GREEN, W * 0.25f, H * 0.3f, 1.0f))
            game.assetLoader.getSound("audio/sfx_heal.ogg").play()
        }
    }

    private fun advanceTurn() {
        heroesDone++
        val alive = party.filter { it.isAlive }
        if (heroesDone >= alive.size || party.isEmpty()) {
            combat.applyEndOfTurnEffects(battleState).forEach { addLog(it) }
            heroesDone = 0; heroIndex = 0; phase = Phase.ENEMY_TURN; animTimer = 0f
        } else {
            heroIndex = (heroIndex + 1) % party.size
            var safety = 0
            while (!party[heroIndex].isAlive && safety < party.size) {
                heroIndex = (heroIndex + 1) % party.size
                safety++
            }
            phase = Phase.PLAYER_ACTION; actionIdx = 0
        }
    }

    private fun doEnemyTurn() {
        aliveEnemies.forEach { enemy -> combat.executeEnemyTurn(enemy, battleState).forEach { addLog(it.message) } }
        battleState = combat.checkCombatEnd(battleState); if (battleState.playerWon) onVictory() else if (battleState.isOver) { phase = Phase.GAME_OVER; animTimer = 0f } else { heroIndex = 0; heroesDone = 0; phase = Phase.PLAYER_ACTION }
    }

    private fun tryFlee() {
        if (combat.canFlee(party, enemies)) { addLog("L'équipe prend la fuite !"); returnToExploration() }
        else { addLog("Impossible de fuir !"); phase = Phase.ENEMY_TURN; animTimer = 0f }
    }

    private fun useItemInBattle() {
        val item = state.inventory.keys.mapNotNull { ItemFactory.getById(it) }.firstOrNull { it.hpRestore > 0 }
        if (item != null) {
            val target = aliveParty.minByOrNull { it.currentHp } ?: return
            target.heal(item.hpRestore); state.removeItem(item.id); addLog("${currentHero?.name} utilise ${item.name} sur ${target.name}."); advanceTurn()
        } else { addLog("Aucun objet utilisable !"); phase = Phase.PLAYER_ACTION }
    }

    private fun onVictory() {
        val rewards = combat.calculateRewards(enemies)
        val levelsBefore = party.associate { it.id to it.level }
        levelUpHeroes = state.applyCombatRewards(rewards.experience, rewards.gold, rewards.items, game.random)
        
        addLog("Victoire !  +${rewards.experience} EXP  +${rewards.gold} Or")
        if (rewards.items.isNotEmpty()) {
            rewards.items.forEach { addLog("Objet trouvé : ${ItemFactory.getById(it)?.name ?: it}") }
        }

        enemies.filter { it.isBoss }.forEach { state.defeatBoss(it.id) }
        
        if (levelUpHeroes.isNotEmpty()) { 
            levelUpHeroes.forEach { h ->
                addLog("${h.name} monte au niveau ${h.level} !") 
                val oldLvl = levelsBefore[h.id] ?: (h.level - 1)
                h.skills.filter { it.unlockLevel in (oldLvl + 1)..h.level }.forEach {
                    addLog("✨ NOUVEAU SORT : ${it.name} !")
                }
            }
            phase = Phase.LEVEL_UP; levelUpShowTimer = 0f 
        }
        else { phase = Phase.VICTORY; animTimer = 0f }
    }

    private fun returnToExploration() {
        runCatching { game.audioManager.playMusic(game.assetLoader.getMusic(MapRegistry.getMap(state.currentMapId)?.musicFile ?: "audio/music_village.ogg")) }
        game.setScreen(returnScreen); dispose()
    }

    private fun draw() {
        val W = game.viewport.worldWidth; val H = game.viewport.worldHeight
        Gdx.gl.glClearColor(0.05f, 0.02f, 0.1f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        game.batch.projectionMatrix = game.viewport.camera.combined; game.shapeRenderer.projectionMatrix = game.viewport.camera.combined
        
        if (shakeTimer > 0) {
            val sx = (Math.random().toFloat() - 0.5f) * 2f * shakeIntensity; val sy = (Math.random().toFloat() - 0.5f) * 2f * shakeIntensity
            game.viewport.camera.translate(sx, sy, 0f); game.viewport.camera.update()
            game.batch.projectionMatrix = game.viewport.camera.combined; game.shapeRenderer.projectionMatrix = game.viewport.camera.combined
        }

        game.batch.color = ambientColor
        val shape = game.shapeRenderer
        shape.begin(ShapeRenderer.ShapeType.Filled); shape.color = C_BG1; shape.rect(0f, 0f, W, H); shape.end()

        // FIX BACKGROUND: IT IS A PLATFORM SHEET
        game.batch.begin()
        val bgTex = game.assetLoader.getBattleBackground(mapId)
        // Draw only the first platform (top left 256x144 part of the sheet)
        game.batch.draw(bgTex, 0f, H * 0.35f, W, H * 0.65f, 0, 0, 256, 144, false, false)
        game.batch.end()

        shape.begin(ShapeRenderer.ShapeType.Filled)
        for (i in aliveEnemies.indices) {
            val enemy = aliveEnemies.getOrNull(i) ?: continue
            val ex = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1)); val ey = H * 0.65f; val size = if (enemy.isBoss) 88f else 52f
            if (enemy.isBoss) { shape.color = C_GOLD; shape.triangle(ex-22f, ey+size/2f, ex, ey+size/2f+22f, ex+22f, ey+size/2f) }
            if ((phase == Phase.PLAYER_TARGET || phase == Phase.PLAYER_SKILL_TARGET) && i == targetIdx) {
                cursorColor.set(1f, 1f, 0f, 0.55f + MathUtils.sin(elapsed * 5f) * 0.35f); shape.color = cursorColor; shape.rect(ex - size/2f - 5f, ey - size/2f - 5f, size + 10f, size + 10f)
            }
            val hpR = enemy.currentHp.toFloat() / enemy.maxHp.coerceAtLeast(1)
            shape.color = C_HP_BG; shape.rect(ex - 36f, ey + size/2f + 8f, 72f, 9f)
            tmpColor.set(hpR * 0.9f, (1f-hpR)*0.1f + hpR*0.6f, 0f, 1f); shape.color = tmpColor; shape.rect(ex - 36f, ey + size/2f + 8f, 72f * hpR, 9f)
        }
        shape.color = C_PANEL_L; shape.rect(0f, 0f, W * 0.56f, H * 0.35f)
        shape.color = C_PANEL_R; shape.rect(W * 0.56f, 0f, W * 0.44f, H * 0.35f)
        for (i in party.indices) {
            val h = party.getOrNull(i) ?: continue
            val by = H * 0.31f - i * 80f
            if (i == heroIndex && phase == Phase.PLAYER_ACTION) { shape.color = C_ACTIVE_HL; shape.rect(2f, by - 32f, W * 0.55f - 4f, 76f) }
            val hpR = h.currentHp.toFloat() / h.maxHp.coerceAtLeast(1); val mpR = h.currentMp.toFloat() / h.maxMp.coerceAtLeast(1)
            shape.color = C_HP_BG; shape.rect(68f, by+10f, 118f, 10f)
            tmpColor.set(hpR*0.8f, hpR*0.6f, 0.1f, 1f); shape.color = tmpColor; shape.rect(68f, by+10f, 118f*hpR, 10f)
            shape.color = C_MP_BG; shape.rect(68f, by-4f, 118f, 8f)
            shape.color = C_MP_FG; shape.rect(68f, by-4f, 118f*mpR, 8f)
        }
        shape.color = C_LOG_BG; shape.rect(W * 0.57f, H * 0.35f, W * 0.43f - 4f, H * 0.22f); shape.end()

        game.batch.color = Color.WHITE
        if (postProcessShader != null) game.batch.shader = postProcessShader
        game.batch.begin()
        for (i in aliveEnemies.indices) {
            val e = aliveEnemies.getOrNull(i) ?: continue
            val ex = W * 0.20f + i * (W / (aliveEnemies.size + 1).coerceAtLeast(1)); val ey = H * 0.65f; val size = if (e.isBoss) 120f else 64f
            if (flashTimers.containsKey(e)) game.batch.setColor(Color.WHITE) else game.batch.setColor(C_WHITE)
            // CROP ENEMY SPRITE (use first frame of the sheet)
            val eTex = game.assetLoader.getEnemyTexture(e.id)
            val eRegions = TextureRegion.split(eTex, 64, 64)
            val eFrame = if (eRegions.isNotEmpty() && eRegions[0].isNotEmpty()) eRegions[0][0] else TextureRegion(eTex)
            
            game.batch.draw(eFrame, ex - size/2f, ey - size/2f, size, size); game.batch.setColor(C_WHITE)
            game.fonts.small.setColor(C_TXT_ENE); game.fonts.small.draw(game.batch, e.name, ex - 48f, ey - size/2f - 6f)
            game.fonts.tiny.setColor(C_WHITE); sb.clear(); sb.append(e.currentHp).append('/').append(e.maxHp); game.fonts.tiny.draw(game.batch, sb, ex - 28f, ey - size/2f - 20f)
        }

        // DRESSED HEROES IN BATTLE
        for (i in party.indices) {
            val h = party.getOrNull(i) ?: continue
            val by = H * 0.31f - i * 80f
            val baseColor = if (h.isAlive) Color.WHITE else Color.GRAY
            if (flashTimers.containsKey(h)) game.batch.setColor(Color.WHITE) else game.batch.setColor(baseColor)
            var oy = if (i == heroIndex && h.isAlive && phase == Phase.PLAYER_ACTION) MathUtils.sin(elapsed * 6f) * 4f else 0f

            // RESTORE UNIQUE HERO IMAGES IN BATTLE
            val hLayers = mutableListOf<Texture>()
            val baseP = when(h.id) {
                HeroId.NASSIM -> "sprites/nassim.png"
                HeroId.YASMINE -> "sprites/yasmine.png"
                HeroId.LWIZ -> "sprites/lwiz.png"
            }
            try { hLayers.add(game.assetLoader.getTexture(baseP)) } catch(e: Exception) {}
            
            for (tex in hLayers) {
                val regions = TextureRegion.split(tex, 64, 64)
                val frame = if (regions.isNotEmpty() && regions[0].isNotEmpty()) regions[0][0] else TextureRegion(tex)
                game.batch.draw(frame, 20f, by - 26f + oy, 48f, 48f)
            }

            game.batch.setColor(Color.WHITE)
            game.fonts.normal.setColor(if (h.isAlive) C_WHITE else C_GRAY); game.fonts.normal.draw(game.batch, h.name, 68f, by+28f)
            game.fonts.tiny.setColor(C_TXT_HP); sb.clear(); sb.append("HP ").append(h.currentHp).append('/').append(h.maxHp); game.fonts.tiny.draw(game.batch, sb, 68f, by+8f)
            game.fonts.tiny.setColor(C_TXT_MP); sb.clear(); sb.append("MP ").append(h.currentMp).append('/').append(h.maxMp); game.fonts.tiny.draw(game.batch, sb, 68f, by-6f)
            
            // Status Icons / Highlights
            if (h.statusEffect != StatusEffect.NONE) { 
                val statusColor = when(h.statusEffect) {
                    StatusEffect.POISON -> Color.GREEN
                    StatusEffect.BURN -> Color.ORANGE
                    StatusEffect.FREEZE -> Color.CYAN
                    StatusEffect.STUN -> Color.YELLOW
                    StatusEffect.BLESSED -> Color.GOLD
                    StatusEffect.SHIELDED -> Color.WHITE
                    else -> Color.WHITE
                }
                game.fonts.tiny.setColor(statusColor)
                game.fonts.tiny.draw(game.batch, "[${h.statusEffect}]", 180f, by+8f) 
            }
        }

        val ax = W * 0.58f; val ay = H * 0.31f
        when (phase) {
            Phase.PLAYER_ACTION -> {
                game.fonts.small.setColor(C_TXT_ACT); game.fonts.small.draw(game.batch, "[ ${currentHero?.name ?: "—"} ]", ax, ay+14f)
                for (i in actionItems.indices) {
                    val item = actionItems.getOrNull(i) ?: continue
                    val sel = i == actionIdx; val fnt = if (sel) game.fonts.normal else game.fonts.small
                    fnt.setColor(if (sel) C_WHITE else C_TXT_UNS); fnt.draw(game.batch, "${if (sel) "►" else " "} $item", ax, ay-6f-i*38f)
                }
            }
            Phase.PLAYER_SKILL, Phase.PLAYER_SKILL_TARGET -> {
                val sks = currentHero?.getAvailableSkills() ?: emptyList()
                game.fonts.small.setColor(C_TXT_SKILL); game.fonts.small.draw(game.batch, "Compétences :", ax, ay+14f)
                for (i in sks.indices) {
                    val sk = sks.getOrNull(i) ?: continue
                    val sel = i == skillIdx; val hasMP = (currentHero?.currentMp ?: 0) >= sk.mpCost; val fnt = if (sel) game.fonts.normal else game.fonts.small
                    fnt.setColor(if (!hasMP) C_GRAY else if (sel) C_WHITE else C_TXT_UNS); sb.clear(); sb.append(if (sel) "► " else "  ").append(sk.name).append(" (").append(sk.mpCost).append("MP)"); fnt.draw(game.batch, sb, ax, ay-2f-i*34f)
                }
            }
            Phase.PLAYER_SUMMON -> {
                val sums = currentHero?.unlockedSummons ?: emptyList()
                game.fonts.small.setColor(C_GOLD); game.fonts.small.draw(game.batch, "Invocations :", ax, ay+14f)
                for (i in sums.indices) {
                    val s = sums.getOrNull(i) ?: continue
                    val sel = i == summonIdx; val hasMP = (currentHero?.currentMp ?: 0) >= s.mpCost; val fnt = if (sel) game.fonts.normal else game.fonts.small
                    fnt.setColor(if (!hasMP) C_GRAY else if (sel) C_WHITE else C_TXT_UNS); sb.clear(); sb.append(if (sel) "► " else "  ").append(s.name).append(" (").append(s.mpCost).append("MP)"); fnt.draw(game.batch, sb, ax, ay-2f-i*34f)
                }
            }
            Phase.ENEMY_TURN -> { game.fonts.normal.setColor(C_ENEMY_RED); game.fonts.normal.draw(game.batch, "Tour des ennemis...", ax, ay) }
            Phase.LEVEL_UP -> {
                val pulse = 0.8f + MathUtils.sin(elapsed * 6f) * 0.2f
                game.fonts.large.setColor(1f, 0.9f, 0.1f, pulse)
                game.fonts.large.draw(game.batch, "NIVEAU SUPÉRIEUR !", W/2f-140f, H/2f+60f)
                
                for (i in levelUpHeroes.indices) { 
                    val hero = levelUpHeroes.getOrNull(i) ?: continue
                    game.fonts.medium.setColor(C_WHITE); sb.clear(); sb.append(hero.name).append(" → Niveau ").append(hero.level)
                    game.fonts.medium.draw(game.batch, sb, W/2f-100f, H/2f+10f-i*40f) 
                    
                    // Show a hint if a new skill was likely unlocked
                    game.fonts.tiny.setColor(Color.GOLD)
                    game.fonts.tiny.draw(game.batch, "Nouveaux pouvoirs débloqués !", W/2f-100f, H/2f-15f-i*40f)
                }
            }
            Phase.VICTORY -> { game.fonts.title.setColor(C_LEVELUP); game.fonts.title.draw(game.batch, "VICTOIRE !", W/2f-100f, H/2f+20f) }
            Phase.GAME_OVER -> { game.fonts.title.setColor(C_ENEMY_RED); game.fonts.title.draw(game.batch, "GAME OVER", W/2f-90f, H/2f+20f) }
            else -> {}
        }
        val logY = H * 0.35f + H * 0.20f; val logList = battleLog.toList()
        for (i in logList.indices.reversed()) { 
            val alpha = (1f - i * 0.14f).coerceAtLeast(0.2f)
            val msg = logList.getOrNull(logList.size - 1 - i) ?: continue
            game.fonts.tiny.setColor(0.88f, 0.88f, 1f, alpha); game.fonts.tiny.draw(game.batch, msg, W * 0.58f, logY - i * 20f) 
        }
        for (ft in floatingTexts) { ft.color.a = ft.life.coerceIn(0f, 1f); game.fonts.medium.setColor(ft.color); game.fonts.medium.draw(game.batch, ft.text, ft.x, ft.y) }
        game.batch.setColor(C_WHITE); game.batch.end()
        if (shakeTimer > 0) { game.viewport.camera.position.set(W / 2f, H / 2f, 0f); game.viewport.camera.update() }
        game.fonts.resetColors(); game.batch.shader = null
    }

    override fun resize(w: Int, h: Int) { game.viewport.update(w, h, true) }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}
    override fun dispose() { postProcessShader?.dispose() }
}
