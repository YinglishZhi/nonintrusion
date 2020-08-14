package com.yinglishzhi;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.Collection;

/**
 * method visitor
 *
 * @author LDZ
 * @date 2019-10-28 15:02
 */
public class CustomAdviceAdapter extends AdviceAdapter {

    CustomAdviceAdapter(final int api, MethodVisitor methodVisitor, int access, String name, String desc) {
        super(api, methodVisitor, access, name, desc);
    }

    private final Type ASM_TYPE_SPY = Type.getType("Lcom/yinglishzhi/Heck;");
    private final Type ASM_TYPE_METHOD = Type.getType(java.lang.reflect.Method.class);
    private final Type ASM_TYPE_STRING = Type.getType(String.class);
    private final Method ASM_METHOD_METHOD_INVOKE = Method.getMethod("Object invoke(Object,Object[])");
    private final Type ASM_TYPE_THROWABLE = Type.getType(Throwable.class);


    /**
     * 入口
     */
    @Override
    protected void onMethodEnter() {
        _mark(String.format("debug:onMethodEnter"));
    }

    /**
     * 出口
     *
     * @param opcode 字节码
     * @see org.objectweb.asm.Opcodes
     */
    @Override
    protected void onMethodExit(int opcode) {

        System.out.println(asmLocalVariableArrayList);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        mv.visitLdcInsn("biubiubiu");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        mv.visitVarInsn(ILOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


        // 不是异常
        if (opcode != ATHROW) {
            _mark(String.format("debug:onMethodExit() + opcode is %d", opcode));
        }
    }

    /**
     * TryCatch块,用于ExceptionsTable重排序
     */
    class AsmLocalVariable {

        String name;
        String desc;
        String signature;
        Label start;
        Label end;
        int index;

        public AsmLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.start = start;
            this.end = end;
            this.index = index;
        }
    }

    // 用于try-catch的冲排序,目的是让tracing的try...catch能在exceptions tables排在前边
    private static final Collection<AsmLocalVariable> asmLocalVariableArrayList = new ArrayList<>();

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        asmLocalVariableArrayList.add(new AsmLocalVariable(name, desc, signature, start, end, index));
        _mark(String.format("visitLocalVariable name is %s, desc is %s, signature is %s, start is %s, end is %s, index is %d", name, desc, signature, start, end, index));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        System.out.println(asmLocalVariableArrayList);
        //标志：catch块开始位置
        for (AsmTryCatchBlock tcb : asmTryCatchBlocks) {
            System.out.println(tcb);
        }
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        // 这个方法能切到所有方法
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        _mark(String.format("visitMethodInsn opcode is %d, owner is %s, name is %s, desc is %s", opcode, owner, name, desc));
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitParameter(String name, int access) {
        System.out.println("visitParameter name = " + name + "access = " + access);
        super.visitParameter(name, access);
    }

    @Override
    public void visitEnd() {
        _mark(String.format("visitEnd"));
        super.visitEnd();
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        System.out.println(line + "--> " + start);
        super.visitLineNumber(line, start);
    }

    // 用于try-catch的冲排序,目的是让tracing的try...catch能在exceptions tables排在前边
    private static final Collection<AsmTryCatchBlock> asmTryCatchBlocks = new ArrayList<>();

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        _mark(String.format("visitTryCatchBlock start is %s, end is %s, handler is %s, type is %s", start, end, handler, type));
        asmTryCatchBlocks.add(new AsmTryCatchBlock(start, end, handler, type));
    }

// ==============================

    /**
     * TryCatch块,用于ExceptionsTable重排序
     */
    class AsmTryCatchBlock {

        protected final Label start;
        protected final Label end;
        protected final Label handler;
        protected final String type;

        AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this.type = type;
        }

    }

    private void _mark(final String msg) {
        System.out.println(msg);
        _debug(new StringBuilder(), msg);
    }

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
