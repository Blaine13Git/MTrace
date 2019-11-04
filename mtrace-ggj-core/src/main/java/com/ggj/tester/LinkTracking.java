package com.ggj.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author muyi
 */
public class LinkTracking {

    public static void main(String[] args) {
//        String file = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/2019-11-04_trade-service-consign-test_Trace.log";
//        String methodName = "updateDeliverTimeConfig";
//        getTraceData(file, methodName);

        File file = new File("/Users/changfeng/work/code/MTrace/out");
        lookFile(file);
    }

    private static void getTraceData(String originalData_file, String methodName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(originalData_file)))) {
            String lineData = bufferedReader.readLine();
            while (lineData != null) {
                if (lineData.endsWith(methodName)) {
                    String traceLinkStart = lineData;
                    System.out.println("traceLinkStart: " + lineData);
                    String[] splitTraceStart = traceLinkStart.split(", ");
                    String traceLinkEnd;
                    String traceLinkEnd_subString = splitTraceStart[1] + splitTraceStart[2].replace("call", ", return");

                    String traceLinkMiddle = bufferedReader.readLine();
                    while (traceLinkMiddle != null) {
                        if (traceLinkMiddle.contains(traceLinkEnd_subString)) {
                            traceLinkEnd = traceLinkMiddle;
                            System.out.println("traceLinkEnd: " + traceLinkEnd);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Date startTime = dateFormat.parse(splitTraceStart[0]);
                            Date endTime = dateFormat.parse(traceLinkEnd.split(", ")[0]);
                            System.out.println("耗时: " + (endTime.getTime() - startTime.getTime()) + "ms");

                            return;
                        } else {
                            System.out.println("traceLinkMiddle: " + traceLinkMiddle);
                        }
                        traceLinkMiddle = bufferedReader.readLine();
                    }
                }
                lineData = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lookFile(File file) {
        File[] fs = file.listFiles(); //遍历filePath下的文件和目录，放在File数组中
        for (File f : fs) {
            if (f.isDirectory()) {
                lookFile(f); //递归子目录
            }
            if (!f.isDirectory() && f.getName().endsWith("_Trace.log"))//不是目录(即文件)，则打印
                System.out.println(f);
        }
    }

}
