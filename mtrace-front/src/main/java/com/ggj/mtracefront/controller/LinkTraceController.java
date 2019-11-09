package com.ggj.mtracefront.controller;

import com.ggj.mtracefront.requestVO.LinkTrackingVO;
import com.ggj.mtracefront.result.Result;
import com.ggj.mtracefront.result.ResultFactory;
import com.ggj.tester.LinkTracking;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.util.Objects;

@CrossOrigin
@RestController
public class LinkTraceController {
    private static final String filePath = "/Users/changfeng/work/code/MTrace/out";

//    @Resource
//    LinkTracking linkTracking;

    @PostMapping(value = "/api/linkTrace", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result getLinkTraceData(@Valid @RequestBody LinkTrackingVO linkTrackingVO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String message = String.format("", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }

        System.out.println(linkTrackingVO.getClassName());
        System.out.println(linkTrackingVO.getMethodName());
        System.out.println(linkTrackingVO.getParameterName());

        return ResultFactory.buildSuccessResult("OK");
    }
}
