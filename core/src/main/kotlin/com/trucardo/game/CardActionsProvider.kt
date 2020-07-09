package com.trucardo.game

import com.badlogic.gdx.utils.Pool
import ktx.collections.gdxArrayOf

open class CardActionsProvider {
    private var actionPool: Pool<CardAction> = object : Pool<CardAction>(){
         override fun newObject(): CardAction? {
            return CardAction(this@CardActionsProvider)
        }
    }
    private var actions = gdxArrayOf<CardAction>()
    fun actionComplete(action: CardAction?) {
        actions.removeValue(action, true)
        actionPool.free(action)
    }

    @Suppress("LibGDXUnsafeIterator")
    fun update(delta: Float) {
        for (action in actions) {
            action.update(delta)
        }
    }

    fun animate(card: Card?, x: Float, y: Float, z: Float, angle: Float, speed: Float) {
        val action: CardAction = actionPool.obtain()
        action.reset(card!!)
        action.toPosition.set(x, y, z)
        action.toAngle = angle
        action.speed = speed
        actions.add(action)
    }
}