package com.astralya.engine.core

/**
 * Epic 7 — Catalogue centralisé de tous les assets du jeu.
 * Évite les chemins "magiques" éparpillés dans le code.
 */
object AssetCatalog {

    object Sprites {
        const val NASSIM = "sprites/nassim.png"
        const val YASMINE = "sprites/yasmine.png"
        const val LWIZ = "sprites/lwiz.png"
        const val UI_FRAME = "sprites/ui_frame.png"
        const val PORTAL = "sprites/portal.png"
        const val CHEST_CLOSED = "sprites/chest_closed.png"
        const val CHEST_OPEN = "sprites/chest_open.png"
        
        fun enemy(name: String) = "sprites/enemy_$name.png"
        fun battleBg(zone: String) = "sprites/battle_bg_$zone.png"
    }

    object Audio {
        fun music(name: String) = "audio/music_$name.ogg"
        fun sfx(name: String) = "audio/sfx_$name.ogg"
    }

    object Maps {
        fun tmx(name: String) = "maps/$name.tmx"
    }
}
