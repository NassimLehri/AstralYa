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
            QuestStep("Parlez à l'Ancien Lyros au village.", targetMapId = "village_depart", targetNpcId = "ancien_village"),
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

    val ALL_QUESTS: Map<String, Quest> = mapOf(
        "quete_principale_1" to QUETE_PRINCIPALE_1,
        "quete_cristal_foret" to QUETE_CRISTAL_FORET,
        "quete_boss_golem" to QUETE_BOSS_GOLEM,
        "quete_ruines_desert" to QUETE_RUINES_DESERT,
        "quete_temple_secret" to QUETE_TEMPLE_SECRET,
        "quete_histoire_morvax" to QUETE_HISTOIRE_MORVAX,
        "quete_final_morvax" to QUETE_FINAL_MORVAX,
        "quete_secondaire_chat" to QUETE_CHAT
    )

    fun getQuest(id: String): Quest? = ALL_QUESTS[id]
    fun getMainQuests(): List<Quest> = ALL_QUESTS.values.filter { it.isMainQuest }
    fun getSideQuests(): List<Quest> = ALL_QUESTS.values.filter { !it.isMainQuest }
}
