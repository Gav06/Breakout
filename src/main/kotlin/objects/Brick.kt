package me.gavin.breakout.objects

import me.gavin.breakout.Breakout
import org.joml.Vector3f
import kotlin.math.floor

class Brick(x: Float, y: Float, w: Float, h: Float): GameObject() {

    init {
        pos.x = x
        pos.y = y
        size.x = w
        size.y = h
    }

    override fun init() { }

    override fun update() {
        // eventually will be used to check
    }

    override fun render() {
        if (!visible) return

        val hue = pos.y / (Breakout.HEIGHT / 2f)
        val col = hsbToRgb(hue, 1.0f, 1.0f)

        Breakout.quadRenderer.drawQuad(pos.x, pos.y, size.x, size.y, col.x, col.y, col.z)
    }

    private fun hsbToRgb(hue: Float, saturation: Float, brightness: Float): Vector3f {
        val h = ((hue % 1.0f + 1.0f) % 1.0f) * 6.0f
        val i = floor(h).toInt()
        val f = h - i
        val p = brightness * (1.0f - saturation)
        val q = brightness * (1.0f - f * saturation)
        val t = brightness * (1.0f - (1.0f - f) * saturation)

        val (r, g, b) = when (i) {
            0 -> Triple(brightness, t, p)
            1 -> Triple(q, brightness, p)
            2 -> Triple(p, brightness, t)
            3 -> Triple(p, q, brightness)
            4 -> Triple(t, p, brightness)
            5 -> Triple(brightness, p, q)
            else -> Triple(0f, 0f, 0f) // fallback
        }

        return Vector3f(r, g, b)
    }
}