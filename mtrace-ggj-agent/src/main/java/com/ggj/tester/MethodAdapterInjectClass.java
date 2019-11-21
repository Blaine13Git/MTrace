package com.ggj.tester;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapterInjectClass extends MethodVisitor implements Opcodes {

    public MethodAdapterInjectClass(final MethodVisitor mv) {
        super(ASM7, mv);
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingCall", "()V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingReturn", "()V", false);
        }
        mv.visitInsn(opcode);
    }
}
