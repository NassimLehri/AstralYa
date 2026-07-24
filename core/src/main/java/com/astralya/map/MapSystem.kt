package com.astralya.map

import com.astralya.entities.Item
import com.astralya.entities.ItemFactory

// ── Structures de données carte ───────────────────────────────────────────────

data class Position(val x: Float, val y: Float)

data class NPC(
    val id: String,
    val name: String,
    val position: Position,
    val dialogues: List<String>,
    val questId: String? = null,
    val shopItems: List<String> = emptyList(),
    val spritePath: String? = null
)

data class Chest(
    val id: String,
    val position: Position,
    val itemId: String,
    val quantity: Int = 1,
    var isOpened: Boolean = false,
    val spritePath: String? = null
)

data class Portal(
    val id: String,
    val position: Position,
    val targetMapId: String,
    val targetX: Float,
    val targetY: Float,
    val spritePath: String? = "sprites/portal.png"
)

data class QuestTrigger(
    val questId: String,
    val position: Position,
    val radius: Float = 64f,
    val triggerType: String = "ENTER"   // ENTER, TALK, INTERACT
)

data class GameMap(
    val id: String,
    val name: String,
    val tilemapFile: String,
    val musicFile: String,
    val widthTiles: Int,
    val heightTiles: Int,
    val encounterRate: Float = 0.15f,   // 0 = no encounters
    val npcs: List<NPC> = emptyList(),
    val chests: List<Chest> = emptyList(),
    val portals: List<Portal> = emptyList(),
    val questTriggers: List<QuestTrigger> = emptyList(),
    val canEncounter: Boolean = true,
    val visualBg: String? = null
)

// ── Définition des 7 cartes ───────────────────────────────────────────────────

object MapRegistry {

    val VILLAGE_DEPART = GameMap(
        id = "village_depart",
        name = "Village d'Étoilebourg",
        tilemapFile = "maps/village.tmx",
        musicFile = "audio/music_village.ogg",
        widthTiles = 30, heightTiles = 20,
        encounterRate = 0f,
        canEncounter = false,
        npcs = listOf(
            NPC(
                id = "marchand_village",
                name = "Marchande Selya",
                position = Position(480f, 256f),
                dialogues = listOf("Bienvenue dans ma boutique ! Je vends tout pour vos aventures."),
                shopItems = listOf("herbe_soin", "potion_mp", "antidote", "epee_acier", "baton_bois", "armure_cuir"),
                spritePath = "sprites/yasmine.png"
            ),
            NPC(
                id = "forgeron",
                name = "Forgeron Torvan",
                position = Position(160f, 256f),
                dialogues = listOf("Je forge les meilleures armes d'Astralya. Apportez-moi des matériaux !"),
                spritePath = "sprites/soldier.png"
            ),
            NPC(
                id = "enfant_perdu",
                name = "Petit Miro",
                position = Position(750f, 256f),
                dialogues = listOf("Mon chat s'est enfui dans la forêt... Pouvez-vous le retrouver ?"),
                questId = "quete_secondaire_chat",
                spritePath = "sprites/male_walkcycle.png"
            )
        ),
        chests = listOf(
            Chest("chest_village_1", Position(64f, 64f), "herbe_soin", 3),
            Chest("chest_village_2", Position(700f, 64f), "potion_mp", 2)
        ),
        portals = listOf(
            Portal("portal_village_foret", Position(920f, 256f), "foret_enchantee", 120f, 300f),
            Portal("portal_village_maison", Position(320f, 288f), "maison_interieur", 240f, 64f)
        ),
        questTriggers = listOf(
            QuestTrigger("quete_principale_1", Position(320f, 288f), 80f, "ENTER")
        ),
        visualBg = "sprites/_map_village_bg.png"
    )

