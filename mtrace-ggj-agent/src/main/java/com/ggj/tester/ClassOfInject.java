package com.ggj.tester;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassOfInject {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static ClassOfInject classOfInject;

    public static ClassOfInject getInstance() {
        if (classOfInject == null) {
            return classOfInject = new ClassOfInject();
        } else {
            return classOfInject;
        }
    }

    /**
     * @param fileName
     * @author 慕一
     */
    public void linkTrackingCall(String fileName) {
        // 获取调用者的类名和方法名称
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[2].getClassName();
        String methodName = stackTrace[2].getMethodName();

        //获取调用者的父类

        long id = Thread.currentThread().getId();
        String now = simpleDateFormat.format(new Date().getTime());
        writeContent(fileName, now + ", ThreadId=" + id + ", Call=" + className + "." + methodName);
    }

    /**
     * @param fileName
     * @author 慕一
     */
    public void linkTrackingReturn( String fileName) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[2].getClassName();
        String methodName = stackTrace[2].getMethodName();

        long id = Thread.currentThread().getId();
        String now = simpleDateFormat.format(new Date().getTime());
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
