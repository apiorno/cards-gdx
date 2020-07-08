package com.trucardo.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import ktx.app.KtxScreen
import kotlin.math.min
import kotlin.math.tan


class FirstScreen(private val game: CardAnimation3D) : KtxScreen {



    private lateinit var atlas: TextureAtlas
    private val modelBatch = game.modelBatch
    private val camera = game.camera
    private lateinit var cameraController : CameraInputController
    private lateinit var deck: CardDeck
    private lateinit var cards: CardBatch
    private lateinit var tableTopModel: Model
    private lateinit var tableTop: ModelInstance
    private lateinit var environment: Environment
    private lateinit var shadowLight: DirectionalShadowLight
    private lateinit var shadowBatch: ModelBatch
    private lateinit var actions: CardActions
    private val MINIMUM_VIEWPORT_SIZE = 5f
    private var spawnTimer = -1f
    private var pipIdx = -1
    private var suitIdx = 0
    private val INITIAL_CARD_POSITION_X =0f
    private val CARD_POSITIONS_X_OFFSET = 0.5f
    private val CARD_POSITIONS_Y_OFFSET = CARD_POSITIONS_X_OFFSET
    //private val MAX_CARD_POSITION_X = 6f // FOR 13 CARDS
    private val MAX_CARD_POSITION_X = 5.5f // FOR 12 CARDS

    private var spawnX = -0.5f
    private var spawnY = 0f
    private var spawnZ = 0f

    override fun show() {
        camera.position.set(0f, 0f, 10f)
        camera.lookAt(0f, 0f, 0f)
        cameraController = CameraInputController(camera)
        Gdx.input.inputProcessor = cameraController

        atlas = TextureAtlas(Gdx.files.internal("assets/spanish_deck.atlas"))
        val material = Material(
                TextureAttribute.createDiffuse(atlas.textures.first()),
                BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f))
        cards = CardBatch(material)
        deck = CardDeck(atlas, -1)
        //deck = CardDeck(atlas, 3)
        //val card = deck.getCard(Suit.Spades, Pip.King)
        val card = deck.getCard(Suit.Spade, Pip.King)
        card!!.position[3.5f, -2.5f] = 0.01f
        card.angle = 180f
        card.update()
        cards.add(card)

        val builder = ModelBuilder()
        builder.begin()
        builder.node().id = "top"
        val part =builder.part("top", GL20.GL_TRIANGLES, Usage.Position.toLong() or Usage.Normal.toLong(),
                Material(ColorAttribute.createDiffuse(Color(0x63750A))))
        BoxShapeBuilder.build(part,0f, 0f, -0.5f, 20f, 20f, 1f)
        tableTopModel = builder.end()
        tableTop = ModelInstance(tableTopModel)

        shadowBatch = ModelBatch(DepthShaderProvider())
        shadowLight = DirectionalShadowLight(1024, 1024, 10f, 10f, 1f, 20f)
        shadowLight[0.8f, 0.8f, 0.8f, -.4f, -.4f] = -.4f


        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f))
        environment.shadowMap = shadowLight

        actions = CardActions()
    }

    override fun render(delta: Float) {
        val interpolatedDeltaTime = min(1 / 30f, delta)

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        cameraController.update()

        if (spawnTimer < 0) {
            if (Gdx.input.justTouched()) spawnTimer = 1f
        } else if (interpolatedDeltaTime.let { spawnTimer -= it; spawnTimer } <= 0f) {
            spawnTimer = 0.25f
            spawn()
        }

        actions.update(interpolatedDeltaTime)

        shadowLight.begin(Vector3.Zero, Vector3.Zero)
        shadowBatch.begin(shadowLight.camera)
        shadowBatch.render(cards)
        shadowBatch.end()
        shadowLight.end()

        modelBatch.begin(camera)
        modelBatch.render(tableTop, environment)
        modelBatch.render(cards, environment)
        modelBatch.end()
    }
    private fun spawn() {
        if (++pipIdx >= Pip.values().size) {
            pipIdx = 0
            suitIdx = (suitIdx + 1) % Suit.values().size
        }
        val suit = Suit.values()[suitIdx]
        val pip = Pip.values()[pipIdx]
        Gdx.app.log("Spawn", "$suit - $pip")
        val card = deck.getCard(suit, pip)
        card!!.position[3.5f, -2.5f] = 0.01f
        card.angle = 180f
        if (!cards.contains(card)) cards.add(card)
        spawnX += CARD_POSITIONS_X_OFFSET
        if (spawnX > MAX_CARD_POSITION_X) {
            spawnX = INITIAL_CARD_POSITION_X
            spawnY = (spawnY + CARD_POSITIONS_Y_OFFSET) % 2f
        }
        spawnZ += 0.001f
        actions.animate(card, -3.5f + spawnX, 2.5f - spawnY, 0.01f + spawnZ, 0f, 1f)
    }

    override fun resize(width: Int, height: Int) {
        var halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f
        if (height > width) halfHeight *= height.toFloat() / width.toFloat()
        val halfFovRadians: Float = MathUtils.degreesToRadians * camera.fieldOfView * 0.5f
        val distance = halfHeight / tan(halfFovRadians.toDouble()).toFloat()

        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        camera.position.set(0f, 0f, distance)
        camera.lookAt(0f, 0f, 0f)
        camera.update()
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        game.modelBatch.dispose()
        atlas.dispose()
        cards.dispose()
        tableTopModel.dispose()
        shadowBatch.dispose()
        shadowLight.dispose()
    }
}