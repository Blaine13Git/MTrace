package com.ggj.tester;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class InjectClassloader extends ClassLoader {

    public Class defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.endsWith("ClassOfInject")) {
            ClassWriter cw = new ClassWriter(0);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;

            cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/ggj/tester/ClassOfInject", null, "java/lang/Object", null);

            {
                fv = cw.visitField(ACC_PRIVATE, "simpleDateFormat", "Ljava/text/SimpleDateFormat;", null, null);
                fv.visitEnd();
            }

            {
                fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "classOfInject", "Lcom/ggj/tester/ClassOfInject;", null, null);
                fv.visitEnd();
            }

            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/text/SimpleDateFormat");
                mv.visitInsn(DUP);
                mv.visitLdcInsn("yyyy-MM-dd HH:mm:ss.SSS");
                mv.visitMethodInsn(INVOKESPECIAL, "java/text/SimpleDateFormat", "<init>", "(Ljava/lang/String;)V", false);
                mv.visitFieldInsn(PUTFIELD, "com/ggj/tester/ClassOfInject", "simpleDateFormat", "Ljava/text/SimpleDateFormat;");
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 1);
                mv.visitEnd();
            }

            {
                mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "getInstance", "()Lcom/ggj/tester/ClassOfInject;", null, null);
                mv.visitCode();
                mv.visitFieldInsn(GETSTATIC, "com/ggj/tester/ClassOfInject", "classOfInject", "Lcom/ggj/tester/ClassOfInject;");
                Label l0 = new Label();
                mv.visitJumpInsn(IFNONNULL, l0);
                mv.visitTypeInsn(NEW, "com/ggj/tester/ClassOfInject");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "com/ggj/tester/ClassOfInject", "<init>", "()V", false);
                mv.visitInsn(DUP);
                mv.visitFieldInsn(PUTSTATIC, "com/ggj/tester/ClassOfInject", "classOfInject", "Lcom/ggj/tester/ClassOfInject;");
                mv.visitInsn(ARETURN);
                mv.visitLabel(l0);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitFieldInsn(GETSTATIC, "com/ggj/tester/ClassOfInject", "classOfInject", "Lcom/ggj/tester/ClassOfInject;");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(2, 0);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "linkTrackingCall", "(Ljava/lang/String;)V", null, null);
                mv.visitParameter("fileName", 0);
                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                mv.visitVarInsn(ASTORE, 2);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 3);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 4);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
                mv.visitVarInsn(LSTORE, 5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/ggj/tester/ClassOfInject", "simpleDateFormat", "Ljava/text/SimpleDateFormat;");
                mv.visitTypeInsn(NEW, "java/util/Date");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "()V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J", false);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/text/SimpleDateFormat", "format", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 7);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(", ThreadId=");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(LLOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(", Call=");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(".");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "writeContent", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 8);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "linkTrackingReturn", "(Ljava/lang/String;)V", null, null);
                mv.visitParameter("fileName", 0);
                mv.visitCode();
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                mv.visitVarInsn(ASTORE, 2);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 3);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 4);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
                mv.visitVarInsn(LSTORE, 5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/ggj/tester/ClassOfInject", "simpleDateFormat", "Ljava/text/SimpleDateFormat;");
                mv.visitTypeInsn(NEW, "java/util/Date");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "()V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J", false);
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/text/SimpleDateFormat", "format", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitVarInsn(ASTORE, 7);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(", ThreadId=");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(LLOAD, 5);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(", Return=");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn(".");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKESTATIC, "com/ggj/tester/ClassOfInject", "writeContent", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 8);
                mv.visitEnd();
            }

            {
                mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "writeContent", "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
                mv.visitParameter("fileName", 0);
                mv.visitParameter("content", 0);
                mv.visitCode();
                Label l0 = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
                mv.visitLabel(l0);
                mv.visitTypeInsn(NEW, "java/io/FileWriter");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKESPECIAL, "java/io/FileWriter", "<init>", "(Ljava/lang/String;Z)V", false);
                mv.visitVarInsn(ASTORE, 2);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn("\n");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/FileWriter", "write", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/FileWriter", "close", "()V", false);
                mv.visitLabel(l1);
                Label l3 = new Label();
                mv.visitJumpInsn(GOTO, l3);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
                mv.visitVarInsn(ASTORE, 2);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("\u6587\u4ef6\u5199\u5165\u5931\u8d25\uff01");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
                mv.visitLabel(l3);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 3);
                mv.visitEnd();
            }

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }
}
