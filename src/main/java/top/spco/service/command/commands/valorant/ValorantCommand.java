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
import top.spco.service.command.*;
import top.spco.service.command.commands.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Valorant相关功能
 *
 * @author SpCo
 * @version 2.0.0
 * @since 1.3.0
 */
public class ValorantCommand extends AbstractCommand {
    {
        List<CommandParam> loginParams = new ArrayList<>();
        loginParams.add(new CommandParam("行为类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "login"));
        loginParams.add(new CommandParam("账号", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT));
        loginParams.add(new CommandParam("密码", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT));
        loginUsage = new CommandUsage(getLabels()[0], "登录拳头账户", loginParams);
    }

    public static CommandUsage loginUsage;

    @Override
    public String[] getLabels() {
        return new String[]{"valorant"};
    }

    @Override
    public String getDescriptions() {
        return "Valorant相关功能";
    }

    @Override
    public List<CommandUsage> getUsages() {

        List<CommandParam> shopParams = new ArrayList<>();
        shopParams.add(new CommandParam("行为类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "shop"));
        return List.of(loginUsage, new CommandUsage(getLabels()[0], "获取每日商店皮肤", shopParams));
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) throws CommandSyntaxException {
        switch (usageName) {
            case "登录拳头账户" -> {
                if (from instanceof Group) {
                    // 检测机器人是否有权限撤回用户发送的消息
                    if (PermissionsValidator.verifyBotPermissions(from, message, (NormalMember<?>) sender, false)) {
                        SpCoBot.getInstance().getMessageService().recall(message);
                        from.sendMessage("为防止您的密码泄露，请在私聊使用该命令。");
                    } else {
                        from.quoteReply(message, "为防止您的密码泄露，请撤回该消息并在私聊使用该命令。");
                    }
                    return;
                }
                String username = meta.argument(1);
                String password = meta.argument(2);
                auth(username, password, from, message, user);
            }
            case "获取每日商店皮肤" -> {
                String sql = "SELECT username, password, access_token, entitlements, uuid FROM valorant_user WHERE id = ?";
                try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
                    pstmt.setLong(1, user.getId());

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String username = rs.getString("username");
                            String password = rs.getString("password");
                            String accessToken = rs.getString("access_token");
                            String entitlements = rs.getString("entitlements");
                            String uuid = rs.getString("uuid");
                            if (accessToken.equals("null")) {
                                from.quoteReply(message, "请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n***请注意：为了将来实现自动登录功能，您的账号和密码将在服务器内以「明文形式」存储，如介意请以勿使用该命令，其他用户请务必开启双重验证。");
                                return;
                            }
                            try (CloseableHttpClient session = HttpClients.custom().build()) {
                                HttpGet request = new HttpGet("https://pd.ap.a.pvp.net/store/v2/storefront/" + uuid);
                                request.setHeader("Content-Type", "application/json");
                                request.setHeader("Authorization", "Bearer " + accessToken);
                                request.setHeader("X-Riot-Entitlements-JWT", entitlements);
                                String response = EntityUtils.toString(session.execute(request).getEntity());
                                JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                                if (json.has("httpStatus") && json.get("httpStatus").getAsInt() == 400) {
                                    from.quoteReply(message, "获取每日商店时发生异常，可能是由于登录数据过期。（" + json.get("message").getAsString() + "）\n请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n***请注意：为了将来实现自动登录功能，您的账号和密码将在服务器内以「明文形式」存储，如介意请以勿使用该命令，其他用户请务必开启双重验证。");
                                    return;
                                }
                                SkinsPanelLayoutContainer.SkinsPanelLayout store = SkinsPanelLayoutContainer.parseStore(response).getSkinsPanelLayout();
                                int remainingTime = store.getRemainingTime();
                                LinkedList<WeaponSkin> skins = store.getSkins();
                                var ms = SpCoBot.getInstance().getMessageService();
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
                                toReply.append("\n距商店刷新还剩" + (remainingTime / 3600.0) + "小时");
                                from.quoteReply(message, toReply);
                                return;
                            }
                        } else {
                            from.quoteReply(message, "请在 「私聊」 中使用 " + loginUsage.toString() + " 命令来登录。\n\n***请注意：为了将来实现自动登录功能，您的账号和密码将在服务器内以「明文形式」存储，如介意请以勿使用该命令，其他用户请务必开启双重验证。");
                            return;
                        }
                    }
                } catch (Exception e) {
                    from.handleException(message, e);
                    e.printStackTrace();
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
            e.printStackTrace();
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

    private void auth(String username, String password, Interactive<?> from, Message<?> message, BotUser user) {
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
        } catch (Exception e) {
            from.handleException(message, e);
            e.printStackTrace();
        }
    }
}