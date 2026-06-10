package com.tech.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp工具类
 *
 * @author shenjy 2019/02/19
 */
@Slf4j
public class OkHttpUtil {

    private static final OkHttpClient client;

    private static final MediaType MEDIA_JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType MEDIA_STREAM = MediaType.get("application/octet-stream; charset=utf-8");

    private OkHttpUtil() {
    }

    static {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES))
                .build();
    }

    public static Response get(String url) throws IOException {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }
        Request request = new Request.Builder().url(httpUrl).build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static <T> T get(String url, Class<T> clazz) {
        String data;
        try (Response response = get(url)) {
            if (response == null || response.body() == null) {
                return null;
            }
            data = response.body().string();
        } catch (IOException e) {
            log.error("调用get方法异常", e);
            return null;
        }
        if (StringUtils.isBlank(data)) {
            return null;
        }

        return JsonUtil.parse(data, clazz);
    }

    public static Response get(String url, Map<String, Object> params) throws IOException {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }
        HttpUrl.Builder builder = httpUrl.newBuilder();
        if (null != params) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(builder.build()).build();

        Call call = client.newCall(request);
        return call.execute();
    }

    public static Response get(String url, Map<String, Object> params, Map<String, String> headers) throws IOException {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }
        HttpUrl.Builder builder = httpUrl.newBuilder();
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        Request.Builder requestBuilder = new Request.Builder().url(builder.build());
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static <T> T get(String url, Map<String, Object> params, Class<T> clazz) {
        String data;
        try (Response response = get(url, params)) {
            if (response == null || response.body() == null) {
                return null;
            }

            data = response.body().string();
        } catch (IOException e) {
            log.error("调用get方法异常", e);
            return null;
        }

        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JsonUtil.parse(data, clazz);
    }

    /**
     * 表单格式的post请求
     *
     * @param url    路径
     * @param params 参数
     * @return 请求响应
     */
    public static Response postForm(String url, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (null != params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        return call.execute();
    }

    public static <T> T postForm(String url, Map<String, String> params, Class<T> clazz) {
        String data;
        try (Response response = postForm(url, params)) {
            if (response.body() == null) {
                return null;
            }

            data = response.body().string();
        } catch (IOException e) {
            log.error("调用form post方法异常", e);
            return null;
        }

        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JsonUtil.parse(data, clazz);
    }

    /**
     * json格式的post请求
     *
     * @param url    路径
     * @param params 参与
     * @return 响应
     */
    public static Response postJson(String url, String params) throws IOException {
        return postJson(url, params, Collections.emptyMap());
    }

    /**
     * json格式的post请求
     *
     * @param url    路径
     * @param params 参与
     * @return 响应
     */
    public static Response postJson(String url, String params, Map<String, String> headers) throws IOException {
        // 创建 RequestBody 包装 JSON 数据
        RequestBody requestBody = RequestBody.create(params, MEDIA_JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        // 添加头部
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        return client.newCall(request).execute();
    }

    public static <T> T postJson(String url, Map<String, Object> params, Map<String, String> headers, Class<T> clazz) {
        String data;
        try (Response response = postJson(url, JsonUtil.toJson(params), headers)) {
            if (response.body() == null) {
                return null;
            }

            data = response.body().string();
        } catch (IOException e) {
            log.error("调用form post方法异常", e);
            return null;
        }

        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JsonUtil.parse(data, clazz);
    }

    public static <T> T postJson(String url, Class<T> clazz) {
        return postJson(url, "", new HashMap<>(), clazz);
    }

    public static <T> T postJson(String url, Map<String, Object> params, Class<T> clazz) {
        return postJson(url, JsonUtil.toJson(params), new HashMap<>(), clazz);
    }

    public static <T> T postJson(String url, String params, Map<String, String> headers, Class<T> clazz) {
        String data;
        try (Response response = postJson(url, params, headers)) {
            if (response.body() == null) {
                return null;
            }

            data = response.body().string();
        } catch (IOException e) {
            log.error("调用form post方法异常", e);
            return null;
        }

        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JsonUtil.parse(data, clazz);
    }

    /**
     * 二进制参数的post请求
     *
     * @param url  路径
     * @param body 参数
     * @return 响应
     */
    public static Response postBody(String url, byte[] body, Map<String, String> headers) throws IOException {
        RequestBody requestBody = RequestBody.create(body, MEDIA_STREAM);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        // 添加头部
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        return client.newCall(request).execute();
    }

    public static <T> T postBody(String url, byte[] params, Map<String, String> headers, Class<T> clazz) {
        String data;
        try (Response response = postBody(url, params, headers)) {
            if (response.body() == null) {
                return null;
            }

            data = response.body().string();
        } catch (IOException e) {
            log.error("调用body post方法异常", e);
            return null;
        }

        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JsonUtil.parse(data, clazz);
    }
}
