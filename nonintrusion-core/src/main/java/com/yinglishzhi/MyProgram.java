package com.yinglishzhi;

/**
 * @author LDZ
 * @date 2020/8/6 8:20 下午
 */
public class MyProgram {
    public static void main(String[] args) {
        sayHello();
        sayHello2("hello world222222222");


        sayHello();
    }

    public static void sayHello() {
        try {
            Thread.sleep(2000);
            System.out.println("hello world!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sayHello2(String hello) {
        try {
            Thread.sleep(1000);
            System.out.println(hello);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}