package com.yinglishzhi.advice;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import static com.yinglishzhi.asm.AsmCodeCreator.generateHeckInit;
import static com.yinglishzhi.commons.Constant.*;


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
     * 反射方法
     */
    private static final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");

    /**
     * Label for try...catch block
     */
    private final Label from = new Label();
    private final Label to = new Label();
    private final Label target = new Label();

    @Override
    protected void onMethodEnter() {
        // 标志 try 开始的地方
        visitLabel(from);
        visitTryCatchBlock(from, to, target, ASM_TYPE_EXCEPTION.getInternalName());

        final StringBuilder append = new StringBuilder();
        // debug method enter
        _debug(append, "debug:onMethodEnter()");
        generateHeckInit(mv);

        // 加载 before 方法
        getStatic(ASM_TYPE_HECK, "TEST_METHOD", ASM_TYPE_METHOD);
        _debug(append, "loadAdviceMethod()");

        // 反射调用 黑客类方法
        loadArrayForBefore("comm");
        _debug(append, "loadArrayForBefore()");

        // 调用方法
        invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
        pop();
        _debug(append, "invokeVirtual()");
    }

    /**
     * 加载 before 通知参数数组
     *
     * @param string 参数
     */
    private void loadArrayForBefore(String string) {
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ACONST_NULL);

        push(1);
        newArray(ASM_TYPE_OBJECT);

        dup();
        push(0);
        push(string);
        arrayStore(ASM_TYPE_STRING);

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
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASM_TYPE_EXCEPTION.getInternalName()});

        // 异常信息保存到局部变量
        int local = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, local);

        // 抛出异常
        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        _debug(new StringBuilder(), "debug:visitTryCatchBlock()");
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void catchException(Label start, Label end, Type exception) {
        _debug(new StringBuilder(), "debug:catchException()");
        super.catchException(start, end, exception);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        _debug(new StringBuilder(), "debug:visitMethodInsn()" + opcode);
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
