package com.ggj.mtracefront.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.ggj.mtracefront.requestVO.LinkTrackingVO;
import com.ggj.mtracefront.result.Result;
import com.ggj.mtracefront.result.ResultFactory;
import com.ggj.mtracefront.services.LinkTracking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@CrossOrigin
@RestController
public class LinkTraceController {

    private static final String filePath = "/Users/changfeng/work/code/MTrace/out";
    ArrayList<String> fileList = new ArrayList<>();
    ArrayList<HashMap<String, String>> traceLinks = new ArrayList<>();

    @Autowired
    private LinkTracking linkTracking;

    @PostMapping(value = "/api/linkTrace", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result getLinkTraceData(@Valid @RequestBody LinkTrackingVO linkTrackingVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = String.format("", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }

        String methodName = linkTrackingVO.getMethodName();
        System.out.println(linkTrackingVO.getParameterName());

//    public Result getLinkTraceData() {
//        String methodName = "ConsignDeliverTimeAPIImpl.getDeliverTimeConfig";
//        LinkTracking linkTracking = new LinkTracking();

        getFiles(new File(filePath));

        for (String fileName : fileList) {
            HashMap<String, String> methodLinkTrace = linkTracking.getMethodLinkTrace(fileName, methodName);
            Iterator<Map.Entry<String, String>> iterator = methodLinkTrace.entrySet().iterator();
            int id = 0;
            while (iterator.hasNext()) {
                id++;
                Map.Entry<String, String> next = iterator.next();
                String value = next.getValue();
                if (value != null && value.length() != 0) {

                    System.out.println(value);

                    HashMap<String, String> linkTraceData = new HashMap<>();
                    linkTraceData.put("id", id + "");
                    linkTraceData.put("traceLinks", value);

//                    String[] splitTrace = value.split(">>>");
//                    int length = splitTrace.length;
//                    for (int i = 0; i < length - 1; i++) {
//                        String[] splitColumn = splitTrace[i].split(",");
//                        if (splitColumn.length == 1) {
//                            linkTraceData.put("timeSpend", splitTrace[length - 1]);
//                            traceLinks.add(linkTraceData);
//                        } else {
//                            linkTraceData.put("timestamp", splitColumn[0]);
//                            linkTraceData.put("threadId", splitColumn[1]);
//                            linkTraceData.put("traceLinks", splitColumn[2]);
//                        }
//                    }

//                    linkTraceData.put("timeSpend", splitTrace[length - 1].replace("timeSpend:", ""));
                    traceLinks.add(linkTraceData);
                }
            }
        }
        return ResultFactory.buildSuccessResult(traceLinks);
    }

    public void getFiles(File file) {
        File[] fs = file.listFiles(); //遍历file下的文件和目录，放在File数组中
        for (File f : fs) {
            if (f.isDirectory()) {
                getFiles(f); //递归子目录
            }
            if (!f.isDirectory() && f.getName().endsWith("_Trace.log")) {//不是目录(即文件)，则打印
                fileList.add(f.getAbsolutePath());
            }
        }
    }

    public static ArrayList<String> data_save = new ArrayList<>();
    public static String data = "2019-11-04 13:06:55.108, thread id = 1, call method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>2019-11-04 13:06:55.108, thread id = 1, call method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:06:55.108, thread id = 1, call method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:06:55.108, thread id = 1, call method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:06:55.120, thread id = 1, return method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:06:55.120, thread id = 1, return method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:06:55.122, thread id = 1, return method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:06:55.122, thread id = 1, call method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:06:55.122, thread id = 1, return method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:06:55.122, thread id = 1, return method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>timeSpend:-14ms->>>2019-11-04 13:55:13.229, thread id = 1, call method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>2019-11-04 13:55:13.229, thread id = 1, call method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:55:13.230, thread id = 1, call method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:55:13.230, thread id = 1, call method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:55:13.272, thread id = 1, return method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:55:13.272, thread id = 1, return method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:55:13.274, thread id = 1, call method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>timeSpend:-45ms->>>2019-11-04 13:55:13.229, thread id = 1, call method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>2019-11-04 13:55:13.229, thread id = 1, call method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:55:13.230, thread id = 1, call method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:55:13.230, thread id = 1, call method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:55:13.272, thread id = 1, return method = com/ggj/trade/consign/mapper/DeliverTimeConfigMapper.getByDeliverId>>>2019-11-04 13:55:13.272, thread id = 1, return method = com/ggj/trade/consign/service/ConsignDeliverTimeService.getByDeliverId>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/business/ConsignDeliverTimeBusiness.getDeliverTimeConfig>>>2019-11-04 13:55:13.274, thread id = 1, call method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/util/PlainResultUtil.buildSuccessResult>>>2019-11-04 13:55:13.274, thread id = 1, return method = com/ggj/trade/consign/api/ConsignDeliverTimeAPIImpl.getDeliverTimeConfig>>>timeSpend:-45ms-";

    public static void splitData(String data) {
        if (data.contains("ms->>>")) {
            int index = data.lastIndexOf("ms->>>");
            data_save.add(data.substring(index + 6));
            data = data.substring(0, index + 3);
            splitData(data);
        } else {
            data_save.add(data);
        }
    }

    public static void main(String[] args) {
//        splitData(data);
//        data_save.stream().forEach(dd -> System.out.println(dd));

//        ArrayList<HashMap<String, String>> linkTraceData = (ArrayList<HashMap<String, String>>) new LinkTraceController().getLinkTraceData().getData();
//
//        for (int i = 0; i < linkTraceData.size(); i++) {
//            Iterator<Map.Entry<String, String>> iterator = linkTraceData.get(i).entrySet().iterator();
//            while (iterator.hasNext()){
//                Map.Entry<String, String> next = iterator.next();
//                System.out.println(next.getKey());
//                System.out.println(next.getValue());
//            }
//            System.out.println("===================");
//        }


//        JSONArray array = JSONArray.parseArray(JSON.toJSONString(linkTraceData));
//        System.out.println(array.toString());

    }

}