#version 430 core
#define RAY_ITERATIONS 100
#define DELTA_EPSILON 0.01
#define MAX_DISTANCE 1000.0
#define FIXED_MIN_TRAVEL_DISTANCE 0.2
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

vec3 transform(in mat4 mat,in vec3 p){
    vec4 helper=mat*vec4(p,1.0);
    return helper.xyz;
}

float map(vec3 p){
    return unionSDF(
        unionSDF(
            box(
                translate(p,vec3(0.0,0.0,-10.0)),
                vec3(1)
            ),
            sphere(
                translate(p,vec3(2.0,0.0,-10.0)),
                1.0
            )
        ),
        plane(p,vec3(0.0,1.0,0.0),3.0)
    );
}
vec4 color(vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return vec4(0.3,0.6,0.9,1.0);
    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return vec4(0.9,0.6,0.3,1.0);
    if(plane(p,vec3(0.0,1.0,0.0),3.0)<DELTA_EPSILON*2.0)return vec4(0.6,0.8,0.3,1.0);
    return vec4(0.0);
}

void rayMarch(out float distance,out vec3 endPoint,out int numOfIterations,in vec3 rayOrigin,in vec3 rayDirection){
    distance=0;
    endPoint=vec3(rayOrigin);
    for(int i=0;i<RAY_ITERATIONS;i++){
        numOfIterations=i;
        float d=map(rayOrigin+distance*rayDirection);
        distance+=d;
        if(distance>MAX_DISTANCE||d<DELTA_EPSILON){
            numOfIterations=i;
            endPoint=rayOrigin+distance*rayDirection;
            return;
        }
    }
    numOfIterations=RAY_ITERATIONS;
    endPoint=rayOrigin+distance*rayDirection;
}
vec3 getNormal(vec3 p){
    return normalize(vec3(
        map(p+vec3(DELTA_EPSILON,0.0,0.0))-map(p-vec3(DELTA_EPSILON,0.0,0.0)),
        map(p+vec3(0.0,DELTA_EPSILON,0.0))-map(p-vec3(0.0,DELTA_EPSILON,0.0)),
        map(p+vec3(0.0,0.0,DELTA_EPSILON))-map(p-vec3(0.0,0.0,DELTA_EPSILON))
    ));
}

void phongData(){

}
void phongWithReflectionsData(){

}
void pbrData(){

}
vec4 phong(){
    return vec4(0.0);
}

vec4 phongReflections(){
    return vec4(0.0);
}

vec4 pbr(){
    return vec4(0.0);
}

vec4 calculateColor(vec2 uv,vec2 size){
    //rayDir rayOrigin
    mat4 inverseView=inverse(view);
    vec4 helper;
    float f=2.0;

    vec3 cameraPosition=-view[3].xyz;
    vec3 focusPoint=transform(inverseView,vec3(0.0,0.0,f));
    vec3 rayOrigin=transform(inverseView,vec3(uv,0.0));
    vec3 rayDirection=normalize(rayOrigin-focusPoint);

    float sceneToCameraDistance;
    vec3 firstHit;
    int numOfIterations;
    rayMarch(sceneToCameraDistance,firstHit,numOfIterations,rayOrigin,rayDirection);

    vec4 firstHitColor=color(firstHit);
    vec3 normal=getNormal(firstHit);

    vec3 lightPosition=vec3(2.0,10.0,-8.0);
    rayOrigin=firstHit-rayDirection*DELTA_EPSILON*2.0;
    rayDirection=normalize(lightPosition-rayOrigin);
    float sceneToLightDistance;
    float lightDistance=length(firstHit-lightPosition);
    vec3 secondHit;
    rayMarch(sceneToLightDistance,secondHit,numOfIterations,rayOrigin,rayDirection);

    vec4 finalColor=vec4(firstHitColor.rgb*mix(1.0,0.4,float(sceneToLightDistance<lightDistance)),1.0);

    return finalColor;
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

    imageStore(outputTexture,texelCoord,calculateColor(uv,size));
}