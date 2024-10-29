#version 330

uniform sampler2D positionBuffer;
uniform vec4 rect;
uniform mat4 view;
uniform mat4 projection;

out vec2 uv;

void main(){
    vec2[4] positions;
    positions[0]=vec2(0,0);
    positions[1]=vec2(1,0);
    positions[2]=vec2(0,1);
    positions[3]=vec2(1,1);
    vec2 position=positions[gl_VertexID]*rect.zw+rect.xy;

    uv=positions[gl_VertexID];

    gl_Position=projection*view*vec4(position,0,1);
}