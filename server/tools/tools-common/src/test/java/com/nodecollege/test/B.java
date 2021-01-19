package com.nodecollege.test;

class A {
    public A() {
        System.out.println("1A类的构造方法");
    }
    {
        System.out.println("2A类的构造快");
    }
    static {
        System.out.println("3A类的静态块");
    }
}
 
public class B extends A {
    public B() {
        System.out.println("4B类的构造方法");
    }
    {
        System.out.println("5B类的构造快");
    }
    static {
        System.out.println("6B类的静态块");
    }
    public static void main(String[] args) {
        System.out.println("7");
        new B();
        new B();
        System.out.println("8");
    }
}