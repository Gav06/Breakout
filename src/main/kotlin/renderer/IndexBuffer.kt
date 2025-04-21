package me.gavin.breakout.renderer

import org.lwjgl.opengl.GL30

class IndexBuffer(vararg indices: Int): Buffer {

    val size = indices.size
    private val buffer = GL30.glGenBuffers()

    init {
        bind()
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW)
    }

    override fun bind() {
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer)
    }

    override fun unbind() {
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override fun free() {
        GL30.glDeleteBuffers(buffer)
    }

}