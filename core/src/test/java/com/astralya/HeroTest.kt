package com.astralya

import com.astralya.entities.*
import com.astralya.utils.GameRandom
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HeroTest {

    private val rng = GameRandom(seed = 42L)
    private lateinit var nassim:  Hero
    private lateinit var yasmine: Hero
    private lateinit var lwiz:    Hero

    @Before
    fun setUp() {
        nassim  = HeroFactory.createNassim()
        yasmine = HeroFactory.createYasmine()
        lwiz    = HeroFactory.createLwiz()
    }

    // ── takeDamage ────────────────────────────────────────────────────────────

    @Test
    fun `takeDamage reduit currentHp`() {
        val before = nassim.currentHp
        nassim.takeDamage(50)
        assertTrue("HP réduits", nassim.currentHp < before)
    }

    @Test
    fun `takeDamage ne passe pas sous zero`() {
        nassim.takeDamage(99999)
        assertEquals("HP minimum = 0", 0, nassim.currentHp)
    }

    @Test
    fun `mort declaree quand HP atteint zero`() {
        nassim.takeDamage(99999)
        assertFalse("Nassim est mort", nassim.isAlive)
    }

    @Test
    fun `defense reduit les degats`() {
        val heroFaible = HeroFactory.createNassim().also { it.defense = 0 }
        val heroFort   = HeroFactory.createNassim().also { it.defense = 100 }
        val dmgFaible  = heroFaible.takeDamage(100)
        val dmgFort    = heroFort.takeDamage(100)
        assertTrue("Plus de défense = moins de dégâts", dmgFort < dmgFaible)
    }

    @Test
    fun `degats minimum toujours 1`() {
        nassim.defense = 9999
        val dealt = nassim.takeDamage(1)
        assertEquals("Dégâts minimum = 1", 1, dealt)
    }

    // ── heal ──────────────────────────────────────────────────────────────────

    @Test
    fun `heal restaure HP`() {
        nassim.currentHp = 100
        nassim.heal(200)
        assertTrue("HP augmentés", nassim.currentHp > 100)
    }

    @Test
    fun `heal ne depasse pas maxHp`() {
        nassim.heal(99999)
        assertEquals("HP plafonnés à maxHp", nassim.maxHp, nassim.currentHp)
    }

    @Test
    fun `heal retourne la quantite reelle soignee`() {
        nassim.currentHp = nassim.maxHp - 50
        val healed = nassim.heal(200)
        assertEquals("Soin réel = 50", 50, healed)
    }

    // ── MP ────────────────────────────────────────────────────────────────────

    @Test
    fun `useMp deduit les MP`() {
        val before = nassim.currentMp
        nassim.useMp(10)
        assertEquals("MP déduits", before - 10, nassim.currentMp)
    }

    @Test
    fun `useMp retourne false si MP insuffisants`() {
        nassim.currentMp = 0
        assertFalse("useMp échoue sans MP", nassim.useMp(10))
        assertEquals("MP inchangés", 0, nassim.currentMp)
    }

    @Test
    fun `restoreMp ne depasse pas maxMp`() {
        nassim.restoreMp(99999)
        assertEquals("MP plafonnés", nassim.maxMp, nassim.currentMp)
    }

    // ── Level up ──────────────────────────────────────────────────────────────

    @Test
    fun `gainExp retourne true au level up`() {
        val leveled = nassim.gainExp(nassim.expToNextLevel, rng)
        assertTrue("Level-up détecté", leveled)
        assertEquals("Niveau 2", 2, nassim.level)
    }

    @Test
    fun `level up restaure HP et MP au maximum`() {
        nassim.currentHp = 1; nassim.currentMp = 0
        nassim.gainExp(nassim.expToNextLevel, rng)
        assertEquals("HP restaurés au max", nassim.maxHp, nassim.currentHp)
        assertEquals("MP restaurés au max", nassim.maxMp, nassim.currentMp)
    }

    @Test
    fun `stats augmentent au level up`() {
        val atkBefore = nassim.attack
        nassim.gainExp(nassim.expToNextLevel, rng)
        assertTrue("ATK augmentée", nassim.attack > atkBefore)
    }

    @Test
    fun `exp residuelle conservee apres level up`() {
        nassim.gainExp(nassim.expToNextLevel + 50, rng)
        assertEquals("EXP résiduelle = 50", 50, nassim.experience)
    }

    // ── Revive ────────────────────────────────────────────────────────────────

    @Test
    fun `revive ressuscite le heros`() {
        nassim.isAlive = false; nassim.currentHp = 0
        nassim.revive(0.5f)
        assertTrue("Nassim vivant", nassim.isAlive)
        assertEquals("HP = 50% du max", nassim.maxHp / 2, nassim.currentHp)
    }

    @Test
    fun `revive supprime les effets de statut`() {
        nassim.statusEffect = StatusEffect.POISON
        nassim.isAlive = false; nassim.currentHp = 0
        nassim.revive()
        assertEquals("Statut supprimé", StatusEffect.NONE, nassim.statusEffect)
    }

    // ── Stats avec équipement ─────────────────────────────────────────────────

    @Test
    fun `totalAttack inclut bonus arme`() {
        val baseAtk = nassim.totalAttack()
        nassim.weapon = ItemFactory.EPEE_AURORE
        assertTrue("Attaque augmentée avec arme", nassim.totalAttack() > baseAtk)
    }

    @Test
    fun `totalMagic inclut bonus accessoire`() {
        val baseMag = lwiz.totalMagic()
        lwiz.accessory = ItemFactory.ANNEAU_MANA
        assertTrue("Magie augmentée avec accessoire", lwiz.totalMagic() > baseMag)
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    @Test
    fun `nassim est tank DPS`() {
        assertEquals(HeroRole.TANK_DPS, nassim.role)
        assertTrue("Nassim a 4 compétences", nassim.skills.size == 4)
    }

    @Test
    fun `yasmine est support healer`() {
        assertEquals(HeroRole.SUPPORT_HEAL, yasmine.role)
        assertTrue("Yasmine a un skill de soin", yasmine.skills.any { it.type == SkillType.HEAL })
    }

    @Test
    fun `lwiz est mage cosmique`() {
        assertEquals(HeroRole.MAGE, lwiz.role)
        assertTrue("Lwiz a la plus haute magie", lwiz.magic > nassim.magic)
    }
}
