package me.gavin.breakout.objects

import me.gavin.breakout.Breakout
import kotlin.random.Random

class Ball(val ballSize: Float, val speed: Float): GameObject() {

    var outOfBounds = false

    // 1 or -1, motion will be multiplied by this
    private var dirX = if (Math.random() < 0.5) -1 else 1
    private var dirY = -1

    var prevX = 0f
    var prevY = 0f

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

        // check collisions with paddle
        if (Breakout.paddle.isColliding(this)) {
            // Determine which side of the paddle we hit
            if (prevY + size.y <= Breakout.paddle.pos.y) {
                // Hit from top
                pos.y = Breakout.paddle.pos.y - size.y
                dirY *= -1

                // Optional: Add angle variation based on where the ball hits the paddle
                // Calculate hit position relative to paddle center (0.0 to 1.0)
                val hitPos = (pos.x + size.x/2 - Breakout.paddle.pos.x) / Breakout.paddle.size.x

                // Map hitPos (0 to 1) to dirX values that make sense for your game
                // Center hits (hitPos = 0.5) → dirX = current dirX (maintain direction)
                // Left edge (hitPos = 0) → dirX = -1 (left)
                // Right edge (hitPos = 1) → dirX = 1 (right)
                if (hitPos < 0.33f) {
                    dirX = -1  // Hit left side, go left
                } else if (hitPos > 0.67f) {
                    dirX = 1   // Hit right side, go right
                }
                // Else keep current horizontal direction
            } else if (prevY >= Breakout.paddle.pos.y + Breakout.paddle.size.y) {
                // Hit from bottom (unlikely in Breakout, but for completeness)
                pos.y = Breakout.paddle.pos.y + Breakout.paddle.size.y
                dirY *= -1
            } else if (prevX + size.x <= Breakout.paddle.pos.x) {
                // Hit from left
                pos.x = Breakout.paddle.pos.x - size.x
                dirX *= -1
            } else if (prevX >= Breakout.paddle.pos.x + Breakout.paddle.size.x) {
                // Hit from right
                pos.x = Breakout.paddle.pos.x + Breakout.paddle.size.x
                dirX *= -1
            }
        }

        // check EVERY brick
        for (brick in Breakout.bricks) {
            if (!brick.visible) continue

            if (brick.isColliding(this)) {
                brick.visible = false

                // Determine which side of the brick we hit
                if (prevY + size.y <= brick.pos.y) {
                    // Hit from top
                    pos.y = brick.pos.y - size.y
                    dirY *= -1
                } else if (prevY >= brick.pos.y + brick.size.y) {
                    // Hit from bottom
                    pos.y = brick.pos.y + brick.size.y
                    dirY *= -1
                } else if (prevX + size.x <= brick.pos.x) {
                    // Hit from left
                    pos.x = brick.pos.x - size.x
                    dirX *= -1
                } else if (prevX >= brick.pos.x + brick.size.x) {
                    // Hit from right
                    pos.x = brick.pos.x + brick.size.x
                    dirX *= -1
                }

                break
            }
        }

        motion.x = speed * dirX
        motion.y = speed * dirY

        prevX = pos.x
        prevY = pos.y
        pos.x += motion.x
        pos.y += motion.y
    }

    override fun render() {
        if (!visible) return

        Breakout.quadRenderer.drawQuad(pos.x, pos.y, size.x, size.y)
    }
}