package com.example.http;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static OkHttpClient client;
    public static final String DEFAULT_TYPE = "application/json; charset=utf-8";
    public static final String GET = "GET";
    public static final String POST = "POST";

    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (OkHttpClient.class) {
                if (client == null) {
                    client = new OkHttpClient().newBuilder()
                            .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                            .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                            .build();
                }
            }
        }
        return client;
    }

    public static String doGet(String url, String callMethod) throws IOException {
        long startTime = System.currentTimeMillis();
        addRequestLog(GET, callMethod, url, null, null);
        Request request = new Request.Builder().url(url).build();
        Response response = getInstance().newCall(request).execute();
        return getResult(response, startTime);
    }

    public static String doPost(String url, String postBody, String mediaType) throws IOException {
        long startTime = System.currentTimeMillis();
        addRequestLog(POST, "", url, postBody, null);
        MediaType type = MediaType.parse(mediaType == null ? DEFAULT_TYPE : mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(type, postBody))
                .build();
        Response response = getInstance().newCall(request).execute();
        return getResult(response, startTime);
    }

    private static String getResult(Response response, long startTime) throws IOException {
        int httpCode = response.code();
        String result;
        ResponseBody body = response.body();
        if (body != null) {
            result = body.string();
            addResponseLog(httpCode, result, startTime);
        } else {
            throw new RuntimeException("exception in OkHttpUtil,response body is null");
        }
        return result;
    }

    private static void addRequestLog(String method, String callMethod, String url, String body, String formParam) {
        LOGGER.info("===========================request begin================================================");
        LOGGER.info("URI          : {}", url);
        LOGGER.info("Method       : {}", method);
        if (StringUtils.isNotBlank(body)) {
            LOGGER.info("Request body : {}", body);
        }
        if (StringUtils.isNotBlank(formParam)) {
            LOGGER.info("Request param: {}", formParam);
        }
        LOGGER.info("---------------------------request end--------------------------------------------------");
    }

    private static void addResponseLog(int httpCode, String result, long startTime) {
        long endTime = System.currentTimeMillis();
        LOGGER.info("Status       : {}", httpCode);
        LOGGER.info("Response     : {}", result);
        LOGGER.info("Time         : {} ms", endTime - startTime);
        LOGGER.info("===========================response end================================================");
    }
}
