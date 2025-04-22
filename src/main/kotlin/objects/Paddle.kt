package me.gavin.breakout.objects

import me.gavin.breakout.Breakout
import org.lwjgl.glfw.GLFW

class Paddle: GameObject() {

    override fun init() {
        size.x = 100f
        size.y = 20f

        pos.x = (Breakout.WIDTH / 2f) - (size.x / 2f)
        pos.y = 10f
    }

    override fun update() {

        var motionX = 0f
        if (GLFW.glfwGetKey(Breakout.window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS)
            motionX -= 25f
        if (GLFW.glfwGetKey(Breakout.window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS)
            motionX += 25f
        motion.x = motionX



        pos.x += motion.x
        pos.y += motion.y

        motion.x = 0f
        motion.y = 0f

        if (pos.x <= 0f) pos.x = 0f
        if (pos.x >= Breakout.WIDTH.toFloat() - size.x) pos.x = Breakout.WIDTH.toFloat() - size.x
    }

    override fun render() {
        if (!visible) return

        Breakout.quadRenderer.drawQuad(pos.x, pos.y, size.x, size.y)
    }

}