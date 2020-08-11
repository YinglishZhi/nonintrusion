package com.yinglishzhi;

import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * agent main
 *
 * @author LDZ
 * @date 2020/8/6 2:58 下午
 */
@Slf4j
public class AgentMain {

    /**
     * 打印 class
     */
    private static final Consumer<Instrumentation> CLASSES_PRINT = inst -> {
        Arrays.stream(inst.getAllLoadedClasses()).forEach(clazz -> log.info("class = {}", clazz.getName()));
    };

    /**
     * 在java程序启动后附加 agent
     * vm.loadAgent(jar);
     * 必须在jar包的 manifest文件中指定 Agent-Class 为当前类
     *
     * @param agentArgs agent 参数
     * @param inst      inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("执行了 agentmain 方法......");
        log.info("调用堆栈信息......");
        StackTraceElement[] se = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : se) {
            log.info(stackTraceElement.toString());
        }
        log.info("#########################");
        inst.addTransformer(new Transformer(), true);
        CLASSES_PRINT.accept(inst);
        try {
            inst.retransformClasses(TransClass.class);
        } catch (UnmodifiableClassException e) {
            log.error("error", e);
        }
        log.info("Agent Main Done");
    }

    /**
     * 启动参数的方式执行的是这个方法
     * java -javaagent:xxxx.jar
     * 必须在 jar包的 manifest文件中指定 Premain-Class 为当前类
     *
     * @param agentArgs agent 参数
     * @param inst      inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("执行了 premain 方法......");
        log.info("调用堆栈信息......");
        StackTraceElement[] se = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTraceElement : se) {
            log.info(stackTraceElement.toString());
        }
        log.info("#########################");
        inst.addTransformer(new Transformer());
        log.info("Agent Main Done");
    }
}
