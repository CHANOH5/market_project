package com.market.market.ThreadTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    // 이제 스레드를 직접 만들지 않고 ThreadPool이 관리해줌

    public static void main(String[] args) throws InterruptedException {

        System.out.println("main start: " + Thread.currentThread().getName());

        // 1. 고정된 크기의 스레드 풀
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // 2. 일 10개를 스레드 풀에게 맡기기
        for (int i = 0; i <= 10; i ++) {
            int taskId = i;
            pool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("task " + taskId + " start (Thread " + threadName + ") !!!!");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("task " + taskId + " stop (Thread " + threadName + ") !!!!");
            });
        }

        pool.shutdown();

        // 4) 일정 시간 동안 모두 끝날 때까지 대기
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("모든 작업 종료 여부: " + finished);
        System.out.println("main 종료: " + Thread.currentThread().getName());

    }


} // end class
