// ----------------------------------------------------------------
// Path Tracing (Disney BRDF)
// ----------------------------------------------------------------
//
// This is not optimized, I tried to keep it clear.
// The BRDF has only the diffuse and specular parts (isotropic)
// for now but others parameters should be easy to implement at
// this point.
//
// I'm not sure about the pdf part, but I think it's ok.
// If you want to give me some feedback about that you are welcome:)
//
//
// You can move the camera with the mouse
// and zoom in/out with 'w' and 's'.
//
// - Buffer A : Draw a raw frame
// - Buffer B : Accumulate frame
//              You can change the mode to get TAAish output
// - Image : Basic ACES tonemapping
// ----------------------------------------------------------------

vec3 ACES(const vec3 x) {
    const float a = 2.51;
    const float b = 0.03;
    const float c = 2.43;
    const float d = 0.59;
    const float e = 0.14;
    return (x * (a * x + b)) / (x * (c * x + d) + e);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;
    vec3 col = texture(iChannel0, uv).rgb;

    #if !FURNACE_TEST
    col = ACES(col);
    #endif

    fragColor = vec4(pow(col, vec3(1./2.2)),1.0);
}