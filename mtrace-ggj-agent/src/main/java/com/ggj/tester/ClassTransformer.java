package com.ggj.tester;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassTransformer implements ClassFileTransformer {
    private static final String AGENT_PREFIX;
    private final WildcardMatcher includes;
    private final WildcardMatcher excludes;
    private final WildcardMatcher exclClassloader;
    private final boolean inclBootstrapClasses;
    private final boolean inclNoLocationClasses;

    private final String traceClass;
    private final String traceMethod;

    static {
        final String name = ClassTransformer.class.getName();
        AGENT_PREFIX = toVMName(name.substring(0, name.lastIndexOf('.')));
    }

    public ClassTransformer(final AgentOptions agentOptions) {
        includes = new WildcardMatcher(toVMName(agentOptions.getIncludes()));
        excludes = new WildcardMatcher(toVMName(agentOptions.getExcludes()));
        exclClassloader = new WildcardMatcher(agentOptions.getExclClassloader());
        inclBootstrapClasses = agentOptions.getInclBootstrapClasses();
        inclNoLocationClasses = agentOptions.getInclNoLocationClasses();
        traceClass = agentOptions.getTraceClass();
        traceMethod = agentOptions.getTraceMethod();
    }

    @Override
    public byte[] transform(
            ClassLoader loader, //类加载器
            String className, //类名 xx/xx/xx
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, //保护域
            byte[] classfileBuffer //原字节码
    ) throws IllegalClassFormatException {

        // 一级过滤
        if (classBeingRedefined != null) return null;

        // 二级过滤
        if (!filter(loader, className, protectionDomain)) return null;

        // 三级过滤
        if (filterBySelf(className)) return null;

        // 注入
        return callAsmCoreApi(classfileBuffer, traceClass, traceMethod);
//        return callASMTreeApi(classfileBuffer);

    }

    /**
     * 调用 ASM Core API
     *
     * @param classfileBuffer
     * @return
     */
    private byte[] callAsmCoreApi(byte[] classfileBuffer, String traceClass, String traceMethod) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        try {
            ClassVisitor cv = new ClassAdapter(cw, traceClass, traceMethod);
            cr.accept(cv, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    boolean filter(final ClassLoader loader, final String className, final ProtectionDomain protectionDomain) {
        if (loader == null) {
            if (!inclBootstrapClasses) return false;
        } else {
            if (!inclNoLocationClasses && !hasSourceLocation(protectionDomain)) return false;
            if (exclClassloader.matches(loader.getClass().getName())) return false;
        }
        return !className.startsWith(AGENT_PREFIX) && includes.matches(className) && !excludes.matches(className);
    }

    /**
     * @param className
     * @return 返回true需要过滤
     */
    static boolean filterBySelf(String className) {

        if (className.startsWith("com/ggj/") && !className.startsWith("com/ggj/platform") && !className.startsWith("com/ggj/qa")&& !className.contains("$$")) {
            return false;
        }

//        String[] filterData = new String[28];
//        filterData[0] = "com/intellij/";
//        filterData[1] = "com/beust/";
//        filterData[2] = "com/alibaba/";
//        filterData[3] = "com/aliyun/";
//        filterData[4] = "com/mysql/";
//        filterData[5] = "com/google/";
//        filterData[6] = "com/fasterxml/";
//        filterData[7] = "com/sun/";
//        filterData[8] = "com/github/";
//        filterData[9] = "com/zaxxer/";
//        filterData[10] = "sun/";
//        filterData[11] = "org/";
//        filterData[12] = "ch/";
//        filterData[13] = "javassist/";
//        filterData[14] = "io/";
//        filterData[15] = "springfox/";
//        filterData[16] = "redis/";
//        filterData[17] = "javax/";
//        filterData[18] = "au/";
//        filterData[19] = "java/";
//        filterData[20] = "rx/";
//        filterData[21] = "net/";
//        filterData[22] = "junit/";
//        filterData[23] = "bsh/";
//        filterData[24] = "tk/";
//        filterData[25] = "lombok/";
//        filterData[26] = "lombok/";
//        filterData[27] = "com/ggj/platform/";
//        filterData[28] = "com/ggj/qa/";
//
//        for (int i = 0; i < filterData.length; i++) {
//            if (className.startsWith(filterData[i]) || className.contains("$$")) {
//                return true;
//            }
//        }

        return true;
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
