package com.trucardo.game

import com.badlogic.gdx.math.Vector3


class CardAction(private val parent: CardActionsProvider) {
    private lateinit var card: Card
    private val fromPosition = Vector3()
    private var fromAngle = 0f
    val toPosition = Vector3()
    var toAngle = 0f
    var speed = 0f
    private var alpha = 0f
    fun reset(card: Card) {
        this.card = card
        fromPosition.set(card.position)
        fromAngle = card.angle
        alpha = 0f
    }

    fun update(delta: Float) {
        alpha += delta * speed
        if (alpha >= 1f) {
            alpha = 1f
            parent.actionComplete(this)
        }
        card.position.set(fromPosition).lerp(toPosition, alpha)
        card.angle = fromAngle + alpha * (toAngle - fromAngle)
        card.update()
    }

}