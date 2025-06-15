package com.systemvi.parallel.thread;

import java.util.Random;
import java.util.concurrent.*;

public class Main {
    
    public static int calculation(int value){
        return (int)(Math.sin(value/10000f)*100);
    }

    public static void singleThread(int[][] niz){
        long start=System.currentTimeMillis();
        for(int i=0;i<niz.length;i++){
            int sum=0;
            for(int j=0;j<niz[i].length;j++){
                sum+=calculation(niz[i][j]);
            }
//            System.out.printf("column: %2d sum: %10d\n",i,sum);
        }
        long end=System.currentTimeMillis();
        System.out.printf("time: %5d",end-start);
    }

    public static void multiThread(int[][] niz) throws InterruptedException {
        long start=System.currentTimeMillis();
        Thread[] threads = new Thread[niz.length];
        for(int k=0;k<niz.length;k++){
            int index=k;
            threads[k]=new Thread(()->{
                int sum=0;
                for(int j=0;j<niz[index].length;j++){
                    sum+=calculation(niz[index][j]);
                }
//                System.out.printf("column: %2d sum: %10d\n",index,sum);
            });
            threads[k].start();
        }
        for(int k=0;k<threads.length;k++){
            threads[k].join();
        }
        long end=System.currentTimeMillis();
        System.out.printf("time: %5d",end-start);
    }
    public static void concurent(int[][] niz) throws ExecutionException, InterruptedException {
        ExecutorService service= Executors.newFixedThreadPool(8);
        long start=System.currentTimeMillis();
        Future[] futures=new Future[niz.length];
        for(int k=0;k<niz.length;k++){
            int index=k;
            futures[k]=service.submit(()->{
                int sum=0;
                for(int j=0;j<niz[index].length;j++){
                    sum+=calculation(niz[index][j]);
                }
//                System.out.printf("column: %2d sum: %10d\n",index,sum);
            });
        }
        for(int k=0;k<futures.length;k++){
            futures[k].get();
        }
        long end=System.currentTimeMillis();
        System.out.printf("time: %5d",end-start);
        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }
    public static void main(String[] args)throws  Exception {
        int size=1000000;
        int[][] niz=new int[100][size];
        Random random=new Random();
        for (int i = 0; i < niz.length; i++) {
            for (int j = 0; j < niz[i].length; j++) {
                niz[i][j]=random.nextInt(size);
            }
        }
        singleThread(niz);
        System.out.println();
        multiThread(niz);
        System.out.println();
        concurent(niz);
    }
}
