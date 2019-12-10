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
    private String filePath;

    public ClassAdapter(ClassVisitor cv, String filePath) {
        super(ASM7, cv);
        this.filePath = filePath;
        System.out.println("<<<<<<<<<<<<<<<<<<<<<MT-Call-ClassAdapter>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (!isInterface && mv != null && !name.equals("<init>") && !name.equals("<clinit>")) {

            System.out.println("<<<<<<<<<<<<<<<<<<<<<MT-ClassAdapter>>>>>>>>>>>>>>>>>>>>>>");

            mv = new MethodAdapterInjectClass(mv, filePath);
        }
        return mv;
    }

}
