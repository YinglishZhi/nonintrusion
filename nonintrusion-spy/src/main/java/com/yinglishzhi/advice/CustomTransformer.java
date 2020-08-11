package com.yinglishzhi.advice;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义 transformer
 *
 * @author LDZ
 * @date 2020/8/6 8:13 下午
 */
public class CustomTransformer implements ClassFileTransformer {

    /**
     * 被处理方法列表
     */
    private final static Map<String, List<String>> METHOD_MAP = new HashMap<>();

    public CustomTransformer() {
        add("com.yinglishzhi.MyProgram.sayHello");
        add("com.yinglishzhi.MyProgram.sayHello2");
        System.out.println(METHOD_MAP.toString());
    }

    private void add(String methodString) {
        String className = methodString.substring(0, methodString.lastIndexOf("."));
        String methodName = methodString.substring(methodString.lastIndexOf(".") + 1);
        List<String> list = METHOD_MAP.computeIfAbsent(className, k -> new ArrayList<>());
        list.add(methodName);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        className = className.replace("/", ".");
        // 判断加载的class的包路径是不是需要监控的类
        if (METHOD_MAP.containsKey(className)) {
            try {
                System.out.println("------------------> " + METHOD_MAP.get(className));
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                AdviceWeaver classAdapter = new AdviceWeaver(cw, METHOD_MAP.get(className));
                cr.accept(classAdapter, ClassReader.EXPAND_FRAMES);
                printClass(cw.toByteArray());
                return cw.toByteArray();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

    /**
     * 打印 class
     *
     * @param bytes 字节流
     */
    private static void printClass(byte[] bytes) {
        File file = new File("/Users/mtdp/util/log/test.class");
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            fout.write(bytes);
            fout.close();
        } catch (IOException e) {
            // ignore
        }
    }

}