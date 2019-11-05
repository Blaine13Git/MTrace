package com.ggj.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author muyi
 */
public class LinkTracking {

    public static void main(String[] args) {
        String methodName = "ConsignDeliverTimeAPIImpl.createDeliverTimeConfig";
        File file = new File("/Users/changfeng/work/code/MTrace/out");
        lookTargetLinkTrace(file, methodName);
    }

    //清洗线程
    private static HashMap<String, String> getTraceData(String fileName) {
        HashMap<String, String> traceMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)))) {
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long startTime = 0;
        long endTime = 0;

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

                    try {
                        startTime = dateFormat.parse(traceDataSplit[0]).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (linkTraceEndSubString != null && !value.toString().endsWith("ms-")) {
                    for (int j = start + 1; j < threadTraceData.length; j++) {
                        if (threadTraceData[j].endsWith(linkTraceEndSubString)) {
                            value.append(">>>");
                            value.append(threadTraceData[j]);
                            i = j;
                            try {
                                endTime = dateFormat.parse(threadTraceData[j].split(", ")[0]).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String timeSpend = endTime - startTime + "ms";
                            value.append(">>>");
                            value.append("timeSpend:-" + timeSpend + "-");
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

    //获取文件遍历调用链路追踪
    public static void lookTargetLinkTrace(File file, String methodName) {
        ArrayList<String> fileList = new ArrayList<>();
        File[] fs = file.listFiles(); //遍历filePath下的文件和目录，放在File数组中
        for (File f : fs) {
            if (f.isDirectory()) {
                lookTargetLinkTrace(f, methodName); //递归子目录
            }
            if (!f.isDirectory() && f.getName().endsWith("_Trace.log")) {//不是目录(即文件)，则打印
                fileList.add(f.getAbsolutePath());

                HashMap<String, String> methodLinkTrace = getMethodLinkTrace(f.getAbsolutePath(), methodName);
                Iterator<Map.Entry<String, String>> iterator = methodLinkTrace.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    System.out.println(next.getKey());
                    System.out.println(next.getValue().replace(">>>", "\n"));
                }
            }
        }
    }

}
