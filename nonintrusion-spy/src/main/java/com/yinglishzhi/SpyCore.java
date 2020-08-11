package com.yinglishzhi;

import com.yinglishzhi.advice.CustomTransformer;

import java.lang.instrument.Instrumentation;

/**
 * 间谍类 核心
 *
 * @author LDZ
 * @date 2019-10-25 17:56
 */
public class SpyCore {

    private final Instrumentation instrumentation;
    private static volatile SpyCore spyCore;

    private SpyCore(Instrumentation inst) {
        this.instrumentation = inst;
        printClasses(inst);
    }

    private static void printClasses(Instrumentation inst) {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        System.out.println(allLoadedClasses.length + "哈哈");
    }

    public void invade() {


        System.out.println("============ 执行 侵入 方法 ============");
        Instrumentation inst = spyCore.instrumentation;
        inst.addTransformer(new CustomTransformer());
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
