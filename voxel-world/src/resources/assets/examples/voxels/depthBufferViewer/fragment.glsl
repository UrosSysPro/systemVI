#version 330

out vec4 FragColor;

in vec2 uv;

uniform sampler2D depthBuffer;
uniform float near;
uniform float far;

void main() {
    vec2 samplepoint = vec2(uv.x, 1.0 - uv.y);
    float depth = texture(depthBuffer, samplepoint).r;
    depth = (2.0 * near) / (far + near - depth * (far - near));
    FragColor = vec4(depth);
}