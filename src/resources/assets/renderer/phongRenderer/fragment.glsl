#version 330 core

out vec4 FragColor;

in VERTEX_OUT{
    vec3 tangent,bitangent,normal,modelPosition,worldPosition;
    vec2 texCoords;
    vec4 color;
}vertexIn;

void main(){
    //    FragColor=vec4(0.9764,0.4509,0.0862,1);
    //    FragColor=vec4(normalize(vertexIn.normal),1);
//    FragColor=vertexIn.color;
    FragColor=vec4(vertexIn.texCoords,0.5,1.0);
}