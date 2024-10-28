#version 330

out vec4 FragColor;

in struct VERTEX_OUT{
    vec2 uv;
    vec3 tangent;
    vec3 bitangent;
    vec3 normal;
    vec3 worldPosition;
}vertexOut;

void main(){
    FragColor=vec4(vertexOut.tangent,1.0);
}