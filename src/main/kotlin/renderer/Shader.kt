package me.gavin.breakout.renderer

import org.joml.Matrix2f
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.nio.FloatBuffer
import java.nio.IntBuffer

// default constructor is to get the shader as a resource
class Shader(val shaderName: String, vararg uniforms: String) {

    private val program: Int
    private val uniformCache = hashMapOf<String, Int>()

    init {
        val vertPath = "/shaders/$shaderName.vert"
        val fragPath = "/shaders/$shaderName.frag"

        var stream = Shader::class.java.getResource(vertPath)
            ?: throw RuntimeException("Unable to find vertex shader resource: $vertPath")

        val vSrc = stream.readText()
        val vs = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        GL20.glShaderSource(vs, vSrc)
        GL20.glCompileShader(vs)
        verifyCompilation(shaderName, vs)

        stream = Shader::class.java.getResource(fragPath)
            ?: throw RuntimeException("Unable to find frag shader resource: $fragPath")

        val fSrc = stream.readText()
        val fs = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        GL20.glShaderSource(fs, fSrc)
        GL20.glCompileShader(fs)
        verifyCompilation(shaderName, fs)


        program = GL20.glCreateProgram()
        GL20.glAttachShader(program, vs)
        GL20.glAttachShader(program, fs)
        GL20.glLinkProgram(program)
        Shader.verifyLinking(shaderName, program)

        GL20.glDetachShader(program, vs)
        GL20.glDetachShader(program, fs)
        GL20.glDeleteShader(vs)
        GL20.glDeleteShader(fs)

        println("Compiled shader: $shaderName")

        uniforms.forEach { u -> setupUniform(u) }
    }

