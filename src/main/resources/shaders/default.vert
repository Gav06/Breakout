#version 460

layout (location = 0) in vec2 aPos;

uniform mat4 projection;
uniform mat4 transform;

out vec3 fragCoord;

void main() {
    gl_Position = projection * transform * vec4(aPos, 0.0, 1.0);
    fragCoord = vec3(aPos, 0.0);
}