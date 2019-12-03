package com.ggj.tester;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapter extends MethodVisitor implements Opcodes {
    private String traceMethod;

    public MethodAdapter(final MethodVisitor mv, final String traceMethod) {
        super(ASM7, mv);
        this.traceMethod = traceMethod;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!ClassTransformer.filterBySelf(owner) && !name.equals("<init>") && !name.equals("clinit") && traceMethod.equals("true")) {

            // System.err.println("thread id = " + Thread.currentThread().getId() + ",call method = " + owner + "." + name);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("call=" + owner + "." + name);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 方法调用
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            System.out.println("return=" + owner + "." + name);

        } else {
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