    val FORET_ENCHANTEE = GameMap(
        id = "foret_enchantee",
        name = "Forêt Enchantée de Sylvara",
        tilemapFile = "maps/foret.tmx",
        musicFile = "audio/music_foret.ogg",
        widthTiles = 40, heightTiles = 40,
        encounterRate = 0.18f,
        npcs = listOf(
            NPC(
                id = "dryade",
                name = "Dryade Elysia",
                position = Position(600f, 500f),
                dialogues = listOf(
                    "La forêt gémit... La corruption s'étend.",
                    "Cherchez le Cristal Vert au cœur des arbres anciens.",
                    "Méfiez-vous des loups des ténèbres."
                ),
                questId = "quete_cristal_foret",
                spritePath = "sprites/female_walkcycle.png"
            ),
            NPC(
                id = "chasseur_foret",
                name = "Chasseur Bran",
                position = Position(300f, 250f),
                dialogues = listOf("Ces bois ne sont plus sûrs. Restez sur les sentiers lumineux."),
                spritePath = "sprites/soldier_altcolor.png"
            )
        ),
        chests = listOf(
            Chest("chest_foret_1", Position(200f, 600f), "elixir_soin", 1),
            Chest("chest_foret_2", Position(800f, 150f), "anneau_mana", 1),
            Chest("chest_foret_3", Position(450f, 750f), "herbe_soin", 5)
        ),
        portals = listOf(
            Portal("portal_foret_village", Position(48f, 300f), "village_depart", 650f, 300f),
            Portal("portal_foret_grotte", Position(900f, 400f), "grotte_cristal", 120f, 200f)
        ),
        visualBg = "sprites/_map_foret_bg.png"
    )

    val GROTTE_CRISTAL = GameMap(
        id = "grotte_cristal",
        name = "Grotte des Cristaux Anciens",
        tilemapFile = "maps/grotte.tmx",
        musicFile = "audio/music_grotte.ogg",
        widthTiles = 30, heightTiles = 50,
        encounterRate = 0.22f,
        npcs = listOf(
            NPC(
                id = "nain_mineur",
                name = "Mineur Grum",
                position = Position(200f, 350f),
                dialogues = listOf(
                    "Ces cristaux... ils chantaient autrefois. Plus maintenant.",
                    "Le golem au fond de la grotte protège le Cristal Bleu."
                ),
                spritePath = "sprites/soldier.png"
            )
        ),
        chests = listOf(
            Chest("chest_grotte_1", Position(400f, 100f), "epee_acier", 1),
            Chest("chest_grotte_2", Position(600f, 500f), "fragment_cristal", 3),
            Chest("chest_grotte_3", Position(100f, 600f), "elixir_soin", 2)
        ),
        portals = listOf(
            Portal("portal_grotte_foret", Position(48f, 200f), "foret_enchantee", 800f, 400f),
            Portal("portal_grotte_desert", Position(620f, 620f), "desert_oublie", 200f, 300f)
        ),
        questTriggers = listOf(
            QuestTrigger("quete_boss_golem", Position(500f, 550f), 100f)
        )
    )

    val DESERT_OUBLIE = GameMap(
        id = "desert_oublie",
        name = "Désert Oublié de Sandara",
        tilemapFile = "maps/desert.tmx",
        musicFile = "audio/music_desert.ogg",
        widthTiles = 60, heightTiles = 30,
        encounterRate = 0.20f,
        npcs = listOf(
            NPC(
                id = "nomade_ancien",
                name = "Nomade Zara",
                position = Position(400f, 400f),
                dialogues = listOf(
                    "Ce désert cache des secrets millénaires...",
                    "Le Temple des Étoiles se trouve au nord.",
                    "Méfiez-vous des tempêtes de sable — les serpents surgissent dedans."
                ),
                spritePath = "sprites/female_walkcycle.png"
            ),
            NPC(
                id = "archeologue",
                name = "Archéologue Petra",
                position = Position(700f, 200f),
                dialogues = listOf("J'ai trouvé des ruines ! Elles mentionnent les Sept Cristaux."),
                questId = "quete_ruines_desert",
                spritePath = "sprites/female_walkcycle.png"
            )
        ),
        chests = listOf(
            Chest("chest_desert_1", Position(150f, 700f), "armure_cuir", 1),
            Chest("chest_desert_2", Position(900f, 100f), "potion_mp", 4),
            Chest("chest_desert_3", Position(500f, 850f), "epee_acier", 1)
        ),
        portals = listOf(
            Portal("portal_desert_grotte", Position(100f, 300f), "grotte_cristal", 550f, 550f),
            Portal("portal_desert_temple", Position(1000f, 500f), "temple_etoiles", 150f, 400f)
        )
    )

