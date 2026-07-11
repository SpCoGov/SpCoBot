/*
 * Copyright 2026 SpCo
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
import top.spco.api.message.ImageMessage;
import top.spco.api.message.MessageChain;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.StringParameter;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
import top.spco.user.BotUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@CommandMarker
public class SendCommand extends AbstractCommand {

    @Override
    public String[] getLabels() {
        return new String[]{"send"};
    }

    @Override
    public String getDescriptions() {
        return "发送";
    }

    @Override
    public List<Usage> getUsages() {
        SpecifiedParameterSet set = new SpecifiedParameterHelper("操作类型", false).add("image").build();
        return List.of(
                new UsageBuilder(getLabels()[0], "发送图片")
                        .add(set.get("image"))
                        .add(new StringParameter("url", false, "", StringParameter.StringType.GREEDY_PHRASE))
                        .build()
        );
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, MessageChain message, long time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        switch (usageName) {
            case "发送图片" -> {
                try {
                    URL url = new URL((String) meta.getParams().get("url"));
                    from.quoteReply(message, new ImageMessage(url).toMessageChain());
                } catch (MalformedURLException e) {
                    from.quoteReply(message, "URL格式错误");
                }
            }
        }

    }
}