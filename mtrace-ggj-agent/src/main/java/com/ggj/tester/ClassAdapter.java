package com.ggj.tester;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * 1、检查类是否为接口
 * 2、过滤需要注入的方法
 */
public class ClassAdapter extends ClassVisitor implements Opcodes {

    protected boolean isInterface;
    protected String className;
    protected String methodName;
    private String traceClass;
    private String traceMethod;

    public ClassAdapter(final ClassVisitor cv, String traceClass, String traceMethod) {
        super(ASM7, cv);
        this.traceClass = traceClass;
        this.traceMethod = traceMethod;
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
        className = name;

        // 重定向输出到指定文件
        String traceFile = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/Trace.log";
        redirectOutPut(traceFile);

        if (!isInterface && traceClass.equals("true")) {
            System.err.println("\nClass-Load:" + className);
        }
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
            mv = new MethodAdapter(mv, traceMethod);
        }
        return mv;
    }

    /**
     * 重定向输出
     *
     * @param filePath
     */
    public void redirectOutPut(String filePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
            PrintStream printStream = new PrintStream(fileOutputStream);
//            System.setOut(printStream);
            System.setErr(printStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
