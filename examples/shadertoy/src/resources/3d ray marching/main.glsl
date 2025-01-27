float sphereSdf(float r, vec3 p){
    return length(p)-r;
}

float unionSdf(float d1,float d2){
    return min(d1,d2);
}

float differenceSdf(float d1,float d2){
    return max(d1,-d2);
}

vec3 translateSdf(vec3 t,vec3 p){
    return p-t;
}

float sdf(vec3 p){
    return differenceSdf(
        differenceSdf(
            sphereSdf(100.0,
                translateSdf(
                    vec3(0,0,-300),
                    p
                )
            ),
            sphereSdf(50.0,
                translateSdf(
                    vec3(70,0,-200),
                    p
                )
            )
        ),
        sphereSdf(50.0,
            translateSdf(
                vec3(-70,0,-200),
                p
            )
        )
    );
}

float rayMarch(vec3 rayOrigin, vec3 rayDirection){
    float d=0;
    for(int i=0;i<100;i++){
        vec3 p=rayOrigin+d*rayDirection;
        float step=sdf(p);
        d+=step;
        if(d<0.001 || d>1000.0)break;
    }
    return min(d,1000.0);
}

vec3 normalSdf(vec3 p){
    return normalize(vec3(
        sdf(p+vec3(1,0,0))-sdf(p-vec3(1,0,0)),
        sdf(p+vec3(0,1,0))-sdf(p-vec3(0,1,0)),
        sdf(p+vec3(0,0,1))-sdf(p-vec3(0,0,1))
    ));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec3 lightPosition=vec3(0,200,-150)+vec3(sin(iTime*2)*200,0,cos(iTime*2)*200);
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy*2-1;
    uv.x*=iResolution.x/iResolution.y;

    vec3 rayOrigin=vec3(0,0,2);
    vec3 rayDirection=normalize(vec3(uv,0)-rayOrigin);

    float d=rayMarch(rayOrigin,rayDirection);
    if(d>900){
        fragColor=vec4(0);
    }else{
//        vec3 rayHit=rayOrigin+d*rayDirection;
//        fragColor=vec4(normalSdf(rayHit)/2+0.5,1.0);
        vec3 rayHit=rayOrigin+d*rayDirection;
        rayDirection=normalize(lightPosition-rayHit);
        d=rayMarch(rayHit+rayDirection*0.01,rayDirection);
//        fragColor=vec4(d/1000);
        if(d>distance(rayHit,lightPosition)){
            fragColor=vec4(1);
        }else{
            fragColor=vec4(0.5);
        }
    }
}