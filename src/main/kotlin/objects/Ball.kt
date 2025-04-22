package me.gavin.breakout.objects

import me.gavin.breakout.Breakout
import kotlin.random.Random

class Ball(val ballSize: Float, val speed: Float): GameObject() {

    var outOfBounds = false

    // 1 or -1, motion will be multiplied by this
    private var dirX = if (Math.random() < 0.5) -1 else 1
    private var dirY = -1

    override fun init() {
        size.x = ballSize
        size.y = ballSize

        pos.x = (Breakout.WIDTH / 2f) + (Math.random() * 200f * dirX).toFloat()
        pos.y = Breakout.HEIGHT / 2f - 35f
    }

    override fun update() {
        // bounds checking
        if (pos.x <= 0f) {
            pos.x = 0f
            dirX *= -1
        } else if (pos.x >= Breakout.WIDTH.toFloat() - size.x) {
            pos.x = Breakout.WIDTH.toFloat() - size.x
            dirX *= -1
        }

        if (pos.y < 0f) {
            outOfBounds = true
        } else if (pos.y >= Breakout.HEIGHT - size.y) {
            dirY *= -1
        }

        // check collisions
        val colV = Breakout.paddle.isCollidedVertically(this)
        val colH = Breakout.paddle.isCollidedHorizontal(this)

//        if (c)

        if (colV) {
            dirX *= -1
        }

        if (colH) {
            dirY *= -1
        }

//        if (){
//        }

        // check EVERY brick lol
        for (brick in Breakout.bricks) {
            if (!brick.visible) continue

            var hasHit = false

            if (brick.isCollidedVertically(this)) {
                brick.visible = false
                dirY *= -1
                hasHit = true
            }

            if (!hasHit && brick.isCollidedHorizontal(this)) {
                brick.visible = false
                dirX *= -1
                hasHit = true
            }

        }

        motion.x = speed * dirX
        motion.y = speed * dirY

        pos.x += motion.x
        pos.y += motion.y
    }

    override fun render() {
        if (!visible) return

        Breakout.quadRenderer.drawQuad(pos.x, pos.y, size.x, size.y)
    }
}