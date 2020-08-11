package com.yinglishzhi;

import java.lang.instrument.Instrumentation;

/**
 * @author LDZ
 * @date 2019-10-25 17:56
 */
public class SpyCore {
    private Instrumentation instrumentation;
    private static volatile SpyCore spyCore;

    private SpyCore(Instrumentation inst) {
        this.instrumentation = inst;
        printClasses(inst);
    }

    private static void printClasses(Instrumentation inst) {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        System.out.println(allLoadedClasses.length + "哈哈");
    }

    public void test() {
        System.out.println("执行 test 方法");
        Instrumentation inst = spyCore.instrumentation;
        inst.addTransformer(new MyTransformer());
        System.out.println("=========");
    }

    public static SpyCore getInstance(Instrumentation instrumentation) {
        if (null == spyCore) {
            synchronized (SpyCore.class) {
                if (null == spyCore) {
                    spyCore = new SpyCore(instrumentation);
                }
            }
        }
        return spyCore;
    }
}
