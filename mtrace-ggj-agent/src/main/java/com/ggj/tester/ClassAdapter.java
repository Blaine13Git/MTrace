package com.ggj.tester;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 1、检查类是否为接口
 * 2、过滤需要注入的方法
 */
public class ClassAdapter extends ClassVisitor implements Opcodes {
    private boolean isInterface;
    private String className;
    private String methodName;

    public ClassAdapter(final ClassVisitor cv) {
        super(ASM7, cv);
    }

    @Override
    public void visit(
            int version,
            int access,
            String name,
            String signature,
            String superName,
            String[] interfaces
    ) {
        cv.visit(version, access, name, signature, superName, interfaces);
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        className = name;
        if (!isInterface) {
            System.err.println("className:" + className);
        }
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String desc,
            final String signature,
            final String[] exceptions
    ) {
        methodName = name;
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!isInterface && mv != null && !name.equals("<init>") && !name.equals("<clinit>")) {
//            System.err.println("***" + className + "." + methodName);
            mv = new MethodAdapter(mv);
        }
        return mv;
    }

}
