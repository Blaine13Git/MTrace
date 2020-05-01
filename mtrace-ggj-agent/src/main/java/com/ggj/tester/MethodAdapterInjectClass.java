package com.ggj.tester;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapterInjectClass extends MethodVisitor implements Opcodes {
    private String filePath;

    public MethodAdapterInjectClass(final MethodVisitor mv, final String filePath) {
        super(ASM7, mv);
        this.filePath = filePath;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<MT-MethodAdapter>>>>>>>>>>>>>>>>>>>>>>");



        if (!ClassTransformer.filterBySelf(owner) && !name.equals("<init>") && !name.equals("clinit") ) {
            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInjectDirect", "getInstance", "()Lcom/ggj/tester/ClassOfInjectDirect;", false);
            mv.visitLdcInsn(filePath);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInjectDirect", "linkTrackingCall", "(Ljava/lang/String;)V", false);

            // 方法调用
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInjectDirect", "getInstance", "()Lcom/ggj/tester/ClassOfInjectDirect;", false);
            mv.visitLdcInsn(filePath);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInjectDirect", "linkTrackingReturn", "(Ljava/lang/String;)V", false);

        } else {
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
