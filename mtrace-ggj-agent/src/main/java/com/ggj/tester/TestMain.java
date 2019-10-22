package com.ggj.tester;

public class TestMain {
    private static String AGENT_PREFIX;

    //
    public static void main(String[] args) {

        String projectDir = System.getProperty("user.dir");
        System.out.println("projectDir:" + projectDir);

        String projectName = projectDir.substring(projectDir.lastIndexOf("/") + 1);
        System.out.println("projectName:" + projectName);

        System.out.println("packageName:" + TestMain.class.getPackage().getName());

        System.out.println("path:" + TestMain.class.getResource("").getPath());

        final String name = TestMain.class.getName();
        System.out.println(name);
        AGENT_PREFIX = toVMName(name.substring(0, name.lastIndexOf('.')));
        System.out.println(AGENT_PREFIX);

    }

    //
    private static String toVMName(final String srcName) {
        return srcName.replace('.', '/');
    }

}
