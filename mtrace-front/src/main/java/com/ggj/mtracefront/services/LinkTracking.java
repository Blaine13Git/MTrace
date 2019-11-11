package com.ggj.mtracefront.services;


import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author muyi
 */
@Component
public class LinkTracking {

    //清洗线程
    public HashMap<String, String> getThreadLinkTrace(String fileName, String threadId) {
        HashMap<String, String> traceMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)))) {
            String linkTraceData = bufferedReader.readLine();
            while (linkTraceData != null) {
                if (!linkTraceData.startsWith("Class-Load") && linkTraceData.contains(", threadId=" + threadId + ", ")) {
                    // 根据线程Id进行第一次数据清洗
                    String[] splitTraceData = linkTraceData.split(", ");
                    if (traceMap.get(splitTraceData[1]) == null) {
                        traceMap.put(splitTraceData[1], linkTraceData);
                    } else {
                        traceMap.put(splitTraceData[1], traceMap.get(splitTraceData[1]) + "<br>" + linkTraceData);
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
    public HashMap<String, String> getMethodLinkTrace(String fileName, String methodName, String threadId) {
        HashMap<String, String> traceData = getThreadLinkTrace(fileName, threadId);
        Iterator<Map.Entry<String, String>> iterator = traceData.entrySet().iterator();
        HashMap<String, String> methodLinkTrace = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long startTime = 0;
        long endTime = 0;

        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            StringBuilder value = new StringBuilder();
            String[] threadTraceData = next.getValue().split("<br>");

            String linkTraceEndSubString = null;
            int start = 0;
            for (int i = 0; i < threadTraceData.length; i++) {
                if (threadTraceData[i].endsWith(methodName)) {
                    start = i;
                    if (value == null || value.length() == 0) {
                        value.append(threadTraceData[i]);
                    } else {
                        value.append("<br>");
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
                            value.append("<br>");
                            value.append(threadTraceData[j]);
                            i = j;
                            try {
                                endTime = dateFormat.parse(threadTraceData[j].split(", ")[0]).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String timeSpend = endTime - startTime + "ms";
                            value.append("<br>");
                            value.append("timeSpend:-" + timeSpend + "-");
                            break;
                        } else {
                            value.append("<br>");
                            value.append(threadTraceData[j]);
                        }
                    }
                }
            }
            methodLinkTrace.put(key, value.toString());
        }
        return methodLinkTrace;
    }

    //清洗调用的起点
    public HashMap<String, String> getTargetStart(String targetMethodName) {
//        HashMap<String,String>
        return null;
    }

    /**
     * backup for ……
     */
    private void getCaller() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        System.out.println(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName());
    }

    public static void main(String[] args) {
        String threadId = "1";
        String fileName = "/Users/changfeng/work/code/MTrace/out/artifacts/mtrace/2019-11-04_trade-service-consign-test_Trace.log";
//        String methodName = "ConsignDeliverTimeAPIImpl.getDeliverTimeConfig";
//        File file = new File("/Users/changfeng/work/code/MTrace/out");
//        HashMap<String, String> methodLinkTrace =null;
//        try {
//             methodLinkTrace = new LinkTracking().getMethodLinkTrace(fileName, methodName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Iterator<Map.Entry<String, String>> iterator = methodLinkTrace.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, String> next = iterator.next();
//            System.out.println(next.getValue().replace("<br>", "\n"));
//        }

        Iterator<Map.Entry<String, String>> iterator = new LinkTracking().getThreadLinkTrace(fileName, threadId).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            System.out.println(next.getKey());
            System.out.println(next.getValue().replace("<br>", "\n"));
        }
    }

}
