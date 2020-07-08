package com.trucardo.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3


class Card(suit: Suit, pip: Pip, back: Sprite, front: Sprite){
    private val CARD_WITH_TEXELS = 200f
    private val CARD_HEIGHT_TEXELS = 277f
    private val CARD_WIDTH = 1f
    private val CARD_HEIGHT = CARD_WIDTH * CARD_HEIGHT_TEXELS / CARD_WITH_TEXELS
    private val suit: Suit
    private val pip: Pip

    val vertices: FloatArray
    val indices: ShortArray

    val transform = Matrix4()
    val position = Vector3()
    var angle:Float = 0.0f

    fun update() {
        val z = position.z + 0.5f * Math.abs(MathUtils.sinDeg(angle))
        transform.setToRotation(Vector3.Y, angle)
        transform.trn(position.x, position.y, z)
    }

    companion object {
        private fun convert(front: FloatArray, back: FloatArray): FloatArray {
            return floatArrayOf(
                    front[Batch.X2], front[Batch.Y2], 0f, 0f, 0f, 1f, front[Batch.U2], front[Batch.V2],
                    front[Batch.X1], front[Batch.Y1], 0f, 0f, 0f, 1f, front[Batch.U1], front[Batch.V1],
                    front[Batch.X4], front[Batch.Y4], 0f, 0f, 0f, 1f, front[Batch.U4], front[Batch.V4],
                    front[Batch.X3], front[Batch.Y3], 0f, 0f, 0f, 1f, front[Batch.U3], front[Batch.V3],
                    back[Batch.X1], back[Batch.Y1], 0f, 0f, 0f, -1f, back[Batch.U1], back[Batch.V1],
                    back[Batch.X2], back[Batch.Y2], 0f, 0f, 0f, -1f, back[Batch.U2], back[Batch.V2],
                    back[Batch.X3], back[Batch.Y3], 0f, 0f, 0f, -1f, back[Batch.U3], back[Batch.V3],
                    back[Batch.X4], back[Batch.Y4], 0f, 0f, 0f, -1f, back[Batch.U4], back[Batch.V4]

            )
        }
    }

    init {
        assert(front.texture === back.texture)
        this.suit = suit
        this.pip = pip
        front.setSize(CARD_WIDTH, CARD_HEIGHT)
        back.setSize(CARD_WIDTH, CARD_HEIGHT)
        front.setPosition(-front.width * 0.5f, -front.height * 0.5f)
        back.setPosition(-back.width * 0.5f, -back.height * 0.5f)

        vertices = convert(front.vertices, back.vertices)
        indices = shortArrayOf(0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4)
    }
}