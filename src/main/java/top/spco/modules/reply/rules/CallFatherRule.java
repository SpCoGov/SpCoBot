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
package top.spco.modules.reply.rules;

import top.spco.SpCoBot;
import top.spco.modules.reply.CustomReply;
import top.spco.modules.reply.ExactCustomReply;

/**
 * @author SpCo
 * @version 3.2.2
 * @since 3.2.0
 */
public class CallFatherRule extends ExactCustomReply {
    public CallFatherRule() {
        super("叫爸爸", "叫我爸爸");
    }

    @Override
    public String reply(String text) {
        if (text.contains("@") && !text.contains("@" + SpCoBot.getInstance().botId)) {
            return null;
        }
        String[] strings = {"给我个叫的理由", "爸爸","叫你网友就可以了","我习惯直呼名字，网友","不叫","凭什么叫啊","为什么","好的，爸爸",
                "臭表子瞧你那黑逼臭成什么几把样子了也就你家楼下的老黄狗愿意舔你那留绿汁的逼啊，还“叫爸爸”？"};
        return CustomReply.randomString(strings);
    }
}