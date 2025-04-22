package me.gavin.breakout

import org.lwjgl.glfw.GLFW

class Stopwatch {

    private var startTime = 0.0
    private var running = false

    // can be used to start or reset timer
    fun start() {
        running = true
        startTime = GLFW.glfwGetTime()
    }

    fun hasElapsed(timeSeconds: Double): Boolean {
        return running && GLFW.glfwGetTime() - startTime >= timeSeconds
    }

    fun stop() {
        running = false
    }

    fun isRunning(): Boolean {
        return running
    }
}