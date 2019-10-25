package com.ggj.tester;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassTransformer implements ClassFileTransformer {
    private static final String AGENT_PREFIX;
    private final WildcardMatcher includes;
    private final WildcardMatcher excludes;
    private final WildcardMatcher exclClassloader;
    private final boolean inclBootstrapClasses;
    private final boolean inclNoLocationClasses;

    static {
        final String name = ClassTransformer.class.getName();
        AGENT_PREFIX = toVMName(name.substring(0, name.lastIndexOf('.')));
    }

    public ClassTransformer(final AgentOptions options) {
        includes = new WildcardMatcher(toVMName(options.getIncludes()));
        excludes = new WildcardMatcher(toVMName(options.getExcludes()));
        exclClassloader = new WildcardMatcher(options.getExclClassloader());
        inclBootstrapClasses = options.getInclBootstrapClasses();
        inclNoLocationClasses = options.getInclNoLocationClasses();
    }

    @Override
    public byte[] transform(
            ClassLoader loader, //类加载器
            String className, //类名 xx/xx/xx
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, //保护域
            byte[] classfileBuffer //原字节码
    ) throws IllegalClassFormatException {

        if (classBeingRedefined != null) {
            return null;
        }

        // 重定向输出到指定文件
        String traceFile = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/trace.log";
        redirectOutPut(traceFile);

        // 基本过滤
        if (!filter(loader, className, protectionDomain)) return null;

        //过滤内容
        ArrayList<String> filterData = new ArrayList();
        filterData.add("com/intellij/");
        filterData.add("com/beust/");
        filterData.add("com/alibaba/");
        filterData.add("com/aliyun/");
        filterData.add("com/mysql/");
        filterData.add("com/google/");
        filterData.add("com/fasterxml/");
        filterData.add("com/sun/");
        filterData.add("com/github/");
        filterData.add("com/zaxxer/");
        filterData.add("com/ggj/platform/");
        filterData.add("sun/");
        filterData.add("org/");
        filterData.add("ch/");
        filterData.add("javassist/");
        filterData.add("io/");
        filterData.add("springfox/");
        filterData.add("redis/");
        filterData.add("javax/");
        filterData.add("au/");
        filterData.add("com/ggj/qa/gts/");
        filterData.add("java/");
        filterData.add("rx/");
        filterData.add("net/");

        for (int i = 0; i < filterData.size(); i++) {
            if (className.startsWith(filterData.get(i)) || className.contains("$$")) {
                return null;
            }
        }

        // 调用字节码插入
        return callAsmCoreApi(classfileBuffer);
//        return callASMTreeApi(classfileBuffer);

    }

    /**
     * 调用 ASM Core API
     *
     * @param classfileBuffer
     * @return
     */
    private byte[] callAsmCoreApi(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new ClassAdapter(cw);
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    /**
     * 调用 ASM-Tree API
     *
     * @param classfileBuffer
     * @return
     */
    public byte[] callASMTreeApi(byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassNode cn = new ClassNode(ASM7);
        cr.accept(cn, 0);

        for (MethodNode mn : cn.methods) {

            //过滤类的初始化方法
            if ("<init>".endsWith(mn.name) || "<clinit>".endsWith(mn.name)) continue;

            InsnList insnList = mn.instructions;

            if (insnList.size() == 0) continue; //用来过滤抽象方法

            InsnList insertAtHead = new InsnList();
            insertAtHead.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;"));
            insertAtHead.add(new LdcInsnNode("Call Method-> " + cn.name + "." + mn.name));
            insertAtHead.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
            insnList.insert(insertAtHead);//在调用方法的头部插入埋点
//            mn.maxStack += 3;

            InsnList endData = new InsnList();
            endData.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;"));
            endData.add(new LdcInsnNode("Return Method-> " + cn.name + "." + mn.name));
            endData.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
            insnList.insert(insnList.getLast().getPrevious(), endData);//在调用方法的最后插入埋点
            mn.maxStack += 6;
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
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

    boolean filter(final ClassLoader loader, final String className, final ProtectionDomain protectionDomain) {
        if (loader == null) {
            if (!inclBootstrapClasses) return false;
        } else {
            if (!inclNoLocationClasses && !hasSourceLocation(protectionDomain)) return false;
            if (exclClassloader.matches(loader.getClass().getName())) return false;
        }
        return !className.startsWith(AGENT_PREFIX) && includes.matches(className) && !excludes.matches(className);
    }

    private boolean hasSourceLocation(final ProtectionDomain protectionDomain) {
        if (protectionDomain == null) return false;

        final CodeSource codeSource = protectionDomain.getCodeSource();
        if (codeSource == null) return false;
        return codeSource.getLocation() != null;
    }

    private static String toVMName(final String srcName) {
        return srcName.replace('.', '/');
    }

}
