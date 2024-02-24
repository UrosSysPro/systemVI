#version 430 core

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (binding=0,r16f) uniform image2D density;
layout (binding=1,r16f) uniform image2D u_texture;
layout (binding=2,r16f) uniform image2D v_texture;

uniform ivec2 size;
uniform float deltaTime;
uniform ivec2 offset;
uniform vec2 velocity;

void main() {
    ivec2 texelCoords = ivec2(gl_GlobalInvocationID.xy) + offset;

    imageStore(density, texelCoords, vec4(1.0));
    imageStore(u_texture, texelCoords, vec4(velocity.x));
    imageStore(v_texture, texelCoords, vec4(velocity.y));
}
