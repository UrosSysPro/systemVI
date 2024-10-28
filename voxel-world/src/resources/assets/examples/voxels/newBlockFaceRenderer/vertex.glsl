#version 330
#define PI 3.14159265359


layout (location = 0) in vec3 worldPosition;
layout (location = 1) in vec2 position;
layout (location = 2) in float sideIndex;

uniform float time;
uniform mat4 view;
uniform mat4 projection;

out vec4 color;

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

//    color = vec4(1.0);
//    if (index == 0)color = vec4(0.3, 0.5, 0.7, 1.0);

    color=vec4(position,0.0,1.0);

    mat4 model = translate(worldPosition);
    //left right
    if (index == 0)model = translate(worldPosition) * translate(vec3(-0.5, 0.0, 0.0)) * rotateY(-PI/2);
    if (index == 1)model = translate(worldPosition) * translate(vec3(0.5, 0.0, 0.0)) * rotateY(PI/2);
    //top bottom
    if (index == 2)model = translate(worldPosition) * translate(vec3(0.0, 0.5, 0.0)) * rotateX(PI/2);
    if (index == 3)model = translate(worldPosition) * translate(vec3(0.0, -0.5, 0.0)) * rotateX(-PI/2);
    //fort back
    if (index == 4)model = translate(worldPosition) * translate(vec3(0.0, 0.0, 0.5)) * rotateY(0);
    if (index == 5)model = translate(worldPosition) * translate(vec3(0.0, 0.0, -0.5)) * rotateY(PI);

    gl_Position = projection * view * model * vec4(position-0.5 , 0.0, 1.0);
}