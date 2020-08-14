package com.yinglishzhi.advice;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
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
    // -- Lebel for try...catch block
    private final Label beginLabel = new Label();
    private final Label endLabel = new Label();
    private final Label from = new Label();
    private final Label to = new Label();
    private final Label target = new Label();

    @Override
    protected void onMethodEnter() {
        // 标志 try 开始的地方
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, ASM_TYPE_EXCEPTION.getInternalName());
        mv.visitLabel(l0);

        final StringBuilder append = new StringBuilder();
        // debug method enter
//        _debug(append, "debug:onMethodEnter()");
        generateHeckInit(mv);

        // 加载 before 方法
        getStatic(ASM_TYPE_HECK, "TEST_METHOD", ASM_TYPE_METHOD);
//        _debug(append, "loadAdviceMethod()");

        // 反射调用 黑客类方法
        loadArrayForBefore("comm");
//        _debug(append, "loadArrayForBefore()");

        // 调用方法
        invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
        pop();
//        _debug(append, "invokeVirtual()");
        mv.visitLabel(l1);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASM_TYPE_EXCEPTION.getInternalName()});
        mv.visitVarInsn(ASTORE, 1);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, ASM_TYPE_EXCEPTION.getInternalName(), PRINT_STACK_TRACE, generateDescriptor(void.class), false);
        mv.visitLabel(l4);
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
        if (!isThrow(opcode)) {

            _debug(new StringBuilder(), "debug:onMethodExit()");
            // 标志 try 开始的地方
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, ASM_TYPE_EXCEPTION.getInternalName());
            mv.visitLabel(l0);

            final StringBuilder append = new StringBuilder();
            // debug method enter
            _debug(new StringBuilder(), "debug:onMethodExit()");
            generateHeckInit(mv);

            // 加载 before 方法
            getStatic(ASM_TYPE_HECK, "TEST_METHOD", ASM_TYPE_METHOD);
            _debug(new StringBuilder(), "debug:onMethodExit()");
            // 反射调用 黑客类方法
            loadArrayForBefore("comm");
            _debug(new StringBuilder(), "debug:onMethodExit()");
            // 调用方法
            invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
            pop();
            _debug(new StringBuilder(), "debug:onMethodExit()");
            mv.visitLabel(l1);
            Label l4 = new Label();
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASM_TYPE_EXCEPTION.getInternalName()});
            mv.visitVarInsn(ASTORE, 1);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, ASM_TYPE_EXCEPTION.getInternalName(), PRINT_STACK_TRACE, generateDescriptor(void.class), false);
            mv.visitLabel(l4);
        }
    }

    /**
     * 加载异常
     */
    private void loadThrow() {
        dup();
    }

    /**
     * 将NULL推入堆栈
     */
    private void pushNull() {
        push((Type) null);
    }

    /**
     * 是否抛出异常返回(通过字节码判断)
     *
     * @param opcode 操作码
     * @return true:以抛异常形式返回 / false:非抛异常形式返回(return)
     */
    private boolean isThrow(int opcode) {
        return opcode == ATHROW;
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        final StringBuilder append = new StringBuilder();
        _debug(append, "debug:catchException()");

        // 加载异常
        loadThrow();
        _debug(append, "loadAdviceMethod()");

        // 加载throwing方法
        getStatic(ASM_TYPE_HECK, "TEST_METHOD", ASM_TYPE_METHOD);
        _debug(append, "loadAdviceMethod()");

        // 推入Method.invoke()的第一个参数
        pushNull();

        // 加载throw通知参数数组
        loadArrayForBefore("comm");
        _debug(append, "loadThrowArgs()");

        // 调用方法
        invokeVirtual(ASM_TYPE_METHOD, ASM_METHOD_METHOD_INVOKE);
        pop();
        _debug(append, "invokeVirtual()");

        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//        _debug(new StringBuilder(), "debug:visitMethodInsn()" + opcode);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInsn(int opcode) {
//        _debug(new StringBuilder(), "debug:visitInsn()");
        super.visitInsn(opcode);
    }

    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
//        _debug(new StringBuilder(), "debug:visitLineNumber()");
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitEnd() {
//        _debug(new StringBuilder(), "debug:visitEnd()");
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
