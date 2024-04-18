/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.service.command.commands.valorant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.spco.SpCoBot;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.Closeable;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RiotAuth implements Closeable {
    public static final String AUTH_URL = "https://auth.riotgames.com/api/v1/authorization";
    public static final String REGION_URL = "https://riot-geo.pas.si.riotgames.com/pas/v1/product/valorant";
    public static final String VERIFED_URL = "https://email-verification.riotgames.com/api/v1/account/status";
    public static final String ENTITLEMENT_URL = "https://entitlements.auth.riotgames.com/api/token/v1";
    public static final String USERINFO_URL = "https://auth.riotgames.com/userinfo";
    private final String username;
    private final String password;
    private CloseableHttpClient session;
    private HttpClientContext context;
    private String accessToken;
    private String idToken;
    private String entitlement;
    private String sub;
    private String name;
    private String tag;
    private String creationData;
    private String typeBan;
    private String region;

    public RiotAuth(String username, String password) {
        this.username = username;
        this.password = password;

        CookieStore cookieStore = new BasicCookieStore();
        this.context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        this.session = HttpClients.custom().setSSLContext(createTrustAllSSLContext()).setDefaultCookieStore(cookieStore).build();

    }

    public void parse(String[] tokens) {
        this.accessToken = tokens[0];
        this.idToken = tokens[1];

        this.entitlement = getEntitlementToken();

        String ignored = getEmailVerifed();

        String[] userInfo = getUserInfo();

        this.sub = userInfo[0];
        this.name = userInfo[1];
        this.tag = userInfo[2];
        this.creationData = userInfo[3];
        this.typeBan = userInfo[4];
        this.region = getRegion(null);
    }

    public static SSLContext createTrustAllSSLContext() {
        try {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext customSSLContext = SSLContext.getInstance("TLS");
            customSSLContext.init(null, trustAllCertificates, new SecureRandom());
            return customSSLContext;
        } catch (Exception e) {
            SpCoBot.LOGGER.error(e);
            return null;
        }
    }

    public String[] authorize() throws IOException, InterruptedException {
        JsonObject data = new JsonObject();
        data.addProperty("acr_values", "urn:riot:bronze");
        data.addProperty("claims", "");
        data.addProperty("client_id", "riot-client");
        data.addProperty("nonce", "oYnVwCSrlS5IHKh7iI16oQ");
        data.addProperty("redirect_uri", "http://localhost/redirect");
        data.addProperty("response_type", "token id_token");
        data.addProperty("scope", "openid link ban lol_region");
        HttpPost post = new HttpPost(AUTH_URL);
        post.setEntity(new StringEntity(data.toString()));
        post.setHeader("Content-Type", "application/json");
        post.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
        post.setHeader("Accept-Language", "en-US,en;q=0.9");
        post.setHeader("Accept", "application/json, text/plain, */*");
        session.execute(post, context);

        JsonObject data1 = new JsonObject();
        data1.addProperty("language", "en_US");
        data1.addProperty("password", this.password);
        data1.addProperty("remember", "true");
        data1.addProperty("type", "auth");
        data1.addProperty("username", this.username);
        HttpPut put = new HttpPut(AUTH_URL);
        put.setEntity(new StringEntity(data1.toString()));
        put.setHeader("Content-Type", "application/json");
        put.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
        put.setHeader("Accept-Language", "en-US,en;q=0.9");
        put.setHeader("Accept", "application/json, text/plain, */*");

        HttpResponse response = session.execute(put, context);

        String responseBody = EntityUtils.toString(response.getEntity());

        if (responseBody.contains("access_token")) {
            // 提取多因素认证后的令牌
            JsonObject authResponseData = JsonParser.parseString(responseBody).getAsJsonObject();
            String uri = authResponseData.getAsJsonObject("response").getAsJsonObject("parameters").get("uri").getAsString();
            Pattern pattern = Pattern.compile("access_token=([^&]*)&.*id_token=([^&]*)&");
            // 使用正则表达式匹配 URI 字符串
            Matcher matcher = pattern.matcher(uri);

            // 检查是否找到匹配项
            if (matcher.find()) {
                String token = matcher.group(1);
                String tokenId = matcher.group(2);
                return new String[]{token, tokenId};
            } else {
                throw new IllegalArgumentException("No match found");
            }

        } else if (responseBody.contains("auth_failure")) {
            return new String[]{"x", "auth_failure"};
        } else if (responseBody.contains("rate_limited")) {
            Thread.sleep(40000); // 等待 40 秒
            return new String[]{"x", "rate_limited"};
        } else if (responseBody.contains("invalid_session_id")) {
            return new String[]{"x", "invalid_session_id"};
        } else {
            return new String[]{"x", "2fa_auth"};
        }
    }

    public String[] tfaAuth(String verCode) throws Exception {
        JsonObject data1 = new JsonObject();
        data1.addProperty("language", "en_US");
        data1.addProperty("password", this.password);
        data1.addProperty("remember", "true");
        data1.addProperty("type", "auth");
        data1.addProperty("username", this.username);

        // 发送多因素认证请求
        HttpPut authPut = new HttpPut(AUTH_URL);
        authPut.setEntity(new StringEntity(data1.toString()));
        authPut.setHeader("Content-Type", "application/json");
        authPut.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
        authPut.setHeader("Accept-Language", "en-US,en;q=0.9");
        authPut.setHeader("Accept", "application/json, text/plain, */*");
        JsonObject authData = new JsonObject();
        authData.addProperty("type", "multifactor");
        authData.addProperty("code", verCode);
        authPut.setEntity(new StringEntity(authData.toString()));

        HttpResponse authResponse = session.execute(authPut, context);

        String authResponseBody = EntityUtils.toString(authResponse.getEntity());

        if (authResponseBody.contains("access_token")) {
            // 提取多因素认证后的令牌
            JsonObject authResponseData = JsonParser.parseString(authResponseBody).getAsJsonObject();
            String uri = authResponseData.getAsJsonObject("response").getAsJsonObject("parameters").get("uri").getAsString();
            Pattern pattern = Pattern.compile("access_token=([^&]*)&.*id_token=([^&]*)&");
            // 使用正则表达式匹配 URI 字符串
            Matcher matcher = pattern.matcher(uri);

            // 检查是否找到匹配项
            if (matcher.find()) {
                String token = matcher.group(1);
                String tokenId = matcher.group(2);
                return new String[]{token, tokenId};
            } else {
                throw new IllegalArgumentException("No match found");
            }
        } else if (authResponseBody.contains("auth_failure")) {
            return new String[]{"x", "auth_failure"};
        } else {
            return new String[]{"x", "unknown"};
        }
    }

    private String getEntitlementToken() {
        try {
            HttpPost request = new HttpPost(ENTITLEMENT_URL);
            request.setEntity(new StringEntity("{}"));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
            request.setHeader("Authorization", "Bearer " + this.accessToken);

            HttpResponse response = session.execute(request, context);

            // 解析 JSON 响应
            JsonObject responseData = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();

            return responseData.get("entitlements_token").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getEmailVerifed() {
        try {
            HttpGet request = new HttpGet(VERIFED_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
            request.setHeader("Authorization", "Bearer " + this.accessToken);
            HttpResponse response = session.execute(request, context);

            // 解析 JSON 响应
            JsonObject responseData = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();

            return responseData.get("emailVerified").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getUserInfo() {
        try {
            HttpGet request = new HttpGet(USERINFO_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)");
            request.setHeader("Authorization", "Bearer " + this.accessToken);
            // 发送 GET 请求
            HttpResponse response = session.execute(request, context);

            // 解析 JSON 响应
            JsonObject responseData = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();

            // 提取用户信息
            String sub = responseData.get("sub").getAsString();
            JsonObject acctData = responseData.getAsJsonObject("acct");
            String gameName = acctData.get("game_name").getAsString();
            String tagLine = acctData.get("tag_line").getAsString();
            long createdAtMillis = acctData.get("created_at").getAsLong();

            // 将时间戳转换为日期时间
            Instant createdAtInstant = Instant.ofEpochMilli(createdAtMillis);
            ZonedDateTime createdAt = createdAtInstant.atZone(ZoneId.systemDefault());
            String createdAtStr = createdAt.toString();

            // 提取限制信息
            JsonObject banData = responseData.getAsJsonObject("ban");
            List<JsonObject> restrictionsList = new ArrayList<>();

            if (banData.has("restrictions") && banData.get("restrictions").isJsonArray()) {
                // 获取限制数组
                JsonArray restrictionsArray = banData.getAsJsonArray("restrictions");

                // 将 JSON 数组转换为 List<JsonObject>
                for (JsonElement restrictionElement : restrictionsArray) {
                    if (restrictionElement.isJsonObject()) {
                        JsonObject restriction = restrictionElement.getAsJsonObject();
                        restrictionsList.add(restriction);
                    }
                }
            }

            String typeBan = "None"; // 默认为 "None"

            // 处理限制信息
            if (!restrictionsList.isEmpty()) {
                for (JsonObject restriction : restrictionsList) {
                    String type = restriction.get("type").getAsString();
                    if ("TIME_BAN".equals(type)) {
                        typeBan = "TIME_BAN";

                        // 处理时间戳并转换为日期时间
                        long expirationMillis = restriction.getAsJsonObject("dat").get("expirationMillis").getAsLong();
                        Instant expirationInstant = Instant.ofEpochMilli(expirationMillis);
                        expirationInstant.atZone(ZoneId.systemDefault());
                    } else if ("PERMANENT_BAN".equals(type)) {
                        typeBan = "PERMANENT_BAN";
                    }
                }
            }

            return new String[]{sub, gameName, tagLine, createdAtStr, typeBan};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRegion(Dummy ignored) {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("id_token", this.idToken);

            HttpPut request = new HttpPut(REGION_URL);
            request.setEntity(new StringEntity(data.toString()));
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + accessToken);
            request.setHeader("Content-Type", "application/json");

            HttpResponse response = session.execute(request, context);

            // 解析 JSON 响应
            JsonObject responseData = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
            // 提取 "affinities" 下的 "live" 字段值作为 Region
            return responseData.getAsJsonObject("affinities").get("live").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Dummy{}

    @Override
    public String toString() {
        return "RiotAuth{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", entitlement='" + entitlement + '\'' +
                ", sub='" + sub + '\'' +
                ", name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", creationData='" + creationData + '\'' +
                ", typeBan='" + typeBan + '\'' +
                ", region='" + region + '\'' +
                '}';
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEntitlement() {
        return entitlement;
    }

    public String getSub() {
        return sub;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getCreationData() {
        return creationData;
    }

    public String getTypeBan() {
        return typeBan;
    }

    public String getRegion() {
        return region;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void close() {
        try {
            if (session != null) {
                this.session.close();
            }
            this.session = null;
            this.context = null;
            this.accessToken = null;
            this.creationData = null;
            this.entitlement = null;
            this.idToken = null;
            this.region = null;
            this.sub = null;
            this.tag = null;
            this.name = null;
            this.typeBan = null;
        } catch (IOException e) {
            SpCoBot.LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
}