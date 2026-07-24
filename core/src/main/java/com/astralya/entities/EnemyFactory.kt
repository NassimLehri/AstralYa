package com.astralya.entities

import com.astralya.utils.GameRandom

object EnemyFactory {

    fun createSlimeVert()        = Enemy("slime_vert","Slime Verdâtre",Element.NEUTRAL,80,12,6,10,4,
        listOf(EnemySkill("Bave Toxique",18,statusEffect=StatusEffect.POISON)),15,8,listOf("herbe_soin"))
    fun createLoupSombre()       = Enemy("loup_sombre","Loup des Ténèbres",Element.DARK,140,28,12,32,8,
        listOf(EnemySkill("Morsure Sombre",45),EnemySkill("Hurlement",20,hitAll=true,statusEffect=StatusEffect.STUN)),35,18,listOf("griffe_loup"))
    fun createFeeCorrompue()     = Enemy("fee_corrompue","Fée Corrompue",Element.DARK,110,20,8,40,35,
        listOf(EnemySkill("Malédiction",38,statusEffect=StatusEffect.POISON),EnemySkill("Rayon Obscur",55)),42,22,listOf("aile_fee","poudre_magique"))
    fun createGolemCristal()     = Enemy("golem_cristal","Golem de Cristal",Element.STELLAR,280,38,45,8,15,
        listOf(EnemySkill("Poing de Roc",65),EnemySkill("Éclat Cristallin",50,hitAll=true)),80,45,listOf("fragment_cristal","pierre_dure","amulette_vie"))
    fun createChauveSouris()     = Enemy("chauve_souris_glacee","Chauve-Souris Glacée",Element.NEUTRAL,95,22,10,45,18,
        listOf(EnemySkill("Souffle Glacial",30,statusEffect=StatusEffect.FREEZE)),28,14)
    fun createSerpentSable()     = Enemy("serpent_sable","Serpent du Sable",Element.NEUTRAL,200,42,20,35,12,
        listOf(EnemySkill("Morsure Venimeuse",55,statusEffect=StatusEffect.POISON),EnemySkill("Tourbillon de Sable",40,hitAll=true)),65,38,listOf("ecaille_serpent","venin_pur"))
    fun createScarabeeAncien()   = Enemy("scarabee_ancien","Scarabée Ancien",Element.NEUTRAL,160,35,38,15,5,
        listOf(EnemySkill("Charge Lourde",70)),55,30,listOf("carapace_scarabee"))
    fun createGardienTemple()    = Enemy("gardien_temple","Gardien du Temple",Element.LIGHT,350,55,48,22,40,
        listOf(EnemySkill("Lame Sacrée",80),EnemySkill("Jugement",65,hitAll=true),EnemySkill("Bouclier Divin",0,statusEffect=StatusEffect.SHIELDED)),120,70,listOf("rune_temple","baton_cristal","anneau_mana"))
    fun createAutomate()         = Enemy("automate_volant","Automate Volant",Element.NEUTRAL,420,62,55,40,28,
        listOf(EnemySkill("Laser Précis",90),EnemySkill("Explosion Mécanique",70,hitAll=true)),150,90,listOf("engrenage_ancien","huile_mecanique","orb_cosmique"))
    
    // Nouveaux monstres utilisant les assets intégrés
    fun createOeilNeant()        = Enemy("oeil_neant","Œil du Néant",Element.DARK,380,58,35,45,55,
        listOf(EnemySkill("Regard Pétrifiant",40,statusEffect=StatusEffect.STUN),EnemySkill("Rayon Obscur",70)),140,85,listOf("lentille_noire"))
    fun createCitrouilleMaudite() = Enemy("citrouille_maudite","Citrouille Maudite",Element.DARK,150,32,25,20,40,
        listOf(EnemySkill("Rire Sardonique",30,statusEffect=StatusEffect.POISON)),45,25)
    fun createFleurCarnivore()   = Enemy("fleur_carnivore","Fleur Carnivore",Element.NEUTRAL,120,45,15,35,10,
        listOf(EnemySkill("Morsure Acide",55,statusEffect=StatusEffect.BURN)),50,30,listOf("nectar_pur"))
    fun createVerGeant()         = Enemy("ver_geant","Ver Géant des Sables",Element.NEUTRAL,500,65,40,12,10,
        listOf(EnemySkill("Éboulement",80,hitAll=true),EnemySkill("Morsure Sismique",100)),180,100,listOf("peau_epaisse"))
    fun createFantomeAncien()    = Enemy("fantome_ancien","Fantôme Ancien",Element.DARK,200,40,999,50,70,
        listOf(EnemySkill("Hantise",60,statusEffect=StatusEffect.STUN)),100,60,listOf("essence_fantome"))

    fun createOmbreSentinelle()  = Enemy("ombre_sentinelle","Ombre Sentinelle",Element.DARK,500,72,45,55,60,
        listOf(EnemySkill("Griffes du Néant",100),EnemySkill("Vague d'Obscurité",80,hitAll=true),EnemySkill("Drain de Vie",60,statusEffect=StatusEffect.POISON)),200,120,listOf("essence_ombre"))
    fun createMorvax()           = Enemy("morvax","Morvax — Seigneur du Néant",Element.DARK,9999,120,80,65,110,
        listOf(EnemySkill("Abîme du Néant",180,hitAll=true),EnemySkill("Corruption Stellaire",150,statusEffect=StatusEffect.POISON),
               EnemySkill("Ténèbres Éternelles",200,hitAll=true,statusEffect=StatusEffect.STUN),EnemySkill("Dévoration d'Âme",220),
               EnemySkill("Néant Absolu",350,hitAll=true)),9999,5000,listOf("coeur_neant","cristal_noir","plastron_divinit","anneau_infini"),isBoss=true)

    // FIX PERF #7 — groupSize variable selon la zone (difficulté progressive)
    private fun groupSizeForZone(mapId: String): IntRange = when (mapId) {
        "foret_enchantee" -> 1..2
        "grotte_cristal"  -> 1..2
        "desert_oublie"   -> 2..3
        "temple_etoiles"  -> 2..3
        "cite_volante"    -> 2..4
        "chateau_morvax"  -> 3..4
        else              -> 1..2
    }

    private fun factoriesForZone(mapId: String): List<() -> Enemy> = when (mapId) {
        "foret_enchantee" -> listOf(::createSlimeVert, ::createLoupSombre, ::createFeeCorrompue, ::createCitrouilleMaudite, ::createFleurCarnivore)
        "grotte_cristal"  -> listOf(::createGolemCristal, ::createChauveSouris)
        "desert_oublie"   -> listOf(::createSerpentSable, ::createScarabeeAncien, ::createVerGeant)
        "temple_etoiles"  -> listOf(::createGardienTemple, ::createFantomeAncien)
        "cite_volante"    -> listOf(::createAutomate, ::createOeilNeant)
        "chateau_morvax"  -> listOf(::createOmbreSentinelle)
        else              -> listOf(::createSlimeVert)
    }

    // FIX PERF #6 — rng injecté, plus de .random()
    fun randomEncounterGroup(mapId: String, rng: GameRandom): List<Enemy> {
        val factories = factoriesForZone(mapId)
        val range     = groupSizeForZone(mapId)
        val count     = rng.nextInt(range)
        return List(count) { rng.pick(factories)?.invoke() ?: createSlimeVert() }
    }
}
