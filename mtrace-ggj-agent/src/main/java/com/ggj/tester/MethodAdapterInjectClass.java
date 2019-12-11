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
    public void visitCode() {
        System.out.println("<<<<<<<<<<<<<<<<<<<<<MT-MethodAdapterInjectClass>>>>>>>>>>>>>>>>>>>>>>");
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInjectDirect", "getInstance", "()Lcom/ggj/tester/ClassOfInjectDirect;", false);
        mv.visitLdcInsn(filePath);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInjectDirect", "linkTrackingCall", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInjectDirect", "getInstance", "()Lcom/ggj/tester/ClassOfInjectDirect;", false);
            mv.visitLdcInsn(filePath);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInjectDirect", "linkTrackingReturn", "(Ljava/lang/String;)V", false);
        }
        mv.visitInsn(opcode);
    }
}