    val TEMPLE_ETOILES = GameMap(
        id = "temple_etoiles",
        name = "Temple des Sept Étoiles",
        tilemapFile = "maps/temple.tmx",
        musicFile = "audio/music_temple.ogg",
        widthTiles = 25, heightTiles = 25,
        encounterRate = 0.25f,
        npcs = listOf(
            NPC(
                id = "pretre_temple",
                name = "Grand Prêtre Auron",
                position = Position(400f, 600f),
                dialogues = listOf(
                    "Ce temple fut construit pour honorer les Sept Cristaux Stellaires.",
                    "Chaque cristal représente un pilier de l'équilibre d'Astralya.",
                    "Votre destinée est liée à eux, Gardiens."
                ),
                questId = "quete_temple_secret",
                spritePath = "sprites/lwiz.png"
            )
        ),
        chests = listOf(
            Chest("chest_temple_1", Position(200f, 200f), "baton_cristal", 1),
            Chest("chest_temple_2", Position(600f, 100f), "amulette_vie", 1),
            Chest("chest_temple_3", Position(750f, 650f), "elixir_complet", 2)
        ),
        portals = listOf(
            Portal("portal_temple_desert", Position(64f, 400f), "desert_oublie", 900f, 500f),
            Portal("portal_temple_cite", Position(700f, 680f), "cite_volante", 200f, 300f)
        )
    )

    val CITE_VOLANTE = GameMap(
        id = "cite_volante",
        name = "Cité Volante d'Aethara",
        tilemapFile = "maps/cite_volante.tmx",
        musicFile = "audio/music_cite.ogg",
        widthTiles = 45, heightTiles = 35,
        encounterRate = 0.28f,
        npcs = listOf(
            NPC(
                id = "ingenieur_chef",
                name = "Ingénieure Calia",
                position = Position(500f, 400f),
                dialogues = listOf(
                    "Cette cité flotte grâce à l'énergie des cristaux.",
                    "Si Morvax les détruit tous... nous tomberons.",
                    "Le château de Morvax se trouve au nord-est — accessible par le Portail du Néant."
                ),
                spritePath = "sprites/female_walkcycle.png"
            ),
            NPC(
                id = "archiviste",
                name = "Archiviste Nemo",
                position = Position(200f, 500f),
                dialogues = listOf("Les archives révèlent que Morvax était autrefois un Gardien..."),
                questId = "quete_histoire_morvax",
                spritePath = "sprites/male_walkcycle.png"
            )
        ),
        chests = listOf(
            Chest("chest_cite_1", Position(100f, 100f), "orb_cosmique", 1),
            Chest("chest_cite_2", Position(700f, 200f), "armure_stellaire", 1),
            Chest("chest_cite_3", Position(400f, 700f), "robe_lumiere", 1),
            Chest("chest_cite_4", Position(800f, 600f), "cape_etoiles", 1)
        ),
        portals = listOf(
            Portal("portal_cite_temple", Position(100f, 300f), "temple_etoiles", 600f, 600f),
            Portal("portal_cite_chateau", Position(830f, 350f), "chateau_morvax", 150f, 400f)
        )
    )

