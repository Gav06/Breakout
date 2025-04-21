package me.gavin.breakout.objects

import me.gavin.breakout.Breakout

class Brick: GameObject() {

    val brickWidth = (Breakout.WIDTH / 10f) - (2f * 10f)
    val brickHeight = 20f

    override fun init() {

    }

    override fun update() {
        // eventually will be used to check
    }

    override fun render() {
        if (!visible) return

        val hue = (pos.y / Breakout.HEIGHT) * 360f

        Breakout.quadRenderer.drawQuad(pos.x, pos.y, brickWidth, brickHeight)
    }
}