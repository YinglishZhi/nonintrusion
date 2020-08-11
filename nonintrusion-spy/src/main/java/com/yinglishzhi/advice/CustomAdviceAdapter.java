package com.yinglishzhi.advice;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

/**
 * 自定义 method visitor
 *
 * @author LDZ
 * @date 2019-10-28 15:02
 */
public class CustomAdviceAdapter extends AdviceAdapter {

    CustomAdviceAdapter(final int api, MethodVisitor methodVisitor, int access, String name, String desc) {
        super(api, methodVisitor, access, name, desc);
    }

    /**
     * 黑客类
     */
    private final Type ASM_TYPE_SPY = Type.getType("Lcom/yinglishzhi/Heck;");

    /**
     * 反射执行
     */
    private final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);

    /**
     * String type
     */
    private final Type ASM_TYPE_STRING = Type.getType(String.class);

    /**
     * 反射方法
     */
    private final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");

    /**
     * Lebel for try...catch block
     */
    private final Label from = new Label();
    private final Label to = new Label();
    private final Label target = new Label();

    @Override
    protected void onMethodEnter() {
        // 标志 try 开始的地方
        visitLabel(from);
        visitTryCatchBlock(
                from,
                to,
                target,
                "java/lang/Exception"
        );

        final StringBuilder append = new StringBuilder();
        _debug(append, "debug:onMethodEnter()");

        // 加载 before 方法
        getStatic(ASM_TYPE_SPY, "TEST_METHOD", ASM_TYPE_METHOD);
        _debug(append, "loadAdviceMethod()");

        // 反射调用 黑客类方法
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ICONST_1);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitLdcInsn("comm");
        mv.visitInsn(AASTORE);

        _debug(append, "loadArrayForBefore()");

        invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
        pop();
        _debug(append, "invokeVirtual()");
    }

    @Override
    protected void onMethodExit(int opcode) {
        _debug(new StringBuilder(), "debug:onMethodExit()");
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        // 标志 try 结束
        mv.visitLabel(to);

        // 标志 catch 块开始
        mv.visitLabel(target);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});

        // 异常信息保存到局部变量
        int local = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, local);

        // 抛出异常
        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        _debug(new StringBuilder(), "debug:visitMethodInsn()");
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInsn(int opcode) {
        _debug(new StringBuilder(), "debug:visitInsn()");
        super.visitInsn(opcode);
    }

    @Override
    public void visitParameter(String name, int access) {
        System.out.println("visitParameter name = " + name + "access = " + access);
        super.visitParameter(name, access);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        _debug(new StringBuilder(), "debug:visitLineNumber()");
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitEnd() {
        _debug(new StringBuilder(), "debug:visitEnd()");
        super.visitEnd();
    }

    // ==============================

    private void _debug(final StringBuilder append, final String msg) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (StringUtils.isBlank(append.toString())) {
            mv.visitLdcInsn(append.append(msg).toString());
        } else {
            mv.visitLdcInsn(append.append(" >> ").append(msg).toString());
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
