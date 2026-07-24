package com.astralya.entities

object HeroFactory {

    fun createNassim(): Hero = Hero(
        id = HeroId.NASSIM,
        name = "Nassim",
        role = HeroRole.TANK_DPS,
        skills = listOf(
            Skill(
                id = "coup_stellaire",
                name = "Coup Stellaire",
                description = "Une frappe chargée d'énergie stellaire.",
                mpCost = 8,
                type = SkillType.ATTACK,
                element = Element.STELLAR,
                basePower = 140,
                animationId = "anim_sword_stellar",
                unlockLevel = 1
            ),
            Skill(
                id = "tempete_astrale",
                name = "Tempête Astrale",
                description = "Déchaîne une tempête qui touche tous les ennemis.",
                mpCost = 18,
                type = SkillType.ATTACK,
                element = Element.STELLAR,
                basePower = 110,
                hitAll = true,
                animationId = "anim_storm",
                unlockLevel = 5
            ),
            Skill(
                id = "protection_divine",
                name = "Protection Divine",
                description = "Augmente la défense de l'équipe.",
                mpCost = 12,
                type = SkillType.BUFF,
                element = Element.LIGHT,
                basePower = 0,
                statusEffect = StatusEffect.SHIELDED,
                animationId = "anim_shield",
                unlockLevel = 10
            ),
            Skill(
                id = "jugement_sept_cieux",
                name = "Jugement des Sept Cieux",
                description = "L'attaque ultime de Nassim — dévastatrice.",
                mpCost = 40,
                type = SkillType.ATTACK,
                element = Element.STELLAR,
                basePower = 280,
                hitAll = true,
                animationId = "anim_judgment",
                unlockLevel = 20
            ),
            Skill(
                id = "aura_guerrier",
                name = "Aura de Guerrier",
                description = "Améliore considérablement l'attaque de l'équipe.",
                mpCost = 25,
                type = SkillType.BUFF,
                element = Element.NEUTRAL,
                basePower = 0,
                statusEffect = StatusEffect.BLESSED,
                animationId = "anim_warrior_aura",
                unlockLevel = 15
            )
        ),
        baseMaxHp = 520,
        baseMaxMp = 120,
        baseAttack = 48,
        baseDefense = 42,
        baseAgility = 28,
        baseMagic = 22
    )

    fun createYasmine(): Hero = Hero(
        id = HeroId.YASMINE,
        name = "Yasmine",
        role = HeroRole.SUPPORT_HEAL,
        skills = listOf(
            Skill(
                id = "soin_astral",
                name = "Soin Astral",
                description = "Restaure les HP d'un allié.",
                mpCost = 10,
                type = SkillType.HEAL,
                element = Element.LIGHT,
                basePower = 0,
                healAmount = 180,
                animationId = "anim_heal",
                unlockLevel = 1
            ),
            Skill(
                id = "bouclier_sacre",
                name = "Bouclier Sacré",
                description = "Protège un allié contre les prochaines attaques.",
                mpCost = 14,
                type = SkillType.BUFF,
                element = Element.LIGHT,
                basePower = 0,
                statusEffect = StatusEffect.SHIELDED,
                animationId = "anim_barrier",
                unlockLevel = 6
            ),
            Skill(
                id = "purification",
                name = "Purification",
                description = "Soigne toute l'équipe et supprime les malédictions.",
                mpCost = 22,
                type = SkillType.HEAL,
                element = Element.LIGHT,
                basePower = 0,
                healAmount = 120,
                hitAll = true,
                animationId = "anim_purify",
                unlockLevel = 12
            ),
            Skill(
                id = "renaissance_astrale",
                name = "Renaissance Astrale",
                description = "Ressuscite un allié tombé avec 50% de HP.",
                mpCost = 35,
                type = SkillType.HEAL,
                element = Element.LIGHT,
                basePower = 0,
                healAmount = -1,    // signal: revive
                animationId = "anim_revive",
                unlockLevel = 18
            ),
            Skill(
                id = "benediction_astrale",
                name = "Bénédiction Astrale",
                description = "Restaure progressivement les MP de l'équipe.",
                mpCost = 30,
                type = SkillType.BUFF,
                element = Element.STELLAR,
                basePower = 0,
                statusEffect = StatusEffect.BLESSED,
                animationId = "anim_blessing",
                unlockLevel = 14
            )
        ),
        baseMaxHp = 380,
        baseMaxMp = 220,
        baseAttack = 22,
        baseDefense = 30,
        baseAgility = 36,
        baseMagic = 55
    )

