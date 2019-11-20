package com.ggj.tester;

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
        //向instrumentation中添加一个类的转换器,用于转换类的行为.
        instrumentation.addTransformer(new ClassTransformer(agentOptions));

    }

}

