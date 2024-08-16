package com.systemvi.examples.parallel;

public class Parallel {
    public Parallel() throws Exception {
        Thread[] threads=new Thread[10];
        for(int i=0; i<threads.length; i++) {
            int index = i;
            threads[i]=new Thread(()->{
                sleep(index * 1000);
                System.out.println("hello from thread"+index);
            });
            threads[i].start();
        }

        System.out.println("hello from main");

        for(int i=0; i<threads.length; i++) {
            threads[i].join();
        }
    }

    public static void sleep(long millis){
        try{
            Thread.sleep(millis);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

