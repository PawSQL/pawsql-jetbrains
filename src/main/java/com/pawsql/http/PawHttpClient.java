package com.pawsql.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawsql.exception.OptimizationException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PawHttpClient {
    private static final int MAX_RETRIES = 3;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String userKey;

    public PawHttpClient(String baseUrl, String userKey) {
        this.baseUrl = baseUrl;
        this.userKey = userKey;
        this.objectMapper = new ObjectMapper();

        this.client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(MAX_RETRIES))
                .build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public <T> T get(String path, Class<T> responseType) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userKey", userKey);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)  // Changed from GET to POST since we need to send userKey
                .build();

        return executeRequest(request, responseType);
    }

    public <T> T get(String path, TypeReference<T> responseType) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userKey", userKey);

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)  // Changed from GET to POST since we need to send userKey
                .build();

        return executeRequest(request, responseType);
    }

    public <T> T post(String path, Object requestData, Class<T> responseType) throws IOException {
        Map<String, Object> fullRequestBody = prepareRequestBody(requestData);
        fullRequestBody.put("userKey", userKey);

        String jsonBody = objectMapper.writeValueAsString(fullRequestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)
                .build();

        return executeRequest(request, responseType);
    }

    private Map<String, Object> prepareRequestBody(Object requestData) {
        Map<String, Object> fullRequestBody = new HashMap<>();

        if (requestData != null) {
            if (requestData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dataMap = (Map<String, Object>) requestData;
                fullRequestBody.putAll(dataMap);
            } else {
                // 如果不是 Map，则将整个对象作为 "data" 字段的值
                fullRequestBody.put("data", requestData);
            }
        }

        return fullRequestBody;
    }

    private <T> T executeRequest(Request request, Class<T> responseType) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new OptimizationException("Request failed with code: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new OptimizationException("Empty response body");
            }

            String responseString = responseBody.string();
            return objectMapper.readValue(responseString, responseType);
        }
    }

    private <T> T executeRequest(Request request, TypeReference<T> responseType) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new OptimizationException("Request failed with code: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new OptimizationException("Empty response body");
            }

            String responseString = responseBody.string();
            return objectMapper.readValue(responseString, responseType);
        }
    }

    private static class RetryInterceptor implements Interceptor {
        private final int maxRetries;

        RetryInterceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();

            IOException exception = null;
            int tryCount = 0;

            while (tryCount < maxRetries) {
                try {
                    return chain.proceed(request);
                } catch (IOException e) {
                    tryCount++;
                    exception = e;

                    if (tryCount == maxRetries) {
                        break;
                    }
                }
            }
            throw exception;
        }
    }
}
