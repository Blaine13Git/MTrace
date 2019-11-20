package com.ggj.tester;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdapterInjectClass extends MethodVisitor implements Opcodes {
    private String traceMethod;

    public MethodAdapterInjectClass(final MethodVisitor mv, final String traceMethod) {
        super(ASM7, mv);
        this.traceMethod = traceMethod;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (!ClassTransformer.filterBySelf(owner) && !name.equals("<init>") && !name.equals("clinit") && traceMethod.equals("true")) {

            String fileName = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/injectTrace.log";

            //call
            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
            mv.visitLdcInsn(fileName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingCall", "(Ljava/lang/String;)V", false);

            // 方法调用
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

            //return
//            mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "getInstance", "()Lcom/ggj/tester/ClassOfInject;", false);
//            mv.visitLdcInsn(fileName);
//            mv.visitMethodInsn(INVOKEVIRTUAL, "com/ggj/tester/ClassOfInject", "linkTrackingReturn", "(Ljava/lang/String;)V", false);
        } else {
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
