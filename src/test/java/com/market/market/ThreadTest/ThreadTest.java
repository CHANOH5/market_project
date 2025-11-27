package com.market.market.ThreadTest;

public class ThreadTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("file.encoding = " + System.getProperty("file.encoding"));
        System.out.println("defaultCharset = " + java.nio.charset.Charset.defaultCharset());

        System.out.println("main start: " + Thread.currentThread().getName());

        // 1) Thread + Runnable 직접 생성
        Thread t1 = new Thread(() -> {
            System.out.println("t1 start: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000); // 1초뒤에 끝난다
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1 close: " + Thread.currentThread().getName());
        });

        Thread t2 = new Thread(() -> {
            System.out.println("t2 start: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500); //0.5초 뒤에 끝난다
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 close: " + Thread.currentThread().getName());
        });

        // 1) Thread + Runnable 직접 생성
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("t3 start: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t3 close: " + Thread.currentThread().getName());
            }
        });

        Runnable task4 = new Runnable() {
            @Override
            public void run() {
                System.out.println("t1 start: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 close: " + Thread.currentThread().getName());
            }
        };

        Thread t4 = new Thread(task4);

        t1.start(); // start로 JVM이 OS에게 요청 - 실제 스레드 만들어달라고
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("main 종료: " + Thread.currentThread().getName());

    } // main

} // end class
