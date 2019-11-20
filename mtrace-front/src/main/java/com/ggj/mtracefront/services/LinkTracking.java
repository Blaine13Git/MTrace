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
    public HashMap<String, String> getThreadLinkTrace(String fileName, String threadId, String startTime, String endTime) {
        HashMap<String, String> traceMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)))) {
            String linkTraceData = bufferedReader.readLine();
            while (linkTraceData != null) {
                if (!linkTraceData.startsWith("Class-Load")) {
                    // 根据线程Id进行第一次数据清洗
                    String[] splitTraceData = linkTraceData.split(",");
                    if (isInTime(startTime, endTime, splitTraceData[0]) && (splitTraceData[1].equals("ThreadId=" + threadId) || threadId == null || threadId.length() == 0)) {
                        if (traceMap.get(splitTraceData[1]) == null) {
                            traceMap.put(splitTraceData[1], linkTraceData);
                        } else {
                            traceMap.put(splitTraceData[1], traceMap.get(splitTraceData[1]) + "<br>" + linkTraceData);
                        }
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
    public HashMap<String, String> getMethodLinkTrace(String fileName, String methodName, String threadId, String inputStartTime, String inputEndTime) {
        HashMap<String, String> traceData = getThreadLinkTrace(fileName, threadId, inputStartTime, inputEndTime);
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
                    linkTraceEndSubString = traceDataSplit[1] + ", " + traceDataSplit[2].replace("Call", "Return");

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

    private boolean isInTime(String startTime, String endTime, String targetTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long startDate;
        long endDate;
        long targetDate;
        try {
            // 时间字段都空
            if ((startTime == null || startTime.length() == 0) && (endTime == null || endTime.length() == 0)) {
                return true;
            }

            // 开始时间空
            if (startTime == null || startTime.length() == 0) {
                startDate = 0;
            } else {
                startDate = dateFormat.parse(startTime).getTime();
            }

            // 结束时间空
            if (endTime == null || endTime.length() == 0) {
                endDate = new Date().getTime();
            } else {
                endDate = dateFormat.parse(endTime).getTime();
            }

            targetDate = dateFormat.parse(targetTime).getTime();

            if ((targetDate - startDate) >= 0 && (endDate - targetDate) >= 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }
}
