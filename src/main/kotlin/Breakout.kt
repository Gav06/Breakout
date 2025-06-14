package me.gavin.breakout

import me.gavin.breakout.objects.Ball
import me.gavin.breakout.objects.Brick
import me.gavin.breakout.objects.Paddle
import me.gavin.breakout.renderer.IndexBuffer
import me.gavin.breakout.renderer.QuadRenderer
import me.gavin.breakout.renderer.VertexBuffer
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack

object Breakout {

    const val WIDTH = 800
    const val HEIGHT = 800

    var window: Long = 0L

    private var running = true

    val quadRenderer: QuadRenderer
    val bricks = ArrayList<Brick>()
    val paddle = Paddle()

    private const val BALL_SIZE = 10f
    private const val BALL_SPEED = 7.5f;

    private var ball: Ball? = null
    private val ballSpawnTimer = Stopwatch()

    private val keyCallback = GLFWKeyCallbackI { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
        if (action != GLFW.GLFW_PRESS)
            return@GLFWKeyCallbackI

        when (key) {
            GLFW.GLFW_KEY_ESCAPE -> running = false
            GLFW.GLFW_KEY_R -> { initGameObjects(); ballSpawnTimer.start() }
        }
    }

    private var partialTicks: Float = 0.0f

    init {
        // Init window and GLFW
        GLFWErrorCallback.createPrint(System.err).set()

        if (!GLFW.glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE)
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Breakout", 0L, 0L)

        if (window == 0L)
            throw RuntimeException("Unable to create GLFW window")

        val stack = MemoryStack.stackPush()
        stack.use {
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)

            GLFW.glfwGetWindowSize(window, pWidth, pHeight)
            val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
                ?: throw RuntimeException("Unable to get primary monitor GLFW video mode")

            GLFW.glfwSetWindowPos(
                window,
                (vidMode.width() - pWidth[0]) / 2,
                (vidMode.height() - pHeight[0]) / 2
            )
        }

        GLFW.glfwSetKeyCallback(window, keyCallback)
        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(1)
        GL.createCapabilities()

        val vb = VertexBuffer(GL30.GL_STATIC_DRAW, Float.SIZE_BYTES * 5,
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f
        )
        val ib = IndexBuffer(0, 2, 3, 0, 1, 2)
        quadRenderer = QuadRenderer(vb, ib)

        initGameObjects()
        loop()
    }

    private fun initGameObjects() {
        val brickWidth = WIDTH / 10f
        val brickHeight = HEIGHT / 25f

        val horizAmt = brickWidth.toInt()
        val vertAmt = (HEIGHT / 2) / brickHeight.toInt()

        paddle.init()
        ball = spawnBall()

        if (bricks.isNotEmpty())
            bricks.clear()

        for (x in (0..horizAmt)) {
            for (y in (0..vertAmt)) {
                val brick = Brick(x * brickWidth, HEIGHT.toFloat() - (y * brickHeight) - brickHeight, brickWidth, brickHeight)
                brick.init()
                bricks.add(brick)
            }
        }
    }

    private fun loop() {
        println(GL11.glGetString(GL11.GL_VERSION))
        println(GL11.glGetString(GL11.GL_RENDERER))

        val cx = WIDTH / 2f
        val cy = HEIGHT / 2f

        val tps = 60
        val tickInterval = 1.0 / tps
        var lastTime = GLFW.glfwGetTime()
        var accumulator = 0.0

        GLFW.glfwShowWindow(window)
        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)

        ballSpawnTimer.start()

        quadRenderer.bind()
        while (running) {
            if (GLFW.glfwWindowShouldClose(window)) {
                running = false
                break
            }

            val currTime = GLFW.glfwGetTime()
            val deltaTime = currTime - lastTime
            lastTime = currTime
            accumulator += deltaTime

            // tick pass when needed
            while (accumulator >= tickInterval) {
                update()
                accumulator -= tickInterval
            }

            partialTicks = (accumulator / tickInterval).toFloat()

            render()

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }

        quadRenderer.free()
    }

    private fun update() {
        paddle.update()

        if (ball == null) {
            if (ballSpawnTimer.isRunning()) {
                if (ballSpawnTimer.hasElapsed(1.5)) {
                    ballSpawnTimer.stop()
                    ball = spawnBall()
                }
            } else {
                ballSpawnTimer.start()
            }
        } else {
            if (ball!!.outOfBounds) ball = null else ball!!.update()
        }

        bricks.forEach { it.update() }
    }

    private fun render() {
        quadRenderer.clear()

        // render pass
        paddle.render()
        ball?.render()
        bricks.forEach { it.render() }
    }

    private fun spawnBall(): Ball {
        val b = Ball(BALL_SIZE, BALL_SPEED)
        b.init()
        return b
    }
}