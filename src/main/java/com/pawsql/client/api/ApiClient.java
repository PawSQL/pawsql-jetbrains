package com.pawsql.client.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pawsql.client.api.model.ApiAnalysisCreate;
import com.pawsql.client.api.model.ApiUserRequestBody;
import com.pawsql.client.api.model.LoginRequestBody;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final Logger logger = Logger.getLogger(ApiClient.class);
    private static final String API_VERSION = "/api/v1";
    private final String baseUrl;

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    private String frontUrl;
    private final String email;
    private final String password;
    private String userKey;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ApiClient(String baseUrl, String email, String password) {
        // 确保 baseUrl 不以 / 结尾
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.email = email;
        this.password = password;
        logger.info("ApiClient initialized with baseUrl: " + this.baseUrl + ", username: " + email);
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean testConnection() {
        try {
            logger.info("Testing connection...");
            ApiResult result = getUserKeyFromServer();
            if (result != null && result.isSuccess()) {
                Map map = gson.fromJson(
                        gson.toJson(result.getData()),
                        Map.class
                );
                this.userKey = map.get("apikey").toString();
//                this.frontUrl = map.get()
                logger.info("Connection test successful, userKey obtained");
                return true;
            }
            logger.warn("Connection test failed: " + (result != null ? result.getMessage() : "null result"));
            return false;
        } catch (Exception e) {
            logger.error("Connection test failed with exception", e);
            return false;
        }
    }

    private ApiResult getUserKeyFromServer() {
        try {
            URL url = new URI(baseUrl + API_VERSION + "/getUserKey").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);

            LoginRequestBody requestBody = new LoginRequestBody(email, password);
            String jsonInputString = gson.toJson(requestBody);
            logger.debug("Sending request to " + url + "\nRequest body:\n" + jsonInputString);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            logger.debug("Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                logger.debug("Response body:\n" + gson.toJson(gson.fromJson(responseBody, Object.class)));
                return gson.fromJson(responseBody, ApiResult.class);
            }

            logger.warn("Request failed with response code: " + responseCode);
            return ApiResult.fail("Failed to get user key, response code: " + responseCode);
        } catch (Exception e) {
            logger.error("Failed to get user key", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    public ApiResult optimizeSQL(Map<String, Object> request) {
        try {
            if (userKey == null) {
                logger.error("Cannot optimize SQL: userKey is null");
                return ApiResult.fail("User key is not set");
            }

            // 1. 创建分析任务
            ApiResult createResult = createAnalysis(request);
            if (createResult.getCode() != 200 || createResult.getData() == null) {
                return ApiResult.fail("Optimization failed: " + createResult.getMessage());
            }

            // 获取分析ID
            Map<String, Object> createData = gson.fromJson(
                    gson.toJson(createResult.getData()),
                    Map.class
            );
            String analysisId = createData.get("analysisId").toString();

            // 2. 获取分析结果
            ApiResult summaryResult = getAnalysisSummary(analysisId);
            if (summaryResult.getCode() != 200 || summaryResult.getData() == null) {
                return ApiResult.fail("Error when retrieving optimization result: " + summaryResult.getMessage());
            }

            Map<String, Object> summaryData = gson.fromJson(
                    gson.toJson(summaryResult.getData()),
                    Map.class
            );

            // 获取汇总信息
            Map<String, Object> basicSummary = (Map<String, Object>) summaryData.get("basicSummary");
            if (basicSummary != null && basicSummary.containsKey("summaryMarkdownZh")) {
                return ApiResult.succ(summaryData);
            }

            return ApiResult.fail("Error when retrieving optimization result");

        } catch (Exception e) {
            logger.error("Failed to optimize SQL", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    private ApiResult createAnalysis(Map<String, Object> request) throws IOException, URISyntaxException {
        URL url = new URI(baseUrl + API_VERSION + "/createAnalysis").toURL();
//        URL url = new URL(null, baseUrl + API_VERSION + "/createAnalysis");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        ApiAnalysisCreate requestBody = new ApiAnalysisCreate();
        requestBody.setUserKey(userKey);
        requestBody.setWorkspace((String) request.get("workspaceId"));
        requestBody.setWorkload((String) request.get("sqlText"));
        requestBody.setSingleQueryFlag(true);
        requestBody.setQueryMode("plain_sql");
        requestBody.setCloseRewrite(!(boolean) request.get("isRewrite"));
        requestBody.setValidateFlag((Boolean) request.get("validateFlag"));
        requestBody.setAnalyzeFlag((Boolean) request.get("analyzeFlag"));
        requestBody.setIndexOnly((Boolean) request.get("indexOnly"));
        requestBody.setMaxMembers((Integer) request.get("maxMembers"));
        requestBody.setDeduplicateFlag((Boolean) request.get("isDedupIndex"));
        requestBody.setMaxMembersForIndexOnly((Integer) request.get("maxMembers4IndexOnly"));
        requestBody.setMaxPerTable((Integer) request.get("maxPerTable"));
        requestBody.setUpdateStatsBeforeValidationFlag((Boolean) request.get("updateStats"));

        String jsonInputString = gson.toJson(requestBody);
        logger.info("2. Sending optimization request to " + baseUrl);
        logger.debug("\nRequest body:\n" + jsonInputString);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return handleResponse(connection);
    }

    private ApiResult getAnalysisSummary(String analysisId) throws IOException, URISyntaxException {
        URL url = new URI(baseUrl + API_VERSION + "/getAnalysisSummary").toURL();
//        URL url = new URL(null, baseUrl + API_VERSION + "/getAnalysisSummary");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userKey", userKey);
        requestBody.put("analysisId", analysisId);

        String jsonInputString = gson.toJson(requestBody);
        logger.info("3. Retrieving optimization summary from PawSQL Server");
        logger.debug("Request body:\n" + jsonInputString);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return handleResponse(connection);
    }

    public ApiResult getStatementDetails(String stmtId) throws IOException, URISyntaxException {
        URL url = new URI(baseUrl + API_VERSION + "/getStatementDetails").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        Map<String, String> requestData = new HashMap<>();
        requestData.put("userKey", userKey);
        requestData.put("analysisStmtId", stmtId);

        String jsonInputString = gson.toJson(requestData);
        logger.info("4. Retrieving optimization details from PawSQL Server");
        logger.debug("\nRequest body:\n" + jsonInputString);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return handleResponse(connection);
    }

    private ApiResult handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseBody = response.toString();
            logger.debug("Response body:\n" + gson.toJson(gson.fromJson(responseBody, Object.class)));

            ApiResult result = gson.fromJson(responseBody, ApiResult.class);
            System.out.println(result);
            System.out.println("========");
            if (!result.isSuccess()) {
                logger.warn("API request failed: " + result.getMessage());
            }
            return result;
        }

        // 读取错误流
        String errorMessage;
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorMessage = errorResponse.toString();
        } catch (Exception e) {
            errorMessage = "无法读取错误信息";
        }

        logger.warn("Request failed with response code: " + responseCode + ", error: " + errorMessage);
        return ApiResult.fail("Request failed (HTTP " + responseCode + "): " + errorMessage);
    }

    public ApiResult validateUserKey() {
        try {
            URL url = new URI(baseUrl + API_VERSION + "/getUserKey").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            LoginRequestBody requestBody = new LoginRequestBody(email, password);
            String jsonInputString = gson.toJson(requestBody);
            logger.info("Validating " + email + " credential at " + baseUrl);
            logger.debug("Sending request to " + url + "\nRequest body:\n" + jsonInputString);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            logger.info("Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                logger.debug("Response body:\n" + gson.toJson(gson.fromJson(responseBody, Object.class)));
                return gson.fromJson(responseBody, ApiResult.class);
            } else {
                logger.warn("Request failed with response code: " + responseCode);
                return ApiResult.fail("Failed to get user key");
            }
        } catch (Exception e) {
            logger.error("Failed to validate user key", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    public ApiResult listWorkspaces() throws Exception {
        ApiUserRequestBody requestBody = new ApiUserRequestBody(userKey);
        return post("/listWorkspaces", requestBody);
    }

    private ApiResult post(String path, Object requestBody) throws Exception {
//        URL url = new URL(null, baseUrl + API_VERSION + path);
        URL url = new URI(baseUrl + API_VERSION + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", "UTF-8");

        String jsonInputString = gson.toJson(requestBody);
        logger.debug("Sending request to " + url + "\nRequest body:\n" + jsonInputString);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        logger.debug("Response code: " + responseCode);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            String responseBody = response.toString();
            logger.debug("Response body:\n" + gson.toJson(gson.fromJson(responseBody, Object.class)));
            return gson.fromJson(responseBody, ApiResult.class);
        }
    }
}