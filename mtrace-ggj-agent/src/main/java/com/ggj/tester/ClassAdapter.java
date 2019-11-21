package com.ggj.tester;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 1、检查类是否为接口
 * 2、过滤需要注入的方法
 */
public class ClassAdapter extends ClassVisitor implements Opcodes {

    protected boolean isInterface;
    protected String methodName;
    private String traceClass;


    public ClassAdapter(
            final ClassVisitor cv,
            String traceClass
    ) {
        super(ASM7, cv);
        this.traceClass = traceClass;
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

//        if (!isInterface && traceClass.equals("true")) {
//
//        }
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
        methodName = name;
        if (!isInterface && mv != null && !name.equals("<init>") && !name.equals("<clinit>")) {
            mv = new MethodAdapterInjectClass(mv);
        }
        return mv;
    }
}