    fun createLwiz(): Hero = Hero(
        id = HeroId.LWIZ,
        name = "Lwiz",
        role = HeroRole.MAGE,
        skills = listOf(
            Skill(
                id = "eclat_stellaire",
                name = "Éclat Stellaire",
                description = "Projette un rayon d'énergie cosmique.",
                mpCost = 12,
                type = SkillType.ATTACK,
                element = Element.COSMIC,
                basePower = 160,
                animationId = "anim_beam",
                unlockLevel = 1
            ),
            Skill(
                id = "pluie_cometes",
                name = "Pluie de Comètes",
                description = "Fait pleuvoir des comètes sur tous les ennemis.",
                mpCost = 24,
                type = SkillType.ATTACK,
                element = Element.COSMIC,
                basePower = 130,
                hitAll = true,
                animationId = "anim_comet",
                unlockLevel = 8
            ),
            Skill(
                id = "nova_cosmique",
                name = "Nova Cosmique",
                description = "Une explosion cosmique dévastatrice.",
                mpCost = 36,
                type = SkillType.ATTACK,
                element = Element.COSMIC,
                basePower = 240,
                hitAll = true,
                animationId = "anim_nova",
                unlockLevel = 15
            ),
            Skill(
                id = "coeur_constellations",
                name = "Cœur des Constellations",
                description = "Invoque la puissance des constellations.",
                mpCost = 50,
                type = SkillType.ATTACK,
                element = Element.COSMIC,
                basePower = 320,
                hitAll = true,
                animationId = "anim_constellation",
                unlockLevel = 25
            )
        ),
        baseMaxHp = 320,
        baseMaxMp = 300,
        baseAttack = 28,
        baseDefense = 22,
        baseAgility = 40,
        baseMagic = 72
    )

    // ── Summons ──────────────────────────────────────────────────────────────

    val ALL_SUMMONS: List<Summon> = listOf(
        Summon("summon_loup", "Esprit du Loup", "Une meute spectrale attaque tous les ennemis.", 25, Element.DARK, 180, animationId = "anim_summon_wolf"),
        Summon("summon_stellar", "Divinité Stellaire", "Invoque la puissance pure des étoiles.", 60, Element.STELLAR, 450, animationId = "anim_summon_stellar")
    )

    fun createDefaultParty(): List<Hero> = listOf(createNassim(), createYasmine(), createLwiz())

    // ── Combo Skills ──────────────────────────────────────────────────────────

    val comboSkills: List<ComboSkill> = listOf(
        ComboSkill(
            id = "lumiere_astrale",
            name = "Lumière Astrale",
            description = "Yasmine et Lwiz unissent leur magie pour soigner et endommager.",
            requiredHeroes = listOf(HeroId.YASMINE, HeroId.LWIZ),
            mpCostPerHero = 20,
            basePower = 200,
            element = Element.LIGHT,
            animationId = "anim_combo_light"
        ),
        ComboSkill(
            id = "lame_stellaire",
            name = "Lame Stellaire",
            description = "Nassim et Lwiz forgent une lame d'énergie pure.",
            requiredHeroes = listOf(HeroId.NASSIM, HeroId.LWIZ),
            mpCostPerHero = 22,
            basePower = 350,
            element = Element.STELLAR,
            animationId = "anim_combo_blade"
        ),
        ComboSkill(
            id = "rempart_sacre",
            name = "Rempart Sacré",
            description = "Nassim et Yasmine créent un bouclier indestructible.",
            requiredHeroes = listOf(HeroId.NASSIM, HeroId.YASMINE),
            mpCostPerHero = 18,
            basePower = 0,
            element = Element.LIGHT,
            hitAll = false,
            animationId = "anim_combo_shield"
        )
    )
}
