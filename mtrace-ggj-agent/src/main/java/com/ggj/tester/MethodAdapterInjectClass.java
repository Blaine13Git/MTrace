package com.ggj.tester;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapterInjectClass extends MethodVisitor implements Opcodes {
    private String traceMethod;
    String fileName = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/inject_Trace.log";

    public MethodAdapterInjectClass(final MethodVisitor mv, final String traceMethod) {
        super(ASM7, mv);
        this.traceMethod = traceMethod;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
        mv.visitLdcInsn(fileName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingCall", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
            mv.visitLdcInsn(fileName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingReturn", "(Ljava/lang/String;)V", false);
        }
        mv.visitInsn(opcode);
    }
}
