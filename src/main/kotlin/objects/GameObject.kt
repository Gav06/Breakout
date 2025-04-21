package me.gavin.breakout.objects

import org.joml.Vector2f

abstract class GameObject {

    val pos = Vector2f()
    val motion = Vector2f()
    val visible = true

    abstract fun init()

    abstract fun update()

    abstract fun render()
}