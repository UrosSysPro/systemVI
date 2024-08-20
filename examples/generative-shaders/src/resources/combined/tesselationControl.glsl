#version 410 core
layout (vertices=4) out;

uniform vec3 cameraPosition;
uniform mat4 model;

void main()
{
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;

//    float minDistance=10000.0;
//    for(int i=0;i<4;i++){
//        vec4 position=gl_in[i].gl_Position;
//        float distance=distance(cameraPosition,vec3(model*position));
//        if(distance<minDistance)minDistance=distance;
//    }

    int subdivision=10;
//    if(minDistance>1)subdivision=9;
//    if(minDistance>2)subdivision=8;
//    if(minDistance>3)subdivision=7;
//    if(minDistance>4)subdivision=6;
//    if(minDistance>5)subdivision=5;

    if (gl_InvocationID == 0)
    {
        gl_TessLevelOuter[0] = subdivision;
        gl_TessLevelOuter[1] = subdivision;
        gl_TessLevelOuter[2] = subdivision;
        gl_TessLevelOuter[3] = subdivision;

        gl_TessLevelInner[0] = subdivision;
        gl_TessLevelInner[1] = subdivision;
    }
}