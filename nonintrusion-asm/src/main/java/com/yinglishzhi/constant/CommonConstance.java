package com.yinglishzhi.constant;

import com.yinglishzhi.TestMainJar;

/**
 * @author LDZ
 * @date 2020/8/6 5:06 下午
 */
public class CommonConstance {

    /**
     * java agent jar path
     */
    public static final String AGENT_JAR = "/Users/mtdp/dev/SELF/nonintrusion/nonintrusion-agent/build/libs/nonintrusion-agent-1.0-SNAPSHOT.jar";

    /**
     * 测试待入侵类
     */
    public static final Class<?> TEST_CLASS = TestMainJar.class;

    /**
     * agent args
     */
    public static final String AGENT_ARG = "/Users/mtdp/dev/SELF/struggle-object/src/Struggle/spy/target/spy-core.jar";

    /**
     * 要改变的类名
     */
    public static final String REPLACE_CLASS_NAME = "com/yinglishzhi/agentmain/TransClass";

    /**
     * 要改变的类的字节码
     */
    public static final String CLASS_NUMBER_RETURNS_2 = "/Users/zhiyinglish/dev/struggle-object/src/Struggle/agent/target/classes/com/yinglishzhi/agentmain/TransClass.class";

}
