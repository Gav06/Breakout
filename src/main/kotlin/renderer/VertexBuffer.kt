package me.gavin.breakout.renderer

import org.lwjgl.opengl.GL30

class VertexBuffer(val usage: Int, val vertexSize: Int, vararg verticies: Float): Buffer {

    private val buffer = GL30.glGenBuffers()

    init {
        bind()
        if (usage == GL30.GL_STATIC_DRAW)
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, verticies, usage)
    }

    fun passVertexData(vararg verticies: Float) {
        TODO("Implement support for non-static hints, like dynamic and stream draw")
    }

    override fun bind() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, buffer)
    }

    override fun unbind() {
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
    }

    override fun free() {
        GL30.glDeleteBuffers(buffer)
    }
}