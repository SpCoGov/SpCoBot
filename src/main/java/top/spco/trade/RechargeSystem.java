/*
 * Copyright 2025 SpCo
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
package top.spco.trade;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import top.spco.SpCoBot;
import top.spco.core.config.PayApiSettings;
import top.spco.trade.alipay.*;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.user.UserFetchException;
import top.spco.user.UserOperationException;
import top.spco.util.TimeUtil;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class RechargeSystem {
    private static RechargeSystem instance;
    private final Config WECHAT_PAY_CONFIG = new RSAAutoCertificateConfig.Builder()
            .merchantId(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_MERCHANT_ID))
            .privateKeyFromPath(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_PRIVATE_KEY_PATH))
            .merchantSerialNumber(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_MERCHANT_SERIAL_NUMBER))
            .apiV3Key(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_API_V3_KEY))
            .build();
    private final NativePayService WECHAT_PAY = new NativePayService.Builder().config(WECHAT_PAY_CONFIG).build();
    private final AlipayTradeService ALIPAY = new AlipayTradeServiceImpl.ClientBuilder().build();
    private final byte[] aesKey = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_API_V3_KEY).getBytes();
    private static final int TAG_LENGTH_BIT = 128;

    private RechargeSystem() throws IOException {
        int port = SpCoBot.getInstance().getSettings().getIntegerProperty(PayApiSettings.NOTIFY_SERVER_PORT);
        if (port > 65535 || port < 1) {
            throw new RuntimeException("The port range is 1~65535.");
        }
        HttpServer notifyServer = HttpServer.create(new InetSocketAddress(port), 0);
        notifyServer.createContext("/wechat_pay", new WechatPayNotifyServerHandler());
        notifyServer.createContext("/alipay", new AlipayNotifyServerHandler());

        notifyServer.setExecutor(null);
        notifyServer.start();
    }

    public static RechargeSystem getInstance() throws IOException {
        if (instance == null) {
            instance = new RechargeSystem();
        }
        return instance;
    }

    private Map<String, String> parseParams(String body) {
        Map<String, String> params = new TreeMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                params.put(key, URLDecoder.decode(value, StandardCharsets.UTF_8));
            }
        }
        return params;
    }

    private class AlipayNotifyServerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String, String> params = parseParams(body);
                // 移除sign和sign_type参数
                String sign = params.remove("sign");
                String signType = params.remove("sign_type");

                // URL decode并排序参数
                Map<String, String> sortedParams = new TreeMap<>(params);

                // 构造待签名字符串
                StringBuilder content = new StringBuilder();
                sortedParams.forEach((key, value) -> content.append(key).append("=").append(value).append("&"));
                if (!content.isEmpty()) {
                    content.deleteCharAt(content.length() - 1); // 删除最后一个&
                }

                // 验证签名
                boolean isVerified = AlipaySignature.rsaCheck(content.toString(), sign, AlipayConfig.getAlipayPublicKey(), "UTF-8", signType);
                if (!isVerified) {
                    return;
                }
                if (!params.get("app_id").equals(AlipayConfig.getAppid())) {
                    return;
                }
                String tradeNo = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");
                String tradeStateDB = queryTradeStateFromDB(tradeNo);
                if (tradeStateDB == null) {
                    return;
                }
                if (tradeStatus.equals("TRADE_SUCCESS") && tradeStateDB.equals("unpaid")) {

                    Long userId = SpCoBot.getInstance().getDataBase().selectLong("trade", "user", "id", tradeNo);
                    BotUser user = BotUsers.get(userId);
                    String buyerPayAmount = params.get("buyer_pay_amount");
                    int decimalIndex = buyerPayAmount.indexOf(".");
                    String integerPart = (decimalIndex == -1) ? buyerPayAmount : buyerPayAmount.substring(0, decimalIndex);
                    int amountInt = Integer.parseInt(integerPart);
                    Objects.requireNonNull(user).recharge(tradeNo, amountInt);
                }
                StringBuilder response = new StringBuilder();
                response.append("success");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
            } catch (IOException | AlipayApiException | SQLException | UserFetchException | UserOperationException e) {
                SpCoBot.LOGGER.error("Failed to process notify", e);
            }
        }
    }

    private class WechatPayNotifyServerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(body, JsonObject.class);
                if (exchange.getRequestHeaders().containsKey("Wechatpay-timestamp")) {
                    JsonObject resource = json.get("resource").getAsJsonObject();
                    byte[] nonce = resource.get("nonce").getAsString().getBytes();
                    byte[] associatedData = resource.get("associated_data").getAsString().getBytes();
                    String ciphertext = resource.get("ciphertext").getAsString();
                    JsonObject decrypted = gson.fromJson(decryptToString(associatedData, nonce, ciphertext), JsonObject.class);
                    String tradeNo = decrypted.get("out_trade_no").getAsString();
                    String tradeState = decrypted.get("trade_state").getAsString();
                    String tradeStateDB = queryTradeStateFromDB(tradeNo);

                    if (tradeStateDB == null) {
                        return;
                    }
                    if (tradeState.equals("SUCCESS") && tradeStateDB.equals("unpaid")) {
                        Long userId = SpCoBot.getInstance().getDataBase().selectLong("trade", "user", "id", tradeNo);
                        BotUser user = BotUsers.get(userId);
                        Objects.requireNonNull(user).recharge(decrypted);
                        StringBuilder response = new StringBuilder();
                        response.append("success");
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.toString().getBytes());
                        os.close();
                    }
                }
            } catch (IOException | SQLException | UserFetchException | UserOperationException e) {
                SpCoBot.LOGGER.error("Failed to process notify", e);
            } catch (GeneralSecurityException e) {
                SpCoBot.LOGGER.error("Received a notification that the SecretKey is incorrect.");
            } catch (JsonSyntaxException ignored) {
            }
        }
    }

    private String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData);

            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public WechatPayTrade createWechatPay(BotUser caller, int totalAmount, String description) {
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Recharge amount must be positive.");
        }
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(totalAmount);
        request.setAmount(amount);
        request.setAppid(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_APP_ID));
        request.setMchid(SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.WECHAT_PAY_MERCHANT_ID));
        request.setDescription(description);
        String tradeNo = genTradeNo(caller, PaymentMethod.WECHAT_PAY);
        request.setOutTradeNo(tradeNo);
        String url = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.NOTIFY_URL);
        if (url.endsWith("/")) {
            url += "wechat_pay";
        } else {
            url += "/wechat_pay";
        }
        request.setNotifyUrl(url);
        PrepayResponse response = WECHAT_PAY.prepay(request);
        try {
            SpCoBot.getInstance().getDataBase().insertData("insert into trade(id,user,date,time,amount) values (?,?,?,?,?)",
                    tradeNo, caller.getId(), TimeUtil.today(), System.currentTimeMillis(), totalAmount);
            return new WechatPayTrade(tradeNo, caller.getId(), response.getCodeUrl(), totalAmount);
        } catch (SQLException e) {
            return null;
        }
    }

    public AlipayTrade createAlipay(BotUser caller, int totalAmount, String description) {
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("Recharge amount must be positive.");
        }
        String tradeNo = genTradeNo(caller, PaymentMethod.ALIPAY);
        String url = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.NOTIFY_URL);
        if (url.endsWith("/")) {
            url += "alipay";
        } else {
            url += "/alipay";
        }
        AlipayRequestBuilder builder = new AlipayRequestBuilder()
                .setOutTradeNo(tradeNo)
                .setSubject(description).setTotalAmount(totalAmount + "")
                .setStoreId("spcobot")
                .setNotifyUrl(url);
        AlipayF2FPrecreateResult result = ALIPAY.tradePrecreate(builder);
        if (Objects.requireNonNull(result.getTradeStatus()) == TradeStatus.SUCCESS) {
            AlipayTradePrecreateResponse response = result.getResponse();
            try {
                SpCoBot.getInstance().getDataBase().insertData("insert into trade(id,user,date,time,amount) values (?,?,?,?,?)",
                        tradeNo, caller.getId(), TimeUtil.today(), System.currentTimeMillis(), totalAmount);
                return new AlipayTrade(tradeNo, caller.getId(), response.getQrCode(), totalAmount);
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private static String genTradeNo(BotUser caller, PaymentMethod method) {
        return method.getCode() + caller.getId() + "_" + System.currentTimeMillis() + (new Random().nextInt(9000) + 1000);
    }

    private static String queryTradeStateFromDB(String tradeNo) throws SQLException {
        return SpCoBot.getInstance().getDataBase().selectString("trade", "state", "id", tradeNo);
    }
}