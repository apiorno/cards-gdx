package com.trucardo.game

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf


class CardDeck(atlas: TextureAtlas, backIndex: Int) {
    private val cards: GdxArray<GdxArray<Card?>> = gdxArrayOf(false,Suit.values().size)
    fun getCard(suit: Suit, pip: Pip): Card? {
        return cards[suit.index][pip.index]
    }

    init {
        for (suit in Suit.values()) {
            val pips :GdxArray<Card?> = GdxArray(false,Pip.values().size)
            for (pip in Pip.values()) {
                val front = atlas.createSprite(suit.type, pip.value)
                val back = atlas.createSprite("back", backIndex)
                pips.add( Card(suit, pip, back, front))
            }
            cards.add(pips)
        }
    }
}