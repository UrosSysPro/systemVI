// ---------------------------------------------
// - Camera/data IO + Frames accumulation
//
//
// MODE 0 - Raw frame
// MODE 1 - Accumulate frames
// MODE 2 - Temporal reprojection
// ---------------------------------------------
#define MODE 1


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec3 seed = hash3(vec3(fragCoord.xy, float((iFrame+1) % 10000)));
    vec2 invRes = vec2(1.) / iResolution.xy;
    vec2 uv = (fragCoord + seed.xy-.5) * invRes;

    // read input buffer
    vec2 rawUv = fragCoord*invRes;
    vec4 rawCol = texture(iChannel1, rawUv);
    vec4 col = rawCol;

    // read data
    bool needRefresh = false;
    Data data = readData(iChannel0, invRes);
    if (iFrame == 0) data = initData();

    // camera
    if (iMouse.zw == data.oldMouse.zw) {
        data.theta += (data.oldMouse.x - iMouse.x)  * CAMERA_SENSITIVTY;
        data.phi += (data.oldMouse.y - iMouse.y)  * CAMERA_SENSITIVTY;
        if (texelFetch( iChannel2, ivec2(87,0), 0 ).x > 0.) { // w
            data.r *= 1.-CAMERA_SENSITIVTY;
            needRefresh = true;
        }
        if (texelFetch( iChannel2, ivec2(83,0), 0 ).x > 0.) { // s
            data.r *= 1.+CAMERA_SENSITIVTY;
            needRefresh = true;
        }
        if (texelFetch( iChannel2, ivec2(32,0), 0 ).x > 0.) { // space
            needRefresh = true;
        }
        if (iMouse.z > .5)
        needRefresh = true;
    }
    data.phi = clamp(data.phi, 0.01, 3.);

    data.ro = normalize(vec3(cos(data.theta), data.phi, sin(data.theta)))*data.r;
    data.ta = vec3(0.,0.5,0.);


    // MODE 1 - accumulate frame
    #if MODE == 1
    vec4 lastCol = texture(iChannel0, fragCoord*invRes);
    if (!needRefresh) {
        float w = 1. / (float(iFrame)-data.refreshTime + 1.);
        col = lastCol*(1.-w) + col * w;
    }
    else {
        data.refreshTime = float(iFrame);
    }
    #endif

    // MODE 2 - temporal reprojection
    #if MODE == 2
    if (col.a > 0.) {
        // reconstruct world space position
        vec3 ro = data.ro;
        vec2 v = uv*2.-1.;
        v.x *= iResolution.x * invRes.y;
        vec3 rd = lookat(data.ro, data.ta) * normalize(vec3(v,FOCAL_LENGTH));
        float t = rawCol.a;
        vec3 p = ro + rd * t;

        // reprojection
        mat3 oldCam = lookat(data.oldRo, data.oldTa);
        mat3x4 invOldCam = mat3x4( vec4( oldCam[0], -dot(oldCam[0],data.oldRo) ),
        vec4( oldCam[1], -dot(oldCam[1],data.oldRo) ),
        vec4( oldCam[2], -dot(oldCam[2],data.oldRo) ));
        vec4 wpos = vec4(p,1.0);
        vec3 cpos = wpos*invOldCam;
        vec2 npos = FOCAL_LENGTH*cpos.xy/cpos.z;
        vec2 spos = 0.5 + 0.5*npos*vec2(iResolution.y/iResolution.x,1.0) - (seed.xy-.5)*invRes;
        vec2 rpos = spos * iResolution.xy;
        vec4 lastCol = texture(iChannel0, spos);

        float w = .0;
        vec3 oldRd = lookat(data.oldRo, data.oldTa) * normalize(vec3(v,FOCAL_LENGTH));
        vec3 oldwp = data.oldRo + oldRd * lastCol.w;

        // depth rejection
        for(int x=-1; x<=1; x++){
            for(int y=-1; y<=1; y++){
                float lastT = texture(iChannel0, spos + vec2(x, y) * invRes).a;
                w = max(w, smoothstep(t*t*.005,0.,abs(t-lastT)));
            }
        }
        w -= 0.02;

        // color clamping
        #if 1
        const int kernelSize = 2;
        vec3 minCol = vec3(99999.);
        vec3 maxCol = vec3(0.);
        for(int x=-kernelSize; x<=kernelSize; x++){
            for(int y=-kernelSize; y<=kernelSize; y++){
                vec4 c = texture(iChannel1, spos + vec2(x, y) * invRes);
                c.rgb = RGBToYCoCg(c.rgb);
                minCol = min(minCol, c.rgb);
                maxCol = max(maxCol, c.rgb);
            }
        }
        lastCol.rgb = RGBToYCoCg(lastCol.rgb);
        lastCol.rgb = clamp(lastCol.rgb, minCol, maxCol);
        lastCol.rgb = YCoCgToRGB(lastCol.rgb);
        #endif

        //w = 0.99;
        if (rpos.x < 7. && rpos.y < 1.) w = 0.;
        if (abs(spos.x-.5) > .5) w = 0.;
        if (abs(spos.y-.5) > .5) w = 0.;

        if (iFrame > 1)
        col = mix(col, lastCol, saturate(w));
    }
    #endif


    // write data
    data.oldRo = data.ro;
    data.oldTa = data.oldTa;
    data.oldMouse = iMouse;
    if (fragCoord.y < 1.)
    col = writeData(col, fragCoord, data);

    // output pixel color
    fragColor = col;
}