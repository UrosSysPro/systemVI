package jni;

public class SDF {
    static {
        if (!LibraryLoader.load(Tools.class, "sdf"))
            System.loadLibrary("sdf");
    }
    public static native float sphere(float x,float y,float z,float radius);
}
