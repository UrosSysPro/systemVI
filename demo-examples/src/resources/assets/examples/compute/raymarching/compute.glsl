#version 430 core
#define RAY_ITERATIONS 200
#define DELTA_EPSILON 0.001
#define MAX_DISTANCE 1000.0
#define FIXED_MIN_TRAVEL_DISTANCE 0.2
#define MAX_BOUNCES 20
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
vec3 translate(in vec3 p,in vec3 t){
    return p-t;
}

vec3 transform(in mat4 mat,in vec3 p){
    vec4 helper=mat*vec4(p,1.0);
    return helper.xyz;
}


float map(in vec3 p){
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
        plane(p,vec3(0.0,1.0,0.0),1.0)
    );
}

vec3 getNormal(in vec3 p){
    return normalize(vec3(
    map(p+vec3(DELTA_EPSILON,0.0,0.0))-map(p-vec3(DELTA_EPSILON,0.0,0.0)),
    map(p+vec3(0.0,DELTA_EPSILON,0.0))-map(p-vec3(0.0,DELTA_EPSILON,0.0)),
    map(p+vec3(0.0,0.0,DELTA_EPSILON))-map(p-vec3(0.0,0.0,DELTA_EPSILON))
    ));
}
struct Material{
    vec4 color;
    float roughness;
    float metalic;
};
Material materialOf(vec4 color,float roughness,float metalic){
    Material m;
    m.color=color;
    m.roughness=roughness;
    m.metalic=metalic;
    return m;
}
Material getMaterial(in vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return materialOf(vec4(0.3,0.6,0.9,1.0),1.0,0.5);
//    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return materialOf(vec4(getNormal(p)*0.5+0.5,1.0),1.0,0.5);
    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return materialOf(vec4(0.9,0.6,0.3,1.0),0.3,0.7);
//    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return materialOf(vec4(getNormal(p)*0.5+0.5,1.0),0.3,0.7);
    if(plane(p,vec3(0.0,1.0,0.0),1.0)<DELTA_EPSILON*2.0)return materialOf(vec4(0.6,0.8,0.3,1.0),1.0,0.0);
    return materialOf(vec4(0.9,0.9,1.0,1.0),1.0,0.0);
//    return materialOf(vec4(1.0),1.0,0.0);
//    return materialOf(vec4(0.0),1.0,0.0,1.0);
}
vec4 getColor(in vec3 p){
    if(box(translate(p,vec3(0.0,0.0,-10.0)), vec3(1.0))<DELTA_EPSILON*2.0)return vec4(0.3,0.6,0.9,1.0);
    if(sphere(translate(p,vec3(2.0,0.0,-10.0)), 1.0)<DELTA_EPSILON*2.0)return vec4(0.9,0.6,0.3,1.4);
    if(plane(p,vec3(0.0,1.0,0.0),1.0)<DELTA_EPSILON*2.0)return vec4(0.6,0.8,0.3,1.0);
    return vec4(0.3,0.6,0.9,1.0);
}


struct Ray{
    vec3 origin;
    vec3 direction;
};
struct RayMarchResult{
    vec3 point;
    float distance;
    int numOfIterations;
};
Ray rayOf(vec3 origin,vec3 direction){
    Ray r;
    r.origin=origin;
    r.direction=direction;
    return r;
}
RayMarchResult rayMarchResultOf(float distance,int numOfIterations,vec3 point){
    RayMarchResult r;
    r.distance=distance;
    r.numOfIterations=numOfIterations;
    r.point=point;
    return r;
}
RayMarchResult rayMarchResultOf(){
    return rayMarchResultOf(0.0,0,vec3(0.0));
}
RayMarchResult rayMarch(Ray ray){
    RayMarchResult result=rayMarchResultOf();
    return result;
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

vec4 calculateColor(vec2 uv,vec2 size){

    vec4 color=vec4(1.0);
    mat4 inverted=inverse(view);
    vec4 focus=inverted*vec4(0.0,0.0,2.2,1.0);
    vec4 point=inverted*vec4(uv,0.0,1.0);
    vec3 rayOrigin=vec3(focus.xyz);
    vec3 rayDirection=normalize(vec3(point.xyz)-rayOrigin);

    for(int i=0;i<MAX_BOUNCES;i++){
        float d;
        vec3 p;
        int n;
        rayMarch(d,p,n,rayOrigin,rayDirection);
        if(d>MAX_DISTANCE)break;
        vec3 normal=getNormal(p);
        Material material=getMaterial(p);
        rayOrigin=p+3.0*DELTA_EPSILON*normal;
        rayDirection=reflect(rayDirection,normal);
        color*=material.color;
    }
    return color;
//    val color = new Vector4f(1)
//    val rayOrigin = new Vector3f()
//    val rayDirection = new Vector3f()
//    val inverted = new Matrix4f(camera.view).invert
//    val focus = new Vector4f(0, 0, focalLength, 1).mul(inverted)
//    val point = new Vector4f(x, y, 0, 1).mul(inverted)
//    rayOrigin.set(focus.x, focus.y, focus.z)
//    rayDirection.set(point.x, point.y, point.z).sub(rayOrigin).normalize()
//
//    val reflectedDirection = new Vector3f()
//    val diffusedDirection = new Vector3f()
//    val randomVector = new Vector3f()
//
//    for (_ <- 0 until bounces) RayMarch(rayOrigin,rayDirection,iterations) match{
//        case RayOutput(p, d, outOfRange, normal, material)=>
//        rayOrigin.set(p).add(normal.x * 2 * epsilon, normal.y * 2 * epsilon, normal.z * 2 * epsilon)
//        reflectedDirection.set(rayDirection).reflect(normal).add(
//        randomVector.set(
//        random.nextFloat()*2-1,
//        random.nextFloat()*2-1,
//        random.nextFloat()*2-1
//        ).normalize().mul(material.roughness)
//        ).normalize()
//        diffusedDirection.set(normal).add(
//        randomVector.set(
//        random.nextFloat()*2-1,
//        random.nextFloat()*2-1,
//        random.nextFloat()*2-1
//        ).normalize()
//        ).normalize()
//        rayDirection.set(reflectedDirection.mul(material.metallic)).add(diffusedDirection.mul(1-material.metallic))
//        color.mul(material.color)//.add(material.emission)
//        if (outOfRange) return color
//    }
//    color
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