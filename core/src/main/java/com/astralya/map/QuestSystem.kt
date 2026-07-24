package com.astralya.map

data class QuestStep(
    val description: String,
    val targetMapId: String = "",
    val targetNpcId: String = "",
    val requiredItemId: String = ""
)

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<QuestStep>,
    val rewardGold: Int = 0,
    val rewardExp: Int = 0,
    val rewardItemId: String = "",
    val rewardSummonId: String = "",
    val isMainQuest: Boolean = false
)

object QuestRegistry {

    // ── Quêtes principales ────────────────────────────────────────────────────

    val QUETE_PRINCIPALE_1 = Quest(
        id = "quete_principale_1",
        title = "L'Appel des Gardiens",
        description = "L'Ancien Lyros vous révèle la menace de Morvax. Partez à la recherche des Cristaux Stellaires.",
        isMainQuest = true,
        rewardGold = 200, rewardExp = 300,
        steps = listOf(
            QuestStep("Parlez à l'Ancien Lyros dans sa maison.", targetMapId = "maison_interieur", targetNpcId = "ancien_village"),
            QuestStep("Entrez dans la Forêt Enchantée.", targetMapId = "foret_enchantee"),
            QuestStep("Retrouvez la Dryade Elysia.", targetMapId = "foret_enchantee", targetNpcId = "dryade")
        )
    )

    val QUETE_CRISTAL_FORET = Quest(
        id = "quete_cristal_foret",
        title = "Le Cristal de la Forêt",
        description = "La Dryade vous demande de récupérer le Cristal Vert au cœur de la forêt, gardé par les loups corrompus.",
        isMainQuest = true,
        rewardGold = 500, rewardExp = 800, rewardItemId = "cristal_1",
        steps = listOf(
            QuestStep("Traversez la Forêt Enchantée."),
            QuestStep("Défaites les Loups des Ténèbres."),
            QuestStep("Récupérez le Cristal Vert."),
            QuestStep("Retournez voir la Dryade.", targetNpcId = "dryade")
        )
    )

    val QUETE_BOSS_GOLEM = Quest(
        id = "quete_boss_golem",
        title = "Le Gardien de Cristal",
        description = "Un Golem de Cristal corrompu protège le second cristal dans la grotte. Vous devez le vaincre.",
        isMainQuest = true,
        rewardGold = 800, rewardExp = 1200, rewardItemId = "cristal_2",
        steps = listOf(
            QuestStep("Entrez dans la Grotte des Cristaux."),
            QuestStep("Avancez jusqu'à la salle du trône de cristal."),
            QuestStep("Battez le Golem de Cristal."),
            QuestStep("Récupérez le Cristal Bleu.")
        )
    )

    val QUETE_RUINES_DESERT = Quest(
        id = "quete_ruines_desert",
        title = "Les Ruines de Sandara",
        description = "Petra a découvert des ruines dans le désert. Elles contiennent des indices sur Morvax.",
        isMainQuest = false,
        rewardGold = 400, rewardExp = 600,
        steps = listOf(
            QuestStep("Rejoignez Petra dans les ruines.", targetNpcId = "archeologue"),
            QuestStep("Explorez les ruines et trouvez la tablette ancienne.", requiredItemId = "tablette_ancienne"),
            QuestStep("Retournez voir Petra.")
        )
    )

    val QUETE_TEMPLE_SECRET = Quest(
        id = "quete_temple_secret",
        title = "Le Secret du Temple",
        description = "Le Grand Prêtre Auron vous confie la mission de réveiller les Sept Autels du temple.",
        isMainQuest = true,
        rewardGold = 1000, rewardExp = 1500,
        steps = listOf(
            QuestStep("Parlez au Grand Prêtre Auron.", targetNpcId = "pretre_temple"),
            QuestStep("Activez les 3 autels dans le temple."),
            QuestStep("Revenez voir Auron.")
        )
    )

    val QUETE_HISTOIRE_MORVAX = Quest(
        id = "quete_histoire_morvax",
        title = "La Chute d'un Gardien",
        description = "L'Archiviste Nemo révèle que Morvax était autrefois un Gardien. Découvrez ce qui l'a corrompu.",
        isMainQuest = false,
        rewardGold = 600, rewardExp = 900,
        steps = listOf(
            QuestStep("Parlez à l'Archiviste Nemo.", targetNpcId = "archiviste"),
            QuestStep("Récupérez les trois tomes des archives.", requiredItemId = "tome_archives"),
            QuestStep("Retournez voir Nemo.")
        )
    )

    val QUETE_FINAL_MORVAX = Quest(
        id = "quete_final_morvax",
        title = "La Bataille Finale — Morvax",
        description = "Affrontez Morvax au sommet de sa forteresse et restaurez les Sept Cristaux Stellaires.",
        isMainQuest = true,
        rewardGold = 9999, rewardExp = 9999,
        steps = listOf(
            QuestStep("Montez au sommet du Château de Morvax."),
            QuestStep("Affrontez Morvax — Seigneur du Néant."),
            QuestStep("Restaurez les Sept Cristaux Stellaires.")
        )
    )

    val QUETE_REVEIL_DIVINITES = Quest(
        id = "quete_reveil_divinites",
        title = "Le Réveil des Divinités",
        description = "Un ancien rituel permet d'invoquer les esprits protecteurs. Trouvez l'autel sacré dans le temple.",
        isMainQuest = true,
        rewardGold = 1200, rewardExp = 2000,
        rewardSummonId = "summon_loup",
        steps = listOf(
            QuestStep("Atteignez le niveau 10 avec l'équipe."),
            QuestStep("Parlez au Grand Prêtre Auron.", targetNpcId = "pretre_temple"),
            QuestStep("Méditez devant l'autel central du temple.")
        )
    )

    val QUETE_FLEAU_CITE = Quest(
        id = "quete_fleau_cite",
        title = "Le Fléau de la Cité",
        description = "Un Automate corrompu sème le chaos dans la Cité Volante. Éliminez-le pour libérer la cité.",
        isMainQuest = true,
        rewardGold = 2500, rewardExp = 3500,
        rewardItemId = "orb_cosmique",
        steps = listOf(
            QuestStep("Atteignez le niveau 15."),
            QuestStep("Parlez à l'Ingénieure Calia.", targetMapId = "cite_volante", targetNpcId = "ingenieur_chef"),
            QuestStep("Détruisez l'Automate corrompu au centre de la cité.")
        )
    )

    val QUETE_EPREUVE_STELLAIRE = Quest(
        id = "quete_epreuve_stellaire",
        title = "L'Épreuve Stellaire",
        description = "Le Château de Morvax est protégé par un bouclier du néant. Prouvez votre valeur pour le briser.",
        isMainQuest = true,
        rewardGold = 3000, rewardExp = 5000,
        rewardSummonId = "summon_stellar",
        steps = listOf(
            QuestStep("Trouvez les 3 Gardiens Spectraux dans le château."),
            QuestStep("Battez chaque gardien en duel."),
            QuestStep("Utilisez le Cristal Stellaire sur l'autel final.")
        )
    )

    // ── Quêtes secondaires ────────────────────────────────────────────────────

    val QUETE_CHAT = Quest(
        id = "quete_secondaire_chat",
        title = "Le Chat de Miro",
        description = "Retrouvez le chat du petit Miro dans la Forêt Enchantée.",
        isMainQuest = false,
        rewardGold = 150, rewardExp = 200,
        steps = listOf(
            QuestStep("Entrez dans la Forêt Enchantée."),
            QuestStep("Trouvez le chat près des grands chênes."),
            QuestStep("Ramenez le chat à Miro.", targetNpcId = "enfant_perdu")
        )
    )

    val QUETE_MATERIAUX = Quest(
        id = "quete_materiaux",
        title = "Matériaux Précieux",
        description = "Le forgeron a besoin de fragments de cristal rares pour une armure légendaire.",
        isMainQuest = false,
        rewardGold = 800, rewardExp = 1000, rewardItemId = "amulette_vie",
        steps = listOf(
            QuestStep("Récupérez 5 fragments de cristal dans la grotte.", requiredItemId = "fragment_cristal"),
            QuestStep("Rapportez les fragments au forgeron.", targetNpcId = "forgeron")
        )
    )

    val QUETE_OMBRE_GROTTE = Quest(
        id = "quete_ombre_grotte",
        title = "L'Ombre de la Grotte",
        description = "Une créature mystérieuse terrifie les mineurs. Trouvez-la et éliminez-la.",
        isMainQuest = false,
        rewardGold = 1000, rewardExp = 1500,
        steps = listOf(
            QuestStep("Parlez au Mineur Grum.", targetNpcId = "nain_mineur"),
            QuestStep("Trouvez le passage secret dans la grotte."),
            QuestStep("Battez l'Ombre de Cristal.")
        )
    )

    val QUETE_HERITAGE_FORGERON = Quest(
        id = "quete_heritage_forgeron",
        title = "L'Héritage du Forgeron",
        description = "Torvan veut recréer l'épée de son ancêtre. Il lui faut de l'acier stellaire.",
        isMainQuest = false,
        rewardGold = 1500, rewardExp = 2000,
        rewardItemId = "epee_aurore",
        steps = listOf(
            QuestStep("Parlez au Forgeron Torvan.", targetNpcId = "forgeron"),
            QuestStep("Récupérez l'Acier Stellaire dans les profondeurs du château.", requiredItemId = "acier_stellaire"),
            QuestStep("Retournez voir Torvan.")
        )
    )

    val QUETE_GRIMOIRE_PERDU = Quest(
        id = "quete_grimoire_perdu",
        title = "Le Grimoire Perdu",
        description = "Un livre de sorts anciens est caché dans la bibliothèque du Temple.",
        isMainQuest = false,
        rewardGold = 500, rewardExp = 3000,
        rewardItemId = "grimoire_anciens",
        steps = listOf(
            QuestStep("Explorez la bibliothèque du temple."),
            QuestStep("Trouvez le grimoire derrière l'étagère secrète."),
            QuestStep("Déchiffrez le grimoire.")
        )
    )

    val QUETE_SECOURS_DESERT = Quest(
        id = "quete_secours_desert",
        title = "Secours dans le Désert",
        description = "Un marchand s'est perdu dans une tempête de sable.",
        isMainQuest = false,
        rewardGold = 5000, rewardExp = 1000,
        steps = listOf(
            QuestStep("Trouvez le marchand Zara dans le désert.", targetNpcId = "nomade_ancien"),
            QuestStep("Escortez Zara jusqu'au Temple."),
            QuestStep("Recevez votre récompense.")
        )
    )

    val ALL_QUESTS: Map<String, Quest> = mapOf(
        "quete_principale_1" to QUETE_PRINCIPALE_1,
        "quete_cristal_foret" to QUETE_CRISTAL_FORET,
        "quete_boss_golem" to QUETE_BOSS_GOLEM,
        "quete_ruines_desert" to QUETE_RUINES_DESERT,
        "quete_temple_secret" to QUETE_TEMPLE_SECRET,
        "quete_histoire_morvax" to QUETE_HISTOIRE_MORVAX,
        "quete_final_morvax" to QUETE_FINAL_MORVAX,
        "quete_reveil_divinites" to QUETE_REVEIL_DIVINITES,
        "quete_fleau_cite" to QUETE_FLEAU_CITE,
        "quete_epreuve_stellaire" to QUETE_EPREUVE_STELLAIRE,
        "quete_secondaire_chat" to QUETE_CHAT,
        "quete_materiaux" to QUETE_MATERIAUX,
        "quete_ombre_grotte" to QUETE_OMBRE_GROTTE,
        "quete_heritage_forgeron" to QUETE_HERITAGE_FORGERON,
        "quete_grimoire_perdu" to QUETE_GRIMOIRE_PERDU,
        "quete_secours_desert" to QUETE_SECOURS_DESERT
    )

    fun getQuest(id: String): Quest? = ALL_QUESTS[id]
    fun getMainQuests(): List<Quest> = ALL_QUESTS.values.filter { it.isMainQuest }
    fun getSideQuests(): List<Quest> = ALL_QUESTS.values.filter { !it.isMainQuest }
}
