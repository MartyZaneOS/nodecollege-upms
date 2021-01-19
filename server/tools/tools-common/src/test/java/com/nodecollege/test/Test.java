package com.nodecollege.test;

/*
 * 发起20个线程，每个线程对race变量进行10000次自增操作，如果代码能够正确并发，
 * 则最终race的结果应为200000，但实际的运行结果却小于200000。
 * 
 * @author Colin Wang 
 */
public class Test { 
	public static volatile int race = 0; 
	public static void increase() {
        race++;
    } 
	public static void increase2() {
        race = race + 1;
    }
    private static final int THREADS_COUNT = 20;
    public static void main(String[] args) {
		Thread[] threads = new Thread[THREADS_COUNT]; 
		for (int i = 0; i < THREADS_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override public void run() { for (int i = 0; i < 10000; i++) {
                        increase2();
                    }
                }
            });
            threads[i].start();
        } 
        while (Thread.activeCount() > 1)
            Thread.yield();
        System.out.println(race);
        Integer a1 = 128;
        Integer a2 = 128;
        int a3 = 128;
        int a4 = 128;
        System.out.println(a1==a2);
        System.out.println(a1==a3);
        System.out.println(a3==a4);
    }
}