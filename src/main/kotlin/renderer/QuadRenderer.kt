package me.gavin.breakout.renderer

import me.gavin.breakout.Breakout.HEIGHT
import me.gavin.breakout.Breakout.WIDTH
import org.joml.Matrix4f
import org.joml.Matrix4fStack
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil

class QuadRenderer(val vb: VertexBuffer, val ib: IndexBuffer): Buffer {

    val vao: Int
    val projectionMatrix = Matrix4f()
    val transformStack = Matrix4fStack(128)
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    private val shader = Shader("default", "projection", "transform", "color")

    init {
        vao = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vao)

        GL30.glEnableVertexAttribArray(0)
        GL30.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)

        transformStack.set(Matrix4f())
        projectionMatrix.setOrtho2D(0f, WIDTH.toFloat(), 0f, HEIGHT.toFloat())
    }

    fun drawQuad(x: Float, y: Float, w: Float, h: Float, r: Float = 1f, g: Float = 1f, b: Float = 1f) {
        transformStack.identity()
            .translate(x, y, 0f)
            .scale(w, h, 1f)

        transformStack.get(matrixBuffer)
        shader.setUniformMatrix4f("projection", false, projectionMatrix)
        shader.setUniformMatrix4f("transform", false, matrixBuffer)
        shader.setUniform3f("color", r, g, b)

        GL30.glDrawElements(GL11.GL_TRIANGLES, ib.size, GL11.GL_UNSIGNED_INT, 0)
    }

    override fun bind() {
        vb.bind()
        ib.bind()
        shader.bind()
    }

    override fun unbind() {
        ib.unbind()
        vb.unbind()
        shader.unbind()
    }

    override fun free() {
        vb.free()
        ib.free()
        MemoryUtil.memFree(matrixBuffer)
    }

    fun clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }
}