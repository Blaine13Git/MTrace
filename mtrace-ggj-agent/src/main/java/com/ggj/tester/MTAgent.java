package com.ggj.tester;

import java.lang.instrument.Instrumentation;

/**
 * @author muyi
 */
public class MTAgent {

    //初始化jvm agent
    public static void premain(final String options, final Instrumentation instrumentation) {

        System.out.println("I'm MT start >>>");

        final AgentOptions agentOptions = new AgentOptions(options);

        //向instrumentation中添加一个类的转换器,用于转换类的行为.
        instrumentation.addTransformer(new ClassTransformer(agentOptions));

        System.out.println("I'm MT end >>>");
    }
}

