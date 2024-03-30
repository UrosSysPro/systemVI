#version 430 core

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (binding=0, r16f) uniform image2D density;
layout (binding=1, r16f) uniform image2D u_texture;
layout (binding=2, r16f) uniform image2D v_texture;

uniform float deltaTime;
uniform ivec2 offset;
uniform vec2 velocity;
uniform float fill;

void main() {
    ivec2 texelCoords = ivec2(gl_GlobalInvocationID.xy) + offset;

    float d=imageLoad(density, texelCoords).x;
    vec4 den = vec4(mix(d, 0.7, step(0.5, fill))) - vec4(0.2);
    imageStore(density, texelCoords, den);
    //    float u=imageLoad(u_texture,texelCoords).x;
    imageStore(u_texture, texelCoords, vec4(velocity.x*deltaTime));
    //    float v=imageLoad(v_texture,texelCoords).x;
    imageStore(v_texture, texelCoords, vec4(velocity.y*deltaTime));
}
