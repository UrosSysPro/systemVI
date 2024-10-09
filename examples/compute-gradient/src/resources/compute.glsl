
#version 430 core

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout(rgba32f, binding = 0) uniform image2D outputTexture;

//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_WorkGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

void main(){
    ivec2 position=ivec2(gl_WorkGroupID.xy);
    ivec2 size=ivec2(gl_NumWorkGroups.xy);
    vec4 color=vec4(vec2(position)/vec2(size),0.9,1.0);
    imageStore(outputTexture,position,color);
}