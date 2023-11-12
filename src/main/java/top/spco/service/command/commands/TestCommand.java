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
package top.spco.service.command.commands;

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.chat.*;
import top.spco.service.command.BaseCommand;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * <p>
 * Created on 2023/11/6 0006 12:32
 * <p>
 *
 * @author SpCo
 * @version 2.0
 * @since 1.1
 */
public final class TestCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"test"};
    }

    @Override
    public String getDescriptions() {
        return "测试";
    }

    @Override
    public UserPermission needPermission() {
        return super.needPermission();
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args) {
        try {
            StringBuilder sb = new StringBuilder();
            Chat chat = new ChatBuilder(ChatType.FRIEND, from).addStage(new Stage(() -> "请输入你好", (chat1, bot1, source, sender1, message1, time1) -> {
                if (message1.toMessageContext().equals("你好")) {
                    sb.append("你好，").append(sender1.getId());
                    chat1.next();
                    return;
                }
                chat1.replay();
            })).addStage(new Stage(() -> "请随意输入文本", (chat1, bot1, source, sender1, message1, time1) -> {
                sb.append("\n").append(message1.toMessageContext());
                chat1.next();
            })).addStage(new Stage(() -> "最终文本为\n" + sb + "\n输入确定即可发送，输入取消退出，您可以重新发送/test命令以重新编辑", (chat1, bot1, source, sender1, message1, time1) -> {
                switch (message1.toMessageContext()) {
                    case "取消" -> chat1.stop();
                    case "确定" -> {
                        source.sendMessage(sb.toString());
                        chat1.next();
                    }
                    default -> chat1.replay();
                }
            })).build();
            chat.start();
        } catch (ChatTypeMismatchException e) {
            from.handleException("创建会话失败", e);
        }
    }
}