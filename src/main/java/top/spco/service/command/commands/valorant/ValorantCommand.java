/*
 * Copyright 2023 SpCo
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.spco.SpCoBot;
import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.chat.Chat;
import top.spco.service.chat.ChatBuilder;
import top.spco.service.chat.ChatType;
import top.spco.service.chat.Stage;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
import top.spco.service.command.usage.parameters.StringParameter;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Valorant相关功能
 *
 * @author SpCo
 * @version 3.0.3
 * @since 1.3.0
 */
@CommandMarker
public class ValorantCommand extends AbstractCommand {
    public static Usage loginUsage;

    @Override
    public String[] getLabels() {
        return new String[]{"valorant"};
    }

    @Override
    public String getDescriptions() {
        return "Valorant相关功能";
    }

    @Override
    public List<Usage> getUsages() {
        SpecifiedParameterSet set = new SpecifiedParameterHelper("行为类型", false).add("login", "shop").build();
        return List.of(
                new UsageBuilder(getLabels()[0], "获取每日商店皮肤")
                        .add(set.get("login"))
                        .add(new StringParameter("账号", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .add(new StringParameter("密码", false, null, StringParameter.StringType.QUOTABLE_PHRASE)).build(),
                new UsageBuilder(getLabels()[0], "获取每日商店皮肤")
                        .add(set.get("shop")).build());
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        switch (usageName) {
            case "登录拳头账户" -> {
                if (from instanceof Group) {
                    // 检测机器人是否有权限撤回用户发送的消息
                    if (PermissionsValidator.verifyBotPermissions(from, message, (NormalMember<?>) sender, false)) {
                        SpCoBot.getInstance().getMessageService().recall(message.getSource());
                        from.sendMessage("为防止您的密码泄露，请在私聊使用该命令。");
                    } else {
                        from.quoteReply(message, "为防止您的密码泄露，请撤回该消息并在私聊使用该命令。");
                    }
                    return;
                }
                String username = (String) meta.getParams().get("账号");
                String password = (String) meta.getParams().get("密码");

                try {
                    RiotAuth riot = new RiotAuth(username, password);
                    String[] tokens = riot.authorize();
                    if (tokens[0].equals("x")) {
                        if (tokens[1].equals("2fa_auth")) {
                            ChatType ct = from instanceof Friend ? ChatType.FRIEND : ChatType.GROUP_TEMP;
                            Chat authChat = new ChatBuilder(ct, from)
                                    .addStage(new Stage(() -> "需要验证，请发送您收到的6位验证码。", (chat, bot1, source, sender1, message1, time1) -> {
                                        String varCode = message1.toMessageContext();
                                        try {
                                            String[] authCode = riot.tfaAuth(varCode);
                                            tokens[0] = authCode[0];
                                            tokens[1] = authCode[1];
                                            if (tokens[0].equals("x")) {
                                                from.quoteReply(message, "与拳头官方服务验证时发生异常: " + tokens[1]);
                                                return;
                                            }
                                            handleToken(riot, tokens, user.getId(), from, message1);
                                        } catch (Exception e) {
                                            from.handleException(message, e);
                                        } finally {
                                            chat.stop();
                                        }
                                    })).build();
                            authChat.start();
                        } else {
                            from.quoteReply(message, "与拳头官方服务验证时发生异常: " + tokens[1]);
                        }
                        return;
                    }
                    handleToken(riot, tokens, user.getId(), from, message);
                    riot.close();
                    return;
                } catch (Exception e) {
                    from.handleException(message, e);
                    SpCoBot.LOGGER.error(e);
                    return;
                }
            }
            case "获取每日商店皮肤" -> {
                // 从数据库中获取登录信息和账号密码
                String sql = "SELECT username, password, access_token, entitlements, uuid FROM valorant_user WHERE id = ?";
                try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
                    pstmt.setLong(1, user.getId());
                    ResultSet rs = pstmt.executeQuery();
                    // 如果数据库中没有该用户的数据
                    if (!rs.next()) {
                        from.quoteReply(message, "请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n***请注意：您的账号和密码将在服务器内以「明文形式」存储，如介意请以勿使用该命令，其他用户请务必开启双重验证。");
                        return;
                    }
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String accessToken = rs.getString("access_token");
                    String entitlements = rs.getString("entitlements");
                    String uuid = rs.getString("uuid");
                    rs.close();
                    // 如果该用户的登录数据为null
                    if (accessToken.equals("null")) {
                        from.quoteReply(message, "请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n***请注意：您的账号和密码将在服务器内以「明文形式」存储，如介意请以勿使用该命令，其他用户请务必开启双重验证。");
                        return;
                    }

                    // 开始获取每日商店数据
                    try (CloseableHttpClient session = HttpClients.custom().build()) {
                        HttpGet request = new HttpGet("https://pd.ap.a.pvp.net/store/v2/storefront/" + uuid);
                        request.setHeader("Content-Type", "application/json");
                        request.setHeader("Authorization", "Bearer " + accessToken);
                        request.setHeader("X-Riot-Entitlements-JWT", entitlements);
                        String response = EntityUtils.toString(session.execute(request).getEntity());
                        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

                        var ms = SpCoBot.getInstance().getMessageService();
                        // 登录数据失效
                        if (json.has("httpStatus") && json.get("httpStatus").getAsInt() == 400) {
                            from.quoteReply(message, "获取每日商店时发生异常，可能是由于登录数据过期。（" + json.get("message").getAsString() + "）\n\n即将开始自动登录。");
                            // 开始自动登录
                            try {
                                RiotAuth riot = new RiotAuth(username, password);
                                String[] tokens = riot.authorize();
                                // 登录失败
                                if (tokens[0].equals("x")) {
                                    // 检查是否是由于开启了两步验证需要验证码而导致的登录失败
                                    if (tokens[1].equals("2fa_auth")) {
                                        // 创建私聊对话 接受验证码
                                        ChatType ct = sender instanceof Friend || ((NormalMember<?>) sender).isFriend() ? ChatType.FRIEND : ChatType.GROUP_TEMP;
                                        Chat authChat = new ChatBuilder(ct, sender)
                                                .addStage(new Stage(() -> "需要验证，请发送您收到的6位验证码。", (chat, bot1, source, sender1, message1, time1) -> {
                                                    String varCode = message1.toMessageContext();
                                                    System.out.println("收到验证码：" + varCode);
                                                    try {
                                                        String[] authCode = riot.tfaAuth(varCode);
                                                        tokens[0] = authCode[0];
                                                        tokens[1] = authCode[1];
                                                        if (tokens[0].equals("x")) {
                                                            // 验证码错误或其他
                                                            sender.quoteReply(message1, "与拳头官方服务验证时发生异常: " + tokens[1]);
                                                            return;
                                                        }
                                                        handleToken(riot, tokens, user.getId(), sender1, message1);
                                                        sender1.quoteReply(message1, "登录信息已更新，重新开始获取每日商店信息。");
                                                        // 登录信息已更新 重新开始获取每日商店信息
                                                        try (CloseableHttpClient conn = HttpClients.custom().build()) {
                                                            HttpGet newRequest = new HttpGet("https://pd.ap.a.pvp.net/store/v2/storefront/" + riot.getSub());
                                                            newRequest.setHeader("Content-Type", "application/json");
                                                            newRequest.setHeader("Authorization", "Bearer " + riot.getAccessToken());
                                                            newRequest.setHeader("X-Riot-Entitlements-JWT", riot.getEntitlement());
                                                            var newResponse = EntityUtils.toString(conn.execute(newRequest).getEntity());
                                                            riot.close();

                                                            SkinsPanelLayoutContainer.SkinsPanelLayout store = SkinsPanelLayoutContainer.parseStore(newResponse).getSkinsPanelLayout();
                                                            int remainingTime = store.getRemainingTime();
                                                            LinkedList<WeaponSkin> skins = store.getSkins();

                                                            Message<?> toReply = ms.asMessage("您今日的每日商店为：\n");
                                                            for (var skin : skins) {
                                                                try {
                                                                    toReply.append(skin.getDisplayName() + " " + skin.getCost() + "VP");
                                                                    String filePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "skinIcons" + File.separator + skin.getUuid() + ".png";
                                                                    downloadFile(skin.getaIconURL(), filePath);
                                                                    toReply.append(ms.toImage(new File(filePath), from));
                                                                } catch (IOException e) {
                                                                    toReply.append("(皮肤图片下载失败)");
                                                                }
                                                                toReply.append("\n");
                                                            }
                                                            toReply.append("\n距商店刷新还剩" + convertSecondsToDuration(remainingTime));
                                                            from.quoteReply(message, toReply);
                                                        }
                                                    } catch (Exception e) {
                                                        sender1.handleException(message1, e);
                                                        SpCoBot.LOGGER.error(e);
                                                    }
                                                    chat.stop();
                                                })).build();
                                        authChat.start();
                                    } else {
                                        from.quoteReply(message, "与拳头官方服务验证时发生异常: " + tokens[1]);
                                    }
                                    return;
                                }
                                // 登录信息已更新 重新开始获取每日商店信息
                                handleToken(riot, tokens, user.getId(), from, message);

                                HttpGet newRequest = new HttpGet("https://pd.ap.a.pvp.net/store/v2/storefront/" + riot.getSub());
                                newRequest.setHeader("Content-Type", "application/json");
                                newRequest.setHeader("Authorization", "Bearer " + riot.getAccessToken());
                                newRequest.setHeader("X-Riot-Entitlements-JWT", riot.getEntitlement());
                                response = EntityUtils.toString(session.execute(newRequest).getEntity());
                                riot.close();
                            } catch (Exception e) {
                                from.quoteReply(message, "自动登录失败，请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n" + e.getMessage());
                                SpCoBot.LOGGER.error(e);
                                return;
                            }
                        }

                        // 处理返回的每日商店数据
                        SkinsPanelLayoutContainer.SkinsPanelLayout store = SkinsPanelLayoutContainer.parseStore(response).getSkinsPanelLayout();
                        int remainingTime = store.getRemainingTime();
                        LinkedList<WeaponSkin> skins = store.getSkins();
                        Message<?> toReply = ms.asMessage("您今日的每日商店为：\n");
                        for (var skin : skins) {
                            try {
                                toReply.append(skin.getDisplayName() + " " + skin.getCost() + "VP");
                                String filePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "skinIcons" + File.separator + skin.getUuid() + ".png";
                                downloadFile(skin.getaIconURL(), filePath);
                                toReply.append(ms.toImage(new File(filePath), from));
                            } catch (IOException e) {
                                toReply.append("(皮肤图片下载失败)");
                            }
                            toReply.append("\n");
                        }
                        toReply.append("\n距商店刷新还剩" + convertSecondsToDuration(remainingTime));
                        from.quoteReply(message, toReply);
                        return;
                    }
                } catch (Exception e) {
                    from.handleException(message, e);
                    SpCoBot.LOGGER.error(e);
                }
            }
        }
    }

    private void handleToken(RiotAuth riot, String[] tokens, long userId, Interactive<?> from, Message<?> message) {
        try {
            riot.parse(tokens);
            String sql = "INSERT INTO valorant_user (id, username, password, access_token, entitlements, uuid, name, tag, create_data, ban_type, region) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET username = excluded.username, password = excluded.password, access_token = excluded.access_token, entitlements = excluded.entitlements, " +
                    "uuid = excluded.uuid, name = excluded.name, tag = excluded.tag, create_data = excluded.create_data, ban_type = excluded.ban_type, region = excluded.region";
            try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
                pstmt.setLong(1, userId);
                pstmt.setString(2, riot.getUsername());
                pstmt.setString(3, riot.getPassword());
                pstmt.setString(4, riot.getAccessToken());
                pstmt.setString(5, riot.getEntitlement());
                pstmt.setString(6, riot.getSub());
                pstmt.setString(7, riot.getName());
                pstmt.setString(8, riot.getTag());
                pstmt.setString(9, riot.getCreationData());
                pstmt.setString(10, riot.getTypeBan());
                pstmt.setString(11, riot.getRegion());

                pstmt.executeUpdate();
            }
            from.quoteReply(message, "与拳头官网服务验证成功");
        } catch (Exception e) {
            from.handleException(message, e);
            SpCoBot.LOGGER.error(e);
        }
    }

    /**
     * 下载文件
     *
     * @param url      下载文件的目标地址
     * @param filePath 文件的保存地址
     */
    private void downloadFile(URL url, String filePath) throws IOException {
        // 首先检查文件是否存在
        if (!checkFileExists(filePath)) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            File file = new File(filePath);

            // 确保文件的父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                // 创建父目录及所有必要的上层目录
                parentDir.mkdirs();
            }

            // 打开连接以开始下载
            try (BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } finally {
                httpURLConnection.disconnect();
            }
        }
    }

    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    private static String convertSecondsToDuration(int seconds) {
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        StringBuilder duration = new StringBuilder();
        if (hours > 0) {
            duration.append(hours).append("小时");
        }
        if (minutes > 0) {
            duration.append(minutes).append("分钟");
        }
        if (seconds > 0) {
            duration.append(seconds).append("秒");
        }

        return duration.toString();
    }
}