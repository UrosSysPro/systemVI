#version 430 core

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (r16f, binding = 0) uniform image2D density;
layout (r16f, binding = 1) uniform image2D u_texture;
layout (r16f, binding = 2) uniform image2D v_texture;

uniform ivec2 size;
uniform float deltaTime;
uniform ivec2 offset;
uniform vec2 velocity;

void main() {
    ivec2 texelCoords = ivec2(gl_GlobalInvocationID.xy) + offset;

    imageStore(density, texelCoords, vec4(1.0));
//    imageStore(u_texture, texelCoords, vec4(velocity.x));
    imageStore(u_texture, texelCoords, vec4(1.0));
//    imageStore(v_texture, texelCoords, vec4(velocity.y));
    imageStore(v_texture, texelCoords, vec4(0.0));
}
