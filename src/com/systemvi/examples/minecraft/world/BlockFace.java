//package com.systemvi.examples.minecraft.world;
//
//import com.systemvi.engine.texture.TextureRegion;
//import org.joml.Matrix3f;
//import org.joml.Matrix4f;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
//public class BlockFace {
//    public static class FaceInfo{
//        public Vector3f[] points;
//        public Vector3f normal,tangent,bitangent;
//        public FaceInfo(Vector3f[] points,Vector3f normal,Vector3f tangent,Vector3f bitangent){
//            this.points=points;
//            this.normal=normal;
//            this.tangent=tangent;
//            this.bitangent=bitangent;
//        }
//    }
//    public static Vector3f[] UP=new Vector3f[]{
//      new Vector3f(-0.5f, 0.5f, 0.5f),
//      new Vector3f(-0.5f, 0.5f,-0.5f),
//      new Vector3f( 0.5f, 0.5f,-0.5f),
//      new Vector3f( 0.5f, 0.5f, 0.5f)
//    },DOWN=new Vector3f[]{
//        new Vector3f(-0.5f,-0.5f, 0.5f),
//        new Vector3f(-0.5f,-0.5f,-0.5f),
//        new Vector3f( 0.5f,-0.5f,-0.5f),
//        new Vector3f( 0.5f,-0.5f, 0.5f)
//    },LEFT=new Vector3f[]{
//        new Vector3f(-0.5f, 0.5f, 0.5f),
//        new Vector3f(-0.5f, 0.5f,-0.5f),
//        new Vector3f(-0.5f, -0.5f, 0.5f),
//        new Vector3f(-0.5f, -0.5f, -0.5f),
//    },RIGHT=new Vector3f[]{
//        new Vector3f( 0.5f, 0.5f, 0.5f),
//        new Vector3f( 0.5f, 0.5f,-0.5f),
//        new Vector3f( 0.5f, -0.5f, 0.5f),
//        new Vector3f( 0.5f, -0.5f, -0.5f),
//    },FRONT=new Vector3f[]{
//        new Vector3f(0.5f,0.5f,0.5f),
//        new Vector3f(0.5f,-0.5f,0.5f),
//        new Vector3f(-0.5f,0.5f,0.5f),
//        new Vector3f(-0.5f,-0.5f,0.5f),
//    },BACK=new Vector3f[]{
//        new Vector3f(0.5f,0.5f,-0.5f),
//        new Vector3f(0.5f,-0.5f,-0.5f),
//        new Vector3f(-0.5f,0.5f,-0.5f),
//        new Vector3f(-0.5f,-0.5f,-0.5f),
//    };
////
////    public static FaceInfo UP,DOWN,LEFT,RIGHT,FRONT,BACK;
////    public static void loadFaceInfo(){
////        float size=0.5f;
////        Vector3f[] points=new Vector3f[]{
////            new Vector3f(-size,-size,0),
////            new Vector3f( size, size,0),
////            new Vector3f(-size, size,0),
////            new Vector3f( size,-size,0),
////        };
////        Vector3f normal=new Vector3f(0,0,1);
////        Vector3f tangent=new Vector3f(1,0,0);
////        Vector3f bitangent=new Vector3f(0,1,0);
////
////        Matrix3f rotation=new Matrix3f();
////
//////        FRONT=
////    }
//
//    public Vector3i position;
//    public TextureRegion region;
//    public Vector3f[] points;
//
//    public BlockFace(Vector3i position,TextureRegion region,Vector3f[] points){
//        this.points=points;
//        this.position=position;
//        this.region=region;
//    }
//}
