package me.gavin.breakout.objects

import org.joml.Vector2f

abstract class GameObject {

    val size = Vector2f()
    val pos = Vector2f()
    val motion = Vector2f()
    var visible = true

    abstract fun init()

    abstract fun update()

    abstract fun render()

    // check if colliding with another game object
    fun isColliding(obj: GameObject): Boolean {
        return (pos.x < obj.pos.x + obj.size.x)
                && (pos.x + size.x > obj.pos.x)
                && (pos.y + size.y > obj.pos.y)
                && (pos.y < obj.pos.y + obj.size.y)
    }
}