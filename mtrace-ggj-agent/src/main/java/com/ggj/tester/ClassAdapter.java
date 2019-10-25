package com.ggj.tester;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassAdapter extends ClassVisitor implements Opcodes {
    private boolean isInterface;
    private String className;

    public ClassAdapter(final ClassVisitor cv) {
        super(ASM7, cv);
    }

    //自定义类头部检查
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        className = name;
        System.out.println("className:" + className);
    }


    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String desc,
            final String signature,
            final String[] exceptions
    ) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if (!isInterface && mv != null && !name.equals("<init>") || !name.equals("clinit")) {
            mv = new MethodAdapter(mv);
        }
        return mv;
    }
}
