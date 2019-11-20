package com.ggj.mtracefront.controller;

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
    ArrayList<String> fileList;

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
        String threadId = linkTrackingVO.getThreadID();
        String startTime = linkTrackingVO.getStartTime();
        String endTime = linkTrackingVO.getEndTime();

        fileList = new ArrayList<>();
        ArrayList<HashMap<String, String>> traceLinks = new ArrayList<>();

//    public Result getLinkTraceData() {
//        String methodName = "ConsignDeliverTimeAPIImpl.getDeliverTimeConfig";
//        LinkTracking linkTracking = new LinkTracking();

        getFiles(new File(filePath));

        int id = 1;
        for (String fileName : fileList) {
            HashMap<String, String> methodLinkTrace = linkTracking.getMethodLinkTrace(fileName, methodName, threadId, startTime, endTime);
            Iterator<Map.Entry<String, String>> iterator = methodLinkTrace.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();

                // 添加父方法
                // 18356461107

                String value = next.getValue();
                if (value != null && value.length() != 0) {
                    HashMap<String, String> linkTraceData = new HashMap<>();
                    linkTraceData.put("id", id + "");
                    linkTraceData.put("traceLinks", value);
                    linkTraceData.put("", "");
                    traceLinks.add(linkTraceData);
                    id++;
                }
            }
        }
        return ResultFactory.buildSuccessResult(traceLinks);
    }

    /**
     * 遍历file下的文件和目录，放在File数组中
     *
     * @param file
     */
    public void getFiles(File file) {
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isDirectory()) {
                getFiles(f); //递归子目录
            }
            if (!f.isDirectory() && f.getName().endsWith("_Trace.log")) {//不是目录(即文件)，则打印
                fileList.add(f.getAbsolutePath());
            }
        }
    }

}
