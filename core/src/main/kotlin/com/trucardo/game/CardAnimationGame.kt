package com.trucardo.game

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen

class CardAnimationGame : KtxGame<KtxScreen>() {
    lateinit var modelBatch: ModelBatch
    lateinit var camera :PerspectiveCamera

    override fun create() {
        modelBatch = ModelBatch()
        camera = PerspectiveCamera()
        addScreen(GameScreen(this@CardAnimationGame))
        setScreen<GameScreen>()
    }

}