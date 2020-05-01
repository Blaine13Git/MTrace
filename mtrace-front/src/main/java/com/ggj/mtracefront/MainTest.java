package com.ggj.mtracefront;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainTest {
    private static String partner = "hqbs196";
    private static String key = "bc0acfa673064b88908d097c3a53353b";
    private static String url = "http://seller-open-api.test.gegejia.com/order/findOrders";

    public static void main(String[] args) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", "2019-12-16 00:00:00");
        params.put("endTime", "2019-12-17 23:30:00");
        params.put("status", 100);
        params.put("page", 1);
        params.put("pageSize", 50);

        Map<String, Object> reqData = new HashMap<>();
        reqData.put("partner", partner);
        reqData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        reqData.put("params", params);

        String sign = DigestUtils.md5Hex(key + JSONObject.toJSONString(reqData) + key).toUpperCase();
        System.out.println(sign);

        List<Charset> charset = new ArrayList<>();
        charset.add(Charset.forName("utf-8"));//设置字符集，utf-8

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);//设置返回类型,json格式 编码
        headers.setAcceptCharset(charset);
        headers.add("sign", sign);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);//设置超时时间
        requestFactory.setConnectTimeout(10000);//设置链接超时时间

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(reqData), headers);
        JSONObject result = restTemplate.postForObject(url, entity, JSONObject.class);
        System.out.println(result);

    }


    void getTime(){


    }
}
