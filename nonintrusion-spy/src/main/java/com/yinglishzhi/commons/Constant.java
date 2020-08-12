package com.yinglishzhi.commons;

import com.yinglishzhi.Heck;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * @author LDZ
 * @date 2020/8/12 11:11 上午
 */
public class Constant {

    public static String generateDescriptor(Class<?> returnType, Class<?>... parameterType) {
        StringBuilder result = new StringBuilder("(");
        for (Class<?> clazz : parameterType) {
            result.append(Type.getType(clazz).toString());
        }
        result.append(")");
        result.append(Type.getType(returnType).toString());
        return result.toString();
    }

    /**
     * exception type
     */
    public static final Type ASM_TYPE_OBJECT = Type.getType(Object.class);
    public static final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);

    /**
     * no such method exception type
     */
    public static final Type ASM_TYPE_NO_SUCH_METHOD_EXCEPTION = Type.getType(NoSuchMethodException.class);

    /**
     * exception type
     */
    public static final Type ASM_TYPE_EXCEPTION = Type.getType(Exception.class);

    /**
     * class type
     */
    public static final Type ASM_TYPE_CLASS = Type.getType(Class.class);

    /**
     * String type
     */
    public static final Type ASM_TYPE_STRING = Type.getType(String.class);

    /**
     * 反射执行
     */
    public static final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);

    /**
     * heck type
     */
    public static final Type ASM_TYPE_HECK = Type.getType(Heck.class);

    /**
     * get method
     */
    public static final String GET_METHOD = "getMethod";

    /**
     * the method 'init' of Heck class
     */
    public static final String INIT = "init";

    /**
     *
     */
    public static final String PRINT_STACK_TRACE = "printStackTrace";

    /* todo ============================== config ============================== */

    /**
     * 侵入类 type
     */
    public static final Type ASM_TYPE_MY_INVADE = Type.getType("Lcom/yinglishzhi/intrusion/MyInvade;");

    public static final String INVADE_METHOD_NAME = "catLogReport";

    // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    public static void main(String[] args) {
        String result = generateDescriptor(Method.class, String.class, Class[].class);
        System.out.println(result);
    }
}
