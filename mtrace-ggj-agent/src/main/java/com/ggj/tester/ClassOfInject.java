package com.ggj.tester;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassOfInject {
    private String fileName;
    private static ClassOfInject classOfInject;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static ClassOfInject getInstance() {
        if (classOfInject == null) {
            return classOfInject = new ClassOfInject();
        } else {
            return classOfInject;
        }
    }

    /**
     * @author 慕一
     */
    public void linkTrackingCall(String filePath) {
        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();
        String className = stackTrace[2].getClassName();
        String methodName = stackTrace[2].getMethodName();

        long id = thread.getId();

        String now = simpleDateFormat.format(new Date().getTime());
        String today = now.substring(0, now.lastIndexOf(" "));

        String userDir = System.getProperty("user.dir");
        String projectName = userDir.substring(userDir.lastIndexOf("/") + 1);

        if (filePath == null || filePath.length() == 0) {
            fileName = today + "_" + projectName + "_Trace.log";
        } else {
            fileName = filePath + "/" + today + "_" + projectName + "_Trace.log";
        }
        writeContent(fileName, now + ", ThreadId=" + id + ", Call=" + className + "." + methodName);
    }

    /**
     * @author 慕一
     */
    public void linkTrackingReturn(String filePath) {
        String now = simpleDateFormat.format(new Date().getTime());

        Thread thread = Thread.currentThread();
        long id = thread.getId();

        StackTraceElement[] stackTrace = thread.getStackTrace();
        String className = stackTrace[2].getClassName();
        String methodName = stackTrace[2].getMethodName();
        writeContent(fileName, now + ", ThreadId=" + id + ", Return=" + className + "." + methodName);
    }

    /**
     * 向文件中写入内容
     *
     * @param fileName
     * @param content
     * @author 慕一
     */
    private static void writeContent(String fileName, String content) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(content + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("文件写入失败！");
            e.printStackTrace();
        }
    }
}
