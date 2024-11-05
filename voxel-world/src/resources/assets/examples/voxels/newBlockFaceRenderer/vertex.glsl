#version 330
#define PI 3.14159265359


layout (location = 0) in vec3 worldPosition;
layout (location = 1) in vec2 position;
layout (location = 2) in vec2 uv;
layout (location = 3) in float occlusion;
layout (location = 4) in float sideIndex;

uniform float time;
uniform mat4 view;
uniform mat4 projection;

out struct VERTEX_OUT{
    vec2 uv;
    vec3 tangent;
    vec3 bitangent;
    vec3 normal;
    vec3 worldPosition;
    float occlusion;
}vertexOut;

//enum BlockSide(val index:Int):
//  case Left extends BlockSide(0)
//  case Right extends BlockSide(1)

//  case Top extends BlockSide(2)
//  case Bottom extends BlockSide(3)

//  case Front extends BlockSide(4)
//  case Back extends BlockSide(5)

mat4 rotateX(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(1.0, 0.0, 0.0, 0.0),
    vec4(0.0, c, -s, 0.0),
    vec4(0.0, s, c, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}
mat4 rotateY(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(  c, 0.0,  -s, 0.0),
    vec4(0.0, 1.0, 0.0, 0.0),
    vec4(  s, 0.0,   c, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}
mat4 rotateZ(float angle) {
    float s = sin(angle);
    float c = cos(angle);
    return mat4(
    vec4(c, -s, 0.0, 0.0),
    vec4(s, c, 0.0, 0.0),
    vec4(0.0, 0.0, 1.0, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}
mat4 translate(vec3 position) {
    return mat4(
    vec4(1.0, 0.0, 0.0, 0.0),
    vec4(0.0, 1.0, 0.0, 0.0),
    vec4(0.0, 0.0, 1.0, 0.0),
    vec4(position, 1.0)
    );
}
mat4 identity() {
    return mat4(
    vec4(1.0, 0.0, 0.0, 0.0),
    vec4(0.0, 1.0, 0.0, 0.0),
    vec4(0.0, 0.0, 1.0, 0.0),
    vec4(0.0, 0.0, 0.0, 1.0)
    );
}

void main() {
    int index = int(sideIndex);

    mat4 worldTransform = translate(worldPosition);
    mat4 rotation,translation;

    //left right
    if (index == 0){
        translation=translate(vec3(-0.5, 0.0, 0.0));
        rotation=rotateY(-PI/2);
    }
    if (index == 1){
        translation= translate(vec3(0.5, 0.0, 0.0));
        rotation=rotateY(PI/2);
    }
    //top bottom
    if (index == 2){
        translation=translate(vec3(0.0, 0.5, 0.0));
        rotation=rotateX(PI/2);
    }
    if (index == 3){
        translation=translate(vec3(0.0, -0.5, 0.0));
        rotation=rotateX(-PI/2);
    }
    //fort back
    if (index == 4){
        translation=translate(vec3(0.0, 0.0, 0.5));
        rotation=rotateY(0);
    }
    if (index == 5){
        translation=translate(vec3(0.0, 0.0, -0.5));
        rotation=rotateY(PI);
    }

    vec4 tangent=rotation*vec4(1.0,0.0,0.0,1.0);
    vec4 bitangent=rotation*vec4(0.0,1.0,0.0,1.0);
    vec4 normal=rotation*vec4(0.0,0.0,1.0,1.0);

    mat4 model=worldTransform*translation*rotation;

    vec4 worldPosition=model * vec4(position-0.5 , 0.0, 1.0);
    vertexOut.uv=uv;
    vertexOut.worldPosition=worldPosition.xyz;
    vertexOut.tangent=tangent.xyz;
    vertexOut.bitangent=bitangent.xyz;
    vertexOut.normal=normal.xyz;
    vertexOut.occlusion=occlusion;

    gl_Position = projection * view * worldPosition;
}