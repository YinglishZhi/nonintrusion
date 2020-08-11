package com.yinglishzhi;

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
 * @author LDZ
 * @date 2020/8/6 8:13 下午
 */
public class MyTransformer implements ClassFileTransformer {

    final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";

    // 被处理的方法列表
    final static Map<String, List<String>> methodMap = new HashMap<String, List<String>>();

    public MyTransformer() {
        System.out.println("transform add...");
        add("com.yinglishzhi.MyProgram.sayHello");
        add("com.yinglishzhi.MyProgram.sayHello2");
        System.out.println(methodMap.toString());
    }

    private void add(String methodString) {
        String className = methodString.substring(0, methodString.lastIndexOf("."));
        String methodName = methodString.substring(methodString.lastIndexOf(".") + 1);
        List<String> list = methodMap.computeIfAbsent(className, k -> new ArrayList<>());
        list.add(methodName);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        className = className.replace("/", ".");
        // 判断加载的class的包路径是不是需要监控的类
        if (methodMap.containsKey(className)) {
            try {
                System.out.println("------------------> " + methodMap.get(className));
                // 使用全称,用于取得字节码类 <使用javassist>
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                AdviceWeaver classAdapter = new AdviceWeaver(cw);
                cr.accept(classAdapter, ClassReader.EXPAND_FRAMES);
                for (String methodName : methodMap.get(className)) {
                    System.out.println("the method name is " + methodName);
                    printClass(cw.toByteArray());
                }
                return cw.toByteArray();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

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