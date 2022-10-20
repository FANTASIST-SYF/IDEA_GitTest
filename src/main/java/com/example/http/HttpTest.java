package com.example.http;

import com.alibaba.fastjson.JSONObject;
import com.example.http.entity.MyRequest;

import java.io.IOException;

public class HttpTest {

    public static void main(String[] args) throws IOException {
//        String url = "http://192.168.0.61:8080/DataTransfer/select_production";
//        MyRequest request = new MyRequest();
//        request.setRequestName("selectByProductCode");
//        request.setKey("productCode");
//        request.setValue("P-180600079");
//        String postBody = JSONObject.toJSONString(request);
//        String result = HttpUtil.doPost(url, postBody, null);
//        System.out.println(result);

        String getUrl = "http://192.168.10.3:10050/mrp/product/selectByProductCode?productCode=P-170900008";
        String result = HttpUtil.doGet(getUrl, "");
        System.out.println(result);
    }
}
