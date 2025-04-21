package me.gavin.breakout.renderer

interface Buffer {

    fun bind()

    fun unbind()

    fun free()
}