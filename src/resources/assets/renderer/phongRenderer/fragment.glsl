#version 330 core

out vec4 FragColor;

in VERTEX_OUT{
    vec3 tangent,bitangent,normal,modelPosition,worldPosition;
    vec2 texCoords;
    vec4 color;
}vertexIn;

uniform sampler2D diffuseTexture;

uniform vec4 materialDiffuse;

layout (std140) uniform Colors
{
    int number;
    float value;
    vec4 color1;
    vec4 color2;
};

void main(){
    //    FragColor=vec4(0.9764,0.4509,0.0862,1);
    //    FragColor=vec4(normalize(vertexIn.normal),1);
            FragColor=vec4(vertexIn.modelPosition,1.0);
    //        FragColor=vec4(vertexIn.texCoords,0.0,1.0);
//            FragColor=vertexIn.color;
//    FragColor=texture(diffuseTexture,vertexIn.texCoords);
    //        FragColor=texture(diffuseTexture,vertexIn.modelPosition.xz);
    //    FragColor=materialDiffuse;
}