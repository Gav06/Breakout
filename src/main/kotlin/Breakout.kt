package me.gavin.breakout

import me.gavin.breakout.renderer.Shader
import org.joml.Matrix4f
import org.joml.Matrix4fStack
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

    val shaders = hashMapOf<String, Shader>()
    val projectionMatrix = Matrix4f()
    val transformStack = Matrix4fStack(128)


    init {
        init()
        loop()
    }

    private fun init() {
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
        transformStack.set(Matrix4f())
        projectionMatrix.setOrtho2D(0f, WIDTH.toFloat(), 0f, HEIGHT.toFloat())

        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(window)
        GL.createCapabilities()

        shaders["default"] = Shader("default", "projection", "transform")
    }

    private fun loop() {
        println(GL11.glGetString(GL11.GL_VERSION))
        println(GL11.glGetString(GL11.GL_RENDERER))

        val vaoId = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoId)

        val vboId = GL30.glGenBuffers()
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId)

        val s = 200f
        val h = (sqrt(3f) / 2f) * s
        val halfH = h / 2f
        val halfS = s / 2f

        val cx = WIDTH / 2.0f
        val cy = HEIGHT / 2.0f
        val verticies = floatArrayOf(
            cx, cy + halfH, 1f, 0f, 0f,
            cx - halfS, cy - halfH, 0f, 1f, 0f,
            cx + halfS, cy - halfH, 0f, 0f, 1f
        )

        val vertexBuf = BufferUtils.createFloatBuffer(verticies.size)
        vertexBuf.put(verticies)
        vertexBuf.flip() // prepare buf for reading

        // upload to gpu
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuf, GL30.GL_STATIC_DRAW)

        // setup vertex attribs

        // vec2f pos
        GL30.glEnableVertexAttribArray(0)
        GL30.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 20, 0)
        // vec3f color
        GL30.glEnableVertexAttribArray(1)
        GL30.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, true, 20, 8)

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)


        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        val shader = shaders["default"]
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            transformStack.pushMatrix()
            transformStack.translate(cx, cy, 0f)
            transformStack.rotate(GLFW.glfwGetTime().toFloat(), 0f, 0f, 1f)
            transformStack.translate(-cx, -cy, 0f)

            shader?.bind()
            shader?.setUniformMatrix4f("projection", false, projectionMatrix)
            shader?.setUniformMatrix4f("transform", false, transformStack)

            GL30.glBindVertexArray(vaoId)
            GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, 3)

            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()

            transformStack.popMatrix()
        }

        GL30.glDeleteVertexArrays(vaoId)
        GL30.glDeleteBuffers(vboId)
    }
}