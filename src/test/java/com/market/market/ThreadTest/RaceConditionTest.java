package com.market.market.ThreadTest;

public class RaceConditionTest {

    // 여러 스레드가 같이 사용하는 공유 변수
    private static int counter = 0;

    // 공유 자원 수정에 락 걸기 (synchronized)
    private static synchronized void increment() {
        counter++;
    }

    public static void main(String[] args) throws InterruptedException {

        int threadCount = 10;
        int perThread = 100_000; // 각 스레드가 10만 번씩 증가

        Thread[] threads = new Thread[threadCount];

        // 1. 스레드 생성
        for(int i = 0; i < threadCount; i++) { // 스레드 10개 반복
            threads[i] = new Thread(() -> {
               for(int j = 0; j < perThread; j++) { // 각 스레드 안에서 counter를 10만번 호출
//                   counter++; // 여기서 레이스 컨디션 발생
                   increment(); // 그래서 기대값 1,000,000 예상
               }
            });
        } // for

        // 2. 스레드 시작
        for(Thread t : threads) {
            t.start();
        }

        // 모두 끝날 때까지 대기
        for(Thread t : threads) {
            t.join();
        }

        int expected = threadCount * perThread;
        System.out.println("기대값: " + expected);
        System.out.println("실제값: " + counter);

    } // main

} // end class
