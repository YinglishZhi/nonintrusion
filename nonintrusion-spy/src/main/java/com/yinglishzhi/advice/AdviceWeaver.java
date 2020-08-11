package com.yinglishzhi.advice;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.List;


/**
 * class visitor
 *
 * @author LDZ
 * @date 2019-10-28 15:00
 */
public class AdviceWeaver extends ClassVisitor {

    private List<String> invadeMethodName;

    public AdviceWeaver(ClassVisitor cv, List<String> invadeMethodName) {
        super(Opcodes.ASM5, cv);
        this.invadeMethodName = invadeMethodName;
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        System.out.println("method visitor" + name);
        if (isIgnore(mv, access, name, desc)) {
            return mv;
        }
        return new CustomAdviceAdapter(Opcodes.ASM5, new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions), access, name, desc);
    }

    /**
     * 忽略方法
     *
     * @param mv         method visitor
     * @param access     权限
     * @param methodName 方法名称
     * @param descriptor 方法描述符  (see {@link Type}).
     * @return 是否忽略这个方法
     */
    private boolean isIgnore(MethodVisitor mv, int access, String methodName, String descriptor) {
        return null == mv
                || !invadeMethodName.contains(methodName);
    }
}
