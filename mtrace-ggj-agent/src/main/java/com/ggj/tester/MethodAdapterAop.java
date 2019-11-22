package com.ggj.tester;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

public class MethodAdapterAop extends AdviceAdapter {
    protected MethodAdapterAop(int i, MethodVisitor methodVisitor, int i1, String s, String s1) {
        super(i, methodVisitor, i1, s, s1);
    }

    @Override
    protected void onMethodEnter() {
        System.out.println("before");
    }

    @Override
    protected void onMethodExit(int i) {

        System.out.println("after");
    }

    @Override
    public void visitMaxs(int i, int i1) {


    }
}