    fun bind() {
        GL20.glUseProgram(program)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    private fun setupUniform(uniform: String) {
        val uid = GL20.glGetUniformLocation(program, uniform)
        if (uid != -1) {
            println("Setup uniform $shaderName:$uniform")
            uniformCache[uniform] = uid
        }
    }

    fun getUniformLocation(name: String): Int {
        // safeguard in case uniform we don't have cached is put into the program
        return uniformCache[name] ?: throw IllegalStateException("Attempted to get unknown uniform: $shaderName:$name")
    }

    // Float uniforms
    fun setUniform1f(name: String, value: Float) {
        val location = getUniformLocation(name)
        GL20.glUniform1f(location, value)
    }

    fun setUniform2f(name: String, x: Float, y: Float) {
        val location = getUniformLocation(name)
        GL20.glUniform2f(location, x, y)
    }

    fun setUniform3f(name: String, x: Float, y: Float, z: Float) {
        val location = getUniformLocation(name)
        GL20.glUniform3f(location, x, y, z)
    }

    fun setUniform4f(name: String, x: Float, y: Float, z: Float, w: Float) {
        val location = getUniformLocation(name)
        GL20.glUniform4f(location, x, y, z, w)
    }

    // Vector overloads
    fun setUniform2f(name: String, vector: org.joml.Vector2f) {
        val location = getUniformLocation(name)
        GL20.glUniform2f(location, vector.x, vector.y)
    }

    fun setUniform3f(name: String, vector: org.joml.Vector3f) {
        val location = getUniformLocation(name)
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    fun setUniform4f(name: String, vector: org.joml.Vector4f) {
        val location = getUniformLocation(name)
        GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w)
    }

    // Int uniforms
    fun setUniform1i(name: String, value: Int) {
        val location = getUniformLocation(name)
        GL20.glUniform1i(location, value)
    }

    fun setUniform2i(name: String, x: Int, y: Int) {
        val location = getUniformLocation(name)
        GL20.glUniform2i(location, x, y)
    }

    fun setUniform3i(name: String, x: Int, y: Int, z: Int) {
        val location = getUniformLocation(name)
        GL20.glUniform3i(location, x, y, z)
    }

    fun setUniform4i(name: String, x: Int, y: Int, z: Int, w: Int) {
        val location = getUniformLocation(name)
        GL20.glUniform4i(location, x, y, z, w)
    }

    // Unsigned int uniforms (only in OpenGL 3.0+)
    fun setUniform1ui(name: String, value: Int) {
        val location = getUniformLocation(name)
        GL30.glUniform1ui(location, value)
    }

    fun setUniform2ui(name: String, x: Int, y: Int) {
        val location = getUniformLocation(name)
        GL30.glUniform2ui(location, x, y)
    }

    fun setUniform3ui(name: String, x: Int, y: Int, z: Int) {
        val location = getUniformLocation(name)
        GL30.glUniform3ui(location, x, y, z)
    }

    fun setUniform4ui(name: String, x: Int, y: Int, z: Int, w: Int) {
        val location = getUniformLocation(name)
        GL30.glUniform4ui(location, x, y, z, w)
    }

    // Matrix uniforms
    fun setUniformMatrix2f(name: String, transpose: Boolean, matrix: Matrix2f) {
        val location = getUniformLocation(name)
        val buffer = BufferUtils.createFloatBuffer(4)
        matrix.get(buffer)
        GL20.glUniformMatrix2fv(location, transpose, buffer)
    }

    fun setUniformMatrix3f(name: String, transpose: Boolean, matrix: Matrix3f) {
        val location = getUniformLocation(name)
        val buffer = BufferUtils.createFloatBuffer(9)
        matrix.get(buffer)
        GL20.glUniformMatrix3fv(location, transpose, buffer)
    }

    fun setUniformMatrix4f(name: String, transpose: Boolean, matrixBuffer: FloatBuffer) {
        val location = getUniformLocation(name)
        GL20.glUniformMatrix4fv(location, transpose, matrixBuffer)
    }

    fun setUniformMatrix4f(name: String, transpose: Boolean, matrix: Matrix4f) {
        val location = getUniformLocation(name)
        val buffer = BufferUtils.createFloatBuffer(16)
        matrix.get(buffer)
        GL20.glUniformMatrix4fv(location, transpose, buffer)
    }

    // Using MemoryStack for better performance (alternative matrix methods)
    fun setUniformMatrix4fWithStack(name: String, transpose: Boolean, matrix: Matrix4f) {
        val location = getUniformLocation(name)
        org.lwjgl.system.MemoryStack.stackPush().use { stack ->
            val buffer = stack.mallocFloat(16)
            matrix.get(buffer)
            GL20.glUniformMatrix4fv(location, transpose, buffer)
        }
    }

    // Array uniform setters
    fun setUniform1fv(name: String, values: FloatArray) {
        val location = getUniformLocation(name)
        GL20.glUniform1fv(location, values)
    }

    fun setUniform2fv(name: String, values: FloatArray) {
        val location = getUniformLocation(name)
        GL20.glUniform2fv(location, values)
    }

    fun setUniform3fv(name: String, values: FloatArray) {
        val location = getUniformLocation(name)
        GL20.glUniform3fv(location, values)
    }

    fun setUniform4fv(name: String, values: FloatArray) {
        val location = getUniformLocation(name)
        GL20.glUniform4fv(location, values)
    }

    fun setUniform1iv(name: String, values: IntArray) {
        val location = getUniformLocation(name)
        GL20.glUniform1iv(location, values)
    }

    fun setUniform2iv(name: String, values: IntArray) {
        val location = getUniformLocation(name)
        GL20.glUniform2iv(location, values)
    }

    fun setUniform3iv(name: String, values: IntArray) {
        val location = getUniformLocation(name)
        GL20.glUniform3iv(location, values)
    }

    fun setUniform4iv(name: String, values: IntArray) {
        val location = getUniformLocation(name)
        GL20.glUniform4iv(location, values)
    }

    companion object {

        fun verifyCompilation(name: String, shader: Int) {
            val compStat = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS)
            if (compStat == 0) {
                val log = GL20.glGetShaderInfoLog(shader)
                GL20.glDeleteShader(shader)
                throw RuntimeException("Shader compilation failed for $name: \n$log")
            }
        }

        fun verifyLinking(name: String, program: Int) {
            val linkStat = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS)
            if (linkStat == 0) {
                val log = GL20.glGetProgramInfoLog(program)
                // thanks claude

                // Get number of attached shaders
                val numShaders = GL20.glGetProgrami(program, GL20.GL_ATTACHED_SHADERS)

                // Create a buffer to hold the shader IDs
                val shaderIds = IntBuffer.allocate(numShaders)
                GL20.glGetAttachedShaders(program, null, shaderIds)

                // Delete each attached shader
                for (i in 0 until numShaders) {
                    GL20.glDeleteShader(shaderIds.get(i))
                }

                GL20.glDeleteProgram(program)

                throw RuntimeException("Program linking failed for $name: \n$log")
            }
        }
    }
}