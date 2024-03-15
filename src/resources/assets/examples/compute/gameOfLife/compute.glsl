#version 430 core

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout(rgba32f, binding = 0) uniform image2D current;
layout(rgba32f, binding = 1) uniform image2D next;

uniform ivec2 size;

void main() {
    ivec2 texelCoord = ivec2(gl_GlobalInvocationID.xy);
    vec4 value = imageLoad(current, texelCoord);

    //    value.x = float(texelCoord.x)/(gl_NumWorkGroups.x);
    //    value.y = float(texelCoord.y)/(gl_NumWorkGroups.y);

    bool isalive = value.r >= 0.5;
    int alive = 0;

    for (int i = -1; i <= 1; i++){
        for (int j = -1; j <= 1; j++){
            if (i == 0 && j == 0) continue;
            ivec2 pos = texelCoord + ivec2(i, j);
            if (pos.x < 0 || pos.x >= size.x || pos.y < 0 || pos.y >= size.y) continue;
            vec4 pixel = imageLoad(current, pos);
            alive += int(pixel.r >= 0.5);
        }
    }

    if(isalive&&(alive<2||alive>3))isalive=false; else
    if(isalive&&(alive==3||alive==2))isalive=true;else
    if(!isalive&&(alive==3))isalive=true;


    imageStore(next, texelCoord, vec4(float(isalive)));
}