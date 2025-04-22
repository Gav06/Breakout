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

    fun isCollidedHorizontal(obj: GameObject): Boolean {
        if ((pos.y + size.y > obj.pos.y) && (pos.y < obj.pos.y + obj.size.y)) {
            val leftCollision = pos.x < obj.pos.x + obj.size.x
            val rightCollision = pos.x + size.x > obj.pos.x

            return leftCollision && rightCollision
        }

        return false
    }

    fun isCollidedVertically(obj: GameObject): Boolean {
        if ((pos.x < obj.pos.x + obj.size.x) && (pos.x + size.x > obj.pos.x)) {
            val topCollision = pos.y < obj.pos.y + obj.size.y
            val bottomCollision = pos.y + size.y > obj.pos.y

            return topCollision && bottomCollision
        }

        return false
    }
}