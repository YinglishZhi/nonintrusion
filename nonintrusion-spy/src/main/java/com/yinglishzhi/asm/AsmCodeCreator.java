package com.yinglishzhi.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static com.yinglishzhi.commons.Constant.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * 字节码生成
 *
 * @author LDZ
 * @date 2020/8/12 11:01 上午
 */
public class AsmCodeCreator {

//    private static final Label l0 = new Label();
//    private static final Label l1 = new Label();
//    private static final Label l2 = new Label();
//
//    public static void generateTryCatchBegin(MethodVisitor mv) {
//        mv.visitTryCatchBlock(l0, l1, l2, ASM_TYPE_EXCEPTION.getInternalName());
//        mv.visitLabel(l0);
//    }
//
//
//    public static void generateTryCatchEnd(MethodVisitor mv) {
//        try {
//            mv.visitLabel(l0);
//            Label l4 = new Label();
//            mv.visitJumpInsn(GOTO, l4);
//            mv.visitLabel(l1);
//            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASM_TYPE_NO_SUCH_METHOD_EXCEPTION.getInternalName()});
//            mv.visitVarInsn(ASTORE, 1);
//            Label l5 = new Label();
//            mv.visitLabel(l5);
//            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn(INVOKEVIRTUAL, ASM_TYPE_NO_SUCH_METHOD_EXCEPTION.getInternalName(), PRINT_STACK_TRACE, generateDescriptor(void.class), false);
//            mv.visitLabel(l4);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//    }


    /**
     * 生成 Heck init 代码
     *
     * @param mv method visitor
     */
    public static void generateHeckInit(MethodVisitor mv) {
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, ASM_TYPE_NO_SUCH_METHOD_EXCEPTION.getInternalName());
        mv.visitLabel(l0);

        mv.visitLdcInsn(ASM_TYPE_MY_INVADE);
        mv.visitLdcInsn(INVADE_METHOD_NAME);
        mv.visitInsn(ICONST_1);
        mv.visitTypeInsn(ANEWARRAY, ASM_TYPE_CLASS.getInternalName());
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitLdcInsn(ASM_TYPE_STRING);
        mv.visitInsn(AASTORE);
        mv.visitMethodInsn(INVOKEVIRTUAL, ASM_TYPE_CLASS.getInternalName(), GET_METHOD, generateDescriptor(Method.class, String.class, Class[].class), false);
        mv.visitVarInsn(ASTORE, 1);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, ASM_TYPE_HECK.getInternalName(), INIT, generateDescriptor(void.class, Method.class), false);

        mv.visitLabel(l1);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{ASM_TYPE_NO_SUCH_METHOD_EXCEPTION.getInternalName()});
        mv.visitVarInsn(ASTORE, 1);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, ASM_TYPE_NO_SUCH_METHOD_EXCEPTION.getInternalName(), PRINT_STACK_TRACE, generateDescriptor(void.class), false);
        mv.visitLabel(l4);
    }
}
