#include<jni.h>
#include"jni_SDF.h"
//#include<cmath>
//#include<iostream>

JNIEXPORT jfloat JNICALL Java_jni_SDF_sphere(JNIEnv* env, jclass cls, jfloat x, jfloat y, jfloat z, jfloat radius){
//    std::cout<<"hello from sdf circle"<<std::endl;
    return (jfloat)42.0;
}