package com.systemvi.examples.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Concurrent {
    public Concurrent() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        for(int i=0;i<10;i++){
            final int index=i;
            Future<Integer> f=service.submit(()->{
                try {
                    Thread.sleep(index*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("hello from task"+index);
                return index;
            });
        }
        service.shutdown();
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }
}
