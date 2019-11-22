package com.ggj.mtracefront.services;


import org.springframework.stereotype.Component;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author muyi
 */
@Component
public class LinkTracking {

    public static void main(String[] args) {
        String fileName = "/Users/changfeng/work/traceLogs/2019-11-21_trade-service-consign-test_Trace.log";
        String methodName = "DeliverTimeConfigDO.setIsDeleted";
        LinkTracking linkTracking = new LinkTracking();
        linkTracking.getMethodLinkTrace(fileName, methodName, "", "", "");
    }

    public ArrayList<String> getTargetData(String fileName, String methodName, String threadId, String startTime, String endTime) {
        ArrayList<String> dataList = new ArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName), 500 * 1024)) {
            String linkTraceData = reader.readLine();
            while (linkTraceData != null) {
                if (linkTraceData.endsWith(methodName) && linkTraceData.contains("Call=") && isInTime(startTime, endTime, linkTraceData.substring(0, linkTraceData.lastIndexOf(", ThreadId=")))) {
                    dataList.add(linkTraceData);
                    String start_subString = linkTraceData.substring(linkTraceData.lastIndexOf("ThreadId="));
                    String linkTraceData_Target = reader.readLine();
                    int countNum = 1; //call
                    int findNum = 0;
                    boolean flag = true;
                    while (flag) {
                        String target_substring = linkTraceData_Target.substring(linkTraceData_Target.lastIndexOf("ThreadId="));
                        if (target_substring.contains("Return=")) {
                            if (target_substring.replace("Return=", "Call=").equals(start_subString)) {
                                findNum++;
                                dataList.add(linkTraceData_Target);
                            }
                        }

                        if (target_substring.contains("Call=")) {
                            dataList.add(linkTraceData_Target);
                        }

                        if (target_substring.replace("Return=", "Call=").equals(start_subString)) {
                            findNum++;
                            dataList.add(linkTraceData_Target);
                        }
                        else if (target_substring.equals(start_subString)) {
                            countNum++;
                            dataList.add(linkTraceData_Target);
                        } else {
                            dataList.add(linkTraceData_Target);
                        }
                        if (countNum == findNum) {
                            flag = false;
                        }
                        linkTraceData_Target = reader.readLine();
                    }
                }
                linkTraceData = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public HashMap<String, String> getThreadData(ArrayList<String> data) {
        HashMap<String, String> targetData = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String tempData = data.get(i);
            String[] splitTraceData = tempData.split(",");
            if (targetData.get(splitTraceData[1]) == null) {
                targetData.put(splitTraceData[1], tempData);
            } else {
                targetData.put(splitTraceData[1], targetData.get(splitTraceData[1]) + "<br>" + tempData);
            }
        }
        return targetData;
    }

    // 清洗方法
    public HashMap<String, String> getMethodLinkTrace(String fileName, String methodName, String threadId, String inputStartTime, String inputEndTime) {
        ArrayList<String> targetData = getTargetData(fileName, methodName, threadId, inputStartTime, inputEndTime);
        HashMap<String, String> traceData = getThreadData(targetData);
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

    // 时间处理
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
