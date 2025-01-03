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
package top.spco.modules;

import top.spco.SpCoBot;
import top.spco.core.config.ValorantGroupSettings;
import top.spco.core.module.AbstractModule;
import top.spco.events.MessageEvents;
import top.spco.util.FileManipulation;

import java.io.File;
import java.sql.SQLException;
import java.util.Random;

/**
 * 自动“同意”打瓦
 *
 * @author SpCo
 * @version 4.0.0
 * @since 2.0.0
 */
public class ValorantResponder extends AbstractModule {
    public ValorantResponder() {
        super("ValorantResponder");
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public void init() {
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (!isActive()) {
                return;
            }
            try {
                if (!isAvailable(source)) {
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (source.getId() == SpCoBot.getInstance().getSettings().getLongProperty(ValorantGroupSettings.VALORANT_GROUP) || source.getId() == SpCoBot.getInstance().testGroupId) {
                if (message.toMessageContext().equals("瓦") || message.toMessageContext().endsWith("瓦吗") || message.toMessageContext().endsWith("打不打瓦")
                        || message.toMessageContext().endsWith(" 瓦") || message.toMessageContext().equals("有无瓦") || message.toMessageContext().equals("有没有瓦")
                        || message.toMessageContext().endsWith("瓦不瓦")) {
                    FileManipulation file = new FileManipulation(SpCoBot.configFolder + File.separator + "valorant.spco");
                    String str = getRandomLine(file.readFromFile());
                    if (str == null) {
                        return;
                    }
                    source.quoteReply(message, str);
                }
            }
        });
    }

    private String getRandomLine(String text) {
        String[] lines = text.split("\\r?\\n"); // 以换行符分割文本
        Random random = new Random();

        if (lines.length > 0) {
            int randomIndex = random.nextInt(lines.length); // 生成随机索引
            return lines[randomIndex]; // 返回随机选取的行
        } else {
            return null; // 如果文本为空，则返回空字符串
        }
    }
}