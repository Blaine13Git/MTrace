package com.ggj.tester;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import jdk.internal.instrumentation.Logger;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ListIterator;

public class ClassTransformer implements ClassFileTransformer {
    private static final String AGENT_PREFIX;
    private final WildcardMatcher includes;
    private final WildcardMatcher excludes;
    private final WildcardMatcher exclClassloader;
    private final boolean inclBootstrapClasses;
    private final boolean inclNoLocationClasses;
    private Logger log = new TraceLogger();

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

    public int traceID = 0;

    @Override
    public byte[] transform(
            ClassLoader loader, //类加载器
            String className, //类名 xx/xx/xx
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, //保护域
            byte[] classfileBuffer //原字节码
    ) throws IllegalClassFormatException {
        if (!filter(loader, className, protectionDomain)) return null;
        long TraceId = 11111;
        try {
            // 重定向标准输出和错误输出到文件
            String traceFile = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/trace.log";
            FileOutputStream fileOutputStream = new FileOutputStream(traceFile, true);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setOut(printStream);
            System.setErr(printStream);

            //ASM-Core API
            if (1 == 11) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                ClassVisitor cv = new ClassAdapter(cw);
                cr.accept(cv, 0);
                return cw.toByteArray();
            }

            //ASM-Tree API
            if (2 == 2) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                for (MethodNode md : cn.methods) {
                    if ("<init>".endsWith(md.name) || "<clinit>".equals(md.name)) continue;
                    InsnList insnList = md.instructions;
                    InsnList insertAtHead = new InsnList();
                    insertAtHead.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                    insertAtHead.add(new LdcInsnNode(TraceId + "--Call Method-> " + cn.name + "." + md.name));
                    insertAtHead.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
                    insnList.insert(insertAtHead);//在调用方法的头部插入埋点
                    md.maxStack += 3;

                    InsnList endData = new InsnList();
                    endData.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                    endData.add(new LdcInsnNode(TraceId + "--Return Method-> " + cn.name + "." + md.name));
                    endData.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
                    insnList.insert(insnList.getLast().getPrevious(), endData);//在调用方法的最后插入埋点
                    md.maxStack += 3;
                }

                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                return cw.toByteArray();
            }

            //使用ASM-Tree API, opcode的值进行精准插入
            if (3 == 33) {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);
                for (MethodNode mn : cn.methods) {
                    if ("<init>".endsWith(mn.name) || "<clinit>".equals(mn.name)) continue;
                    InsnList instructions = mn.instructions;
                    ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode next = iterator.next();
                        int opcode = next.getOpcode();
                        if (opcode == Opcodes.RETURN || opcode == Opcodes.ATHROW) {
                            //insert code here by yourself，通过javap查看字节码
                        }
                    }
                }

                ClassWriter cw = new ClassWriter(0);
                cn.accept(cw);
                return cw.toByteArray();
            }

            //BCEL API
            // InstructionList instructionList = new InstructionList();
            // instructionList.append();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;
    }

    boolean filter(final ClassLoader loader, final String classname, final ProtectionDomain protectionDomain) {
        if (loader == null) {
            if (!inclBootstrapClasses) return false;
        } else {
            if (!inclNoLocationClasses && !hasSourceLocation(protectionDomain)) return false;
            if (exclClassloader.matches(loader.getClass().getName())) return false;
        }
        return !classname.startsWith(AGENT_PREFIX) && includes.matches(classname) && !excludes.matches(classname);
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



