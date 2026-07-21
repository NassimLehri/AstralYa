package com.astralya.entities

object ItemFactory {

    // ── Consommables ──────────────────────────────────────────────────────────
    val HERBE_SOIN = Item(
        id = "herbe_soin", name = "Herbe de Soin", type = ItemType.CONSUMABLE,
        description = "Restaure 150 HP à un allié.", hpRestore = 150, value = 50
    )
    val ELIXIR_SOIN = Item(
        id = "elixir_soin", name = "Élixir de Soin", type = ItemType.CONSUMABLE,
        description = "Restaure 400 HP à un allié.", hpRestore = 400, value = 150
    )
    val POTION_MP = Item(
        id = "potion_mp", name = "Potion d'Éther", type = ItemType.CONSUMABLE,
        description = "Restaure 80 MP à un allié.", mpRestore = 80, value = 120
    )
    val ELIXIR_COMPLET = Item(
        id = "elixir_complet", name = "Élixir Complet", type = ItemType.CONSUMABLE,
        description = "Restaure tous les HP et MP.", hpRestore = 9999, mpRestore = 9999, value = 800
    )
    val ANTIDOTE = Item(
        id = "antidote", name = "Antidote", type = ItemType.CONSUMABLE,
        description = "Soigne l'empoisonnement.", value = 80
    )
    val PHOENIX_PLUME = Item(
        id = "phoenix_plume", name = "Plume de Phénix", type = ItemType.CONSUMABLE,
        description = "Ressuscite un allié avec 25% HP.", value = 500
    )

    // ── Armes ─────────────────────────────────────────────────────────────────
    val EPEE_AURORE = Item(
        id = "epee_aurore", name = "Épée Aurore", type = ItemType.WEAPON,
        description = "L'épée légendaire de Nassim, forgée d'énergie stellaire.",
        attackBonus = 45, magicBonus = 10, value = 2000,
        equipableBy = listOf(HeroId.NASSIM)
    )
    val BATON_CRISTAL = Item(
        id = "baton_cristal", name = "Bâton de Cristal", type = ItemType.WEAPON,
        description = "Canal la lumière divine pour Yasmine.",
        attackBonus = 15, magicBonus = 55, value = 1800,
        equipableBy = listOf(HeroId.YASMINE)
    )
    val ORB_COSMIQUE = Item(
        id = "orb_cosmique", name = "Orbe Cosmique", type = ItemType.WEAPON,
        description = "Amplifie la magie cosmique de Lwiz.",
        attackBonus = 10, magicBonus = 70, value = 2200,
        equipableBy = listOf(HeroId.LWIZ)
    )
    val EPEE_ACIER = Item(
        id = "epee_acier", name = "Épée d'Acier", type = ItemType.WEAPON,
        description = "Épée solide pour débuter.", attackBonus = 20, value = 300,
        equipableBy = listOf(HeroId.NASSIM)
    )
    val BATON_BOIS = Item(
        id = "baton_bois", name = "Bâton de Bois", type = ItemType.WEAPON,
        description = "Simple bâton magique.", attackBonus = 5, magicBonus = 20, value = 150,
        equipableBy = listOf(HeroId.YASMINE, HeroId.LWIZ)
    )

    // ── Armures ───────────────────────────────────────────────────────────────
    val ARMURE_STELLAIRE = Item(
        id = "armure_stellaire", name = "Armure Stellaire", type = ItemType.ARMOR,
        description = "Armure forgée sous les étoiles pour Nassim.",
        defenseBonus = 42, value = 2500,
        equipableBy = listOf(HeroId.NASSIM)
    )
    val ROBE_LUMIERE = Item(
        id = "robe_lumiere", name = "Robe de Lumière", type = ItemType.ARMOR,
        description = "Tenue sacrée de Yasmine.",
        defenseBonus = 25, magicBonus = 20, value = 2000,
        equipableBy = listOf(HeroId.YASMINE)
    )
    val CAPE_ETOILES = Item(
        id = "cape_etoiles", name = "Cape des Étoiles", type = ItemType.ARMOR,
        description = "Cape cosmique de Lwiz.",
        defenseBonus = 18, magicBonus = 30, value = 1900,
        equipableBy = listOf(HeroId.LWIZ)
    )
    val ARMURE_CUIR = Item(
        id = "armure_cuir", name = "Armure de Cuir", type = ItemType.ARMOR,
        description = "Protection légère pour tous.", defenseBonus = 12, value = 200,
        equipableBy = listOf(HeroId.NASSIM, HeroId.YASMINE, HeroId.LWIZ)
    )

    // ── Accessoires ───────────────────────────────────────────────────────────
    val ANNEAU_MANA = Item(
        id = "anneau_mana", name = "Anneau de Mana", type = ItemType.ACCESSORY,
        description = "Augmente la magie de 15.", magicBonus = 15, value = 600,
        equipableBy = listOf(HeroId.NASSIM, HeroId.YASMINE, HeroId.LWIZ)
    )
    val AMULETTE_VIE = Item(
        id = "amulette_vie", name = "Amulette de Vie", type = ItemType.ACCESSORY,
        description = "Renforce la défense.", defenseBonus = 10, value = 400,
        equipableBy = listOf(HeroId.NASSIM, HeroId.YASMINE, HeroId.LWIZ)
    )

    // ── Objets de quête ───────────────────────────────────────────────────────
    val CRISTAL_STELLAIRE_1 = Item(
        id = "cristal_1", name = "Cristal Stellaire — Lumière", type = ItemType.KEY_ITEM,
        description = "L'un des Sept Cristaux qui maintiennent Astralya.", value = 0
    )
    val CRISTAL_STELLAIRE_2 = Item(
        id = "cristal_2", name = "Cristal Stellaire — Feu", type = ItemType.KEY_ITEM,
        description = "L'un des Sept Cristaux qui maintiennent Astralya.", value = 0
    )

    // ── Catalogue complet ─────────────────────────────────────────────────────
    val ALL_ITEMS: Map<String, Item> = mapOf(
        "herbe_soin" to HERBE_SOIN,
        "elixir_soin" to ELIXIR_SOIN,
        "potion_mp" to POTION_MP,
        "elixir_complet" to ELIXIR_COMPLET,
        "antidote" to ANTIDOTE,
        "phoenix_plume" to PHOENIX_PLUME,
        "epee_aurore" to EPEE_AURORE,
        "baton_cristal" to BATON_CRISTAL,
        "orb_cosmique" to ORB_COSMIQUE,
        "epee_acier" to EPEE_ACIER,
        "baton_bois" to BATON_BOIS,
        "armure_stellaire" to ARMURE_STELLAIRE,
        "robe_lumiere" to ROBE_LUMIERE,
        "cape_etoiles" to CAPE_ETOILES,
        "armure_cuir" to ARMURE_CUIR,
        "anneau_mana" to ANNEAU_MANA,
        "amulette_vie" to AMULETTE_VIE,
        "cristal_1" to CRISTAL_STELLAIRE_1,
        "cristal_2" to CRISTAL_STELLAIRE_2
    )

    fun getById(id: String): Item? = ALL_ITEMS[id]

    // Inventaire de départ
    fun startingInventory(): List<Pair<String, Int>> = listOf(
        "epee_acier" to 1,
        "baton_bois" to 1,
        "armure_cuir" to 3,
        "herbe_soin" to 5,
        "potion_mp" to 3,
        "antidote" to 2
    )
}
