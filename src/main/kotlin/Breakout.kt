package me.gavin.breakout

import me.gavin.breakout.renderer.IndexBuffer
import me.gavin.breakout.renderer.QuadRenderer
import me.gavin.breakout.renderer.Shader
import me.gavin.breakout.renderer.VertexBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack
import kotlin.math.sqrt

object Breakout {

    const val WIDTH = 800
    const val HEIGHT = 800

    private var window: Long = 0L

    val quadRenderer: QuadRenderer

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

        // set identity stack for transformation


        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(window)
        GL.createCapabilities()

        val vb = VertexBuffer(GL30.GL_STATIC_DRAW, Float.SIZE_BYTES * 5,
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        )
        val ib = IndexBuffer(0, 2, 3, 0, 1, 2)
        quadRenderer = QuadRenderer(vb, ib)

        loop()
    }

    private fun loop() {
        println(GL11.glGetString(GL11.GL_VERSION))
        println(GL11.glGetString(GL11.GL_RENDERER))

        val cx = WIDTH / 2f
        val cy = HEIGHT / 2f

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        quadRenderer.bind()
        while (!GLFW.glfwWindowShouldClose(window)) {
            quadRenderer.clear()

            quadRenderer.drawQuad(cx, cy, 100f, 100f)

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }

        quadRenderer.free()
    }
}