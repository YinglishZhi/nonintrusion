package com.yinglishzhi;

/**
 * @author LDZ
 * @date 2020/8/6 8:20 下午
 */
public class MyProgram {
    public MyProgram() {
    }

    public static void main(String[] args) {
        sayHello();
        sayHello2("hello world222222222");
    }

    public static void sayHello() {
        int a = add(1, 2);
        String s = "100";
        try {
            Thread.sleep(2000);
            System.out.println(111);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("biubiubiu" + a + s);

        try {
            System.out.println("biubiubiu111");
        } catch (Exception e) {
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

    private static int add(int a, int b) {
        return a + b;
    }

}