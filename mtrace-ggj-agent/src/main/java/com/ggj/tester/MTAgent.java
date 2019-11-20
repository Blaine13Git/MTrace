package com.ggj.tester;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.text.SimpleDateFormat;

/**
 * @author muyi
 */
public class MTAgent {
    private static String traceFilePath;
    private static String debug;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void premain(final String options, final Instrumentation instrumentation) {
        final AgentOptions agentOptions = new AgentOptions(options);
//        debug = agentOptions.getDebug();
//        traceFilePath = agentOptions.getTraceFilePath();
//        Date date = new Date();
//        String formatDate = dateFormat.format(date);
//
//        String projectPath = System.getProperty("user.dir");
//        String projectName = projectPath.substring(projectPath.lastIndexOf("/") + 1);
//
//        if (null == traceFilePath || traceFilePath.length() == 0) {
//            String traceFile = projectPath + "/" + formatDate + "_" + projectName + "_Trace.log";
//            redirectOutPut(traceFile);
//        } else {
//            String traceFile = traceFilePath + "/" + formatDate + "_" + projectName + "_Trace.log";
//            redirectOutPut(traceFile);
//        }

//        instrumentation.

        //向instrumentation中添加一个类的转换器,用于转换类的行为.
        instrumentation.addTransformer(new ClassTransformer(agentOptions));

    }

    /**
     * 重定向输出
     *
     * @param filePath
     */
    public static void redirectOutPut(String filePath) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, true);
            PrintStream printStream = new PrintStream(fileOutputStream);
            System.setErr(printStream);
            if (debug.equals("true")) {
//                System.setOut(printStream);
                System.err.println("begin……");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

