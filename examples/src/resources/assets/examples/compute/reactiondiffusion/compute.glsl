#version 430 core

//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_WorkGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
uniform layout(binding=0,rg16f) writeonly image2D next;
uniform layout(binding=1,rg16f) readonly image2D current;

void main() {
    ivec2 texelCoord=ivec2(gl_GlobalInvocationID.xy);
    imageStore(next,texelCoord,vec4(0.6,0.7,1.0,1.0));
}