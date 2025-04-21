#version 460

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec3 aColor;

uniform mat4 projection;
uniform mat4 transform;

out vec3 fragCoord;
out vec4 vertexColor;

void main() {
    gl_Position = projection * transform * vec4(aPos, 0.0, 1.0);
    vertexColor = vec4(aColor, 1.0);
    fragCoord = vec3(aPos, 0.0);
}