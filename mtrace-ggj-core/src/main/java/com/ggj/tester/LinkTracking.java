package com.ggj.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author muyi
 */
public class LinkTracking {

    public static void main(String[] args) {
        String fileName = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/2019-11-04_trade-service-consign-test_Trace.log";
        String methodName = "getDeliverTimeConfig";

        HashMap<String, String> methodLinkTrace = getMethodLinkTrace(fileName, methodName);
        Iterator<Map.Entry<String, String>> iterator = methodLinkTrace.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey());
            System.out.println(next.getValue().replace(">>>", "\n"));
        }

//        HashMap<String, String> traceData = getTraceData(fileName);
//        Iterator<Map.Entry<String, String>> iterator = traceData.entrySet().iterator();
//        while (iterator.hasNext()){
//            Map.Entry<String, String> next = iterator.next();
//            System.out.println(next.getValue().replace(">>>","\n"));
//        }


//        File file = new File("/Users/changfeng/work/code/MTrace/out");
//        lookFile(file);

    }

    //清洗线程
    private static HashMap<String, String> getTraceData(String originalData_file) {
        HashMap<String, String> traceMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(originalData_file)))) {
            String linkTraceData = bufferedReader.readLine();
            while (linkTraceData != null) {
                if (!linkTraceData.startsWith("Class-Load")) {
                    // 根据线程Id进行第一次数据清洗
                    String[] splitTraceData = linkTraceData.split(", ");
                    if (traceMap.get(splitTraceData[1]) == null) {
                        traceMap.put(splitTraceData[1], linkTraceData);
                    } else {
                        traceMap.put(splitTraceData[1], traceMap.get(splitTraceData[1]) + ">>>" + linkTraceData);
                    }
                }
                linkTraceData = bufferedReader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return traceMap;
    }

    //清洗方法
    private static HashMap<String, String> getMethodLinkTrace(String fileName, String methodName) {
        HashMap<String, String> traceData = getTraceData(fileName);
        Iterator<Map.Entry<String, String>> iterator = traceData.entrySet().iterator();
        HashMap<String, String> methodLinkTrace = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            StringBuilder value = new StringBuilder();
            String[] threadTraceData = next.getValue().split(">>>");

            String linkTraceEndSubString = null;
            int start = 0;
            for (int i = 0; i < threadTraceData.length; i++) {
                if (threadTraceData[i].endsWith(methodName)) {
                    start = i;
                    if (value == null || value.length() == 0) {
                        value.append(threadTraceData[i]);
                    } else {
                        value.append(">>>");
                        value.append(threadTraceData[i]);
                    }
                    String[] traceDataSplit = threadTraceData[i].split(", ");
                    linkTraceEndSubString = traceDataSplit[1] + ", " + traceDataSplit[2].replace("call", "return");
                }

                if (linkTraceEndSubString != null && !value.toString().endsWith(linkTraceEndSubString)) {
                    for (int j = start + 1; j < threadTraceData.length; j++) {
                        if (threadTraceData[j].endsWith(linkTraceEndSubString)) {
                            value.append(">>>");
                            value.append(threadTraceData[j]);
                            i = j;
                            break;
                        } else {
                            value.append(">>>");
                            value.append(threadTraceData[j]);
                        }
                    }
                }
            }

            methodLinkTrace.put(key, value.toString());
        }
        return methodLinkTrace;
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
