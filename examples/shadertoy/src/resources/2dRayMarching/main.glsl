float circleSdf(float r, vec2 p){
    return length(p)-r;
}

float unionSdf(float d1,float d2){
    return min(d1,d2);
}

float differenceSdf(float d1,float d2){
    return max(d1,-d2);
}

vec2 translateSdf(vec2 t,vec2 p){
    return p-t;
}

float sdf(vec2 p){
    return differenceSdf(
        circleSdf(100.0,
            translateSdf(
                vec2(350.0,300.0),
                p
            )
        ),
        circleSdf(100.0,
            translateSdf(
                vec2(450.0,300.0),
                p
            )
        )
    );
}

float rayMarch(vec2 rayOrigin, vec2 rayDirection){
    float d=0;
    for(int i=0;i<100;i++){
        vec2 p=rayOrigin+d*rayDirection;
        float step=sdf(p);
        d+=step;
        if(d<0.001 || d>1000.0)break;
    }
//    return d;
    return min(d,1000.0);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{

    vec2 rayOrigin=fragCoord;
    vec2 rayDirection=normalize(iMouse.xy-rayOrigin);
    float lightDistance=distance(iMouse.xy,rayOrigin);
    float d=rayMarch(rayOrigin,rayDirection);
    float color=(1000.0-lightDistance)/1000.0;
    color*=color;
    fragColor=mix(vec4(0.0),vec4(color),float(d>distance(rayOrigin,iMouse.xy)));
}