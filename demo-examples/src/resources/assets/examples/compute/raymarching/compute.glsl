#version 430 core
#define RAY_ITERATIONS 100
#define DELTA_EPSILON 0.01
#define MAX_DISTANCE 1000.0
//in uvec3 gl_NumWorkGroups;
//in uvec3 gl_WorkGroupID;
//in uvec3 gl_LocalInvocationID;
//in uvec3 gl_GlobalInvocationID;
//in uint  gl_LocalInvocationIndex;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
uniform layout(binding=0,rgba32f) writeonly image2D outputTexture;
uniform mat4 view;


float sphere( vec3 p, float s )
{
    return length(p)-s;
}
float box( vec3 p, vec3 b )
{
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}
float torus( vec3 p, vec2 t )
{
    vec2 q = vec2(length(p.xz)-t.x,p.y);
    return length(q)-t.y;
}
float plane( vec3 p, vec3 n, float h )
{
    // n must be normalized
    return dot(p,n) + h;
}
float unionSDF( float d1, float d2 )
{
    return min(d1,d2);
}
float subtraction( float d1, float d2 )
{
    return max(-d1,d2);
}
float intersection( float d1, float d2 )
{
    return max(d1,d2);
}
float xor(float d1, float d2 )
{
    return max(min(d1,d2),-max(d1,d2));
}
float smoothUnion( float d1, float d2, float k )
{
    float h = clamp( 0.5 + 0.5*(d2-d1)/k, 0.0, 1.0 );
    return mix( d2, d1, h ) - k*h*(1.0-h);
}

float smoothSubtraction( float d1, float d2, float k )
{
    float h = clamp( 0.5 - 0.5*(d2+d1)/k, 0.0, 1.0 );
    return mix( d2, -d1, h ) + k*h*(1.0-h);
}

float smoothIntersection( float d1, float d2, float k )
{
    float h = clamp( 0.5 - 0.5*(d2-d1)/k, 0.0, 1.0 );
    return mix( d2, d1, h ) + k*h*(1.0-h);
}
vec3 translate(vec3 p,vec3 t){
    return p-t;
}

float map(vec3 p){
    return unionSDF(
        box(
            translate(p,vec3(0.0,0.0,-10.0)),
            vec3(1)
        ),
        sphere(
            translate(p,vec3(2.0,0.0,-10.0)),
            1
        )
    );
}
vec4 color(vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1))<DELTA_EPSILON*2.0)return vec4(0.3,0.6,0.9,1.0);
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1))<DELTA_EPSILON*2.0)return vec4(0.3,0.6,0.9,1.0);
    return vec4(0.0);
}

void rayMarch(out float distance,out vec3 endPoint,out int numOfIterations,in vec3 rayOrigin,in vec3 rayDirection){
    distance=0;
    endPoint=vec3(rayOrigin);
    for(int i=0;i<RAY_ITERATIONS;i++){
        numOfIterations=i;
        vec3 p=rayOrigin+distance*rayDirection;
        float d=map(p);
        distance+=d;
        if(distance>MAX_DISTANCE||d<DELTA_EPSILON)break;
    }
    endPoint=rayOrigin+distance*rayDirection;
}

vec4 getColor(vec2 uv,vec2 size){
    //rayDir rayOrigin
    mat4 inverseView=inverse(view);
    vec4 helper;

    vec3 focusPoint=vec3(0.0,0.0,1.0);
    vec3 rayOrigin=vec3(uv,0.0);

    helper=vec4(focusPoint,1.0);
    helper=inverseView*helper;
    focusPoint=helper.xyz;
    helper=vec4(rayOrigin,1.0);
    helper=inverseView*helper;
    rayOrigin=helper.xyz;

    vec3 rayDirection=normalize(rayOrigin-focusPoint);

//    rayOrigin-=view[3].xyz;
//    rayDirection=transpose(mat3(view))*rayDirection;

    float distance;
    vec3 point;
    int numOfIterations;
    rayMarch(distance,point,numOfIterations,rayOrigin,rayDirection);

    return vec4(vec3(float(numOfIterations)/float(RAY_ITERATIONS)),1.0);
}


void main() {
    ivec2 texelCoord=ivec2(gl_GlobalInvocationID.xy);
    ivec2 textureSize=imageSize(outputTexture);

    vec2 size=vec2(textureSize);
    vec2 uv=vec2(texelCoord)/size;
    uv-=vec2(0.5);
    float aspect=size.x/size.y;
    uv.x*=aspect;
    //uv size

    imageStore(outputTexture,texelCoord,getColor(uv,size));
}