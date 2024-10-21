package com.systemvi.collections;

interface Function{ 
    float calculate(float x);
}
class LinearFunction implements Function{
    public float calculate(float x) { return x;}
}
class SquaredFunction implements Function{
    public float calculate(float x) { return x*x;}
}

public class TestJava {
    public static void main(String[] args){
        Integer.parseInt("-1");
        Byte.toUnsignedInt((byte)245);
        Float.isNaN(Float.NaN+1);
//        "hello";
        System.out.println(sum(0,10,(x)->x*x));
        System.out.println(product(0,10,(x)->x*x));
        System.out.println(integral(0,10,1000,new SquaredFunction()));
    }

    public static float sum(int start, int end, Function f){
        float value=0;
        for(int i=start;i<=end;i++){
            value+=f.calculate(i);
        }
        return value;
    }
    public static float product(int start, int end, Function f){
        float value=1;
        for(int i=start;i<=end;i++){
            value*=f.calculate(i);
        }
        return value;
    }
    public static float integral(float start, float end,int steps, Function f){
        float value=0;
        float dx=(end-start)/steps;
        float x=start;
        while(x<end){
            value+=f.calculate(x)*dx;
            x+=dx;
        }
        return value;
    }
}