    val CHATEAU_MORVAX = GameMap(
        id = "chateau_morvax",
        name = "Château de Morvax — Forteresse du Néant",
        tilemapFile = "maps/chateau.tmx",
        musicFile = "audio/music_boss.ogg",
        widthTiles = 40, heightTiles = 60,
        encounterRate = 0.35f,
        npcs = listOf(
            NPC(
                id = "esprit_gardien",
                name = "Esprit d'un ancien Gardien",
                position = Position(300f, 600f),
                dialogues = listOf(
                    "Vous avez atteint la forteresse du Néant...",
                    "Morvax est au sommet. Préparez-vous.",
                    "Les Sept Cristaux brillent encore en vous — c'est votre force."
                ),
                spritePath = "sprites/lwiz.png"
            )
        ),
        chests = listOf(
            Chest("chest_chateau_1", Position(150f, 200f), "elixir_complet", 3),
            Chest("chest_chateau_2", Position(600f, 300f), "phoenix_plume", 2),
            Chest("chest_chateau_3", Position(200f, 700f), "epee_aurore", 1)
        ),
        portals = listOf(
            Portal("portal_chateau_cite", Position(64f, 400f), "cite_volante", 750f, 350f),
            Portal("portal_chateau_etage2", Position(400f, 64f), "chateau_etage_2", 64f, 128f)
        ),
        questTriggers = listOf(
            QuestTrigger("quete_final_morvax", Position(400f, 100f), 120f)
        )
    )

    val MAISON_INTERIEUR = GameMap(
        id = "maison_interieur",
        name = "Maison de Nassim",
        tilemapFile = "maps/maison_interieur.tmx",
        musicFile = "audio/music_village.ogg",
        widthTiles = 15, heightTiles = 12,
        encounterRate = 0f,
        canEncounter = false,
        npcs = listOf(
            NPC(
                id = "maman_nassim",
                name = "Maman",
                position = Position(240f, 200f),
                dialogues = listOf("Fais attention à toi dans tes aventures, mon fils.", "N'oublie pas de bien te reposer."),
                spritePath = "sprites/female_walkcycle.png"
            ),
            NPC(
                id = "ancien_village",
                name = "Ancien Lyros",
                position = Position(350f, 200f),
                dialogues = listOf(
                    "Bienvenue, jeunes héros. Les Cristaux Stellaires disparaissent...",
                    "Morvax, le Seigneur du Néant, corrompt notre monde depuis les ténèbres.",
                    "Vous seuls, les Gardiens d'Astralya, pouvez le stopper."
                ),
                questId = "quete_principale_1",
                spritePath = "sprites/lwiz.png"
            )
        ),
        chests = listOf(
            Chest("chest_maison_1", Position(64f, 160f), "elixir_soin", 1)
        ),
        portals = listOf(
            Portal("portal_maison_village", Position(240f, 32f), "village_depart", 320f, 224f)
        )
    )

    val CHATEAU_ETAGE_2 = GameMap(
        id = "chateau_etage_2",
        name = "Chateau de Morvax — Étage Supérieur",
        tilemapFile = "maps/chateau_etage_2.tmx",
        musicFile = "audio/music_boss.ogg",
        widthTiles = 30, heightTiles = 30,
        encounterRate = 0.38f,
        npcs = emptyList(),
        chests = listOf(
            Chest("chest_chateau_etage2_1", Position(400f, 400f), "armure_stellaire", 1)
        ),
        portals = listOf(
            Portal("portal_chateau_e2_to_e1", Position(64f, 64f), "chateau_morvax", 400f, 100f)
        )
    )

    val ALL_MAPS: Map<String, GameMap> = mapOf(
        "village_depart" to VILLAGE_DEPART,
        "foret_enchantee" to FORET_ENCHANTEE,
        "grotte_cristal" to GROTTE_CRISTAL,
        "desert_oublie" to DESERT_OUBLIE,
        "temple_etoiles" to TEMPLE_ETOILES,
        "cite_volante" to CITE_VOLANTE,
        "chateau_morvax" to CHATEAU_MORVAX,
        "maison_interieur" to MAISON_INTERIEUR,
        "chateau_etage_2" to CHATEAU_ETAGE_2
    )

    fun getMap(id: String): GameMap? = ALL_MAPS[id]
}
