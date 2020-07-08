package com.trucardo.game

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen


class CardAnimation3D : KtxGame<KtxScreen>() {
    lateinit var modelBatch: ModelBatch
    lateinit var camera :PerspectiveCamera

    override fun create() {
        modelBatch = ModelBatch()
        camera = PerspectiveCamera()
        addScreen(FirstScreen(this@CardAnimation3D))
        setScreen<FirstScreen>()
    }

}