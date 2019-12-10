package com.ggj.tester;

import java.lang.instrument.Instrumentation;

/**
 * @author muyi
 */
public class MTAgent {

    public static void agentmain(final String options, Instrumentation instrumentation) {
        final AgentOptions agentOptions = new AgentOptions(options);
        instrumentation.addTransformer(new ClassTransformer(agentOptions), true);

        try {
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();

            for (int i = 0; i < allLoadedClasses.length; i++) {
                instrumentation.retransformClasses(allLoadedClasses[i]);
            }

            System.out.println("Agent Load Done.");
        } catch (Exception e) {
            System.out.println("agent load failed!");
            e.printStackTrace();
        }
    }
}
