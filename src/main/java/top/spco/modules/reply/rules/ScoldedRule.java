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
package top.spco.modules.reply.rules;

import top.spco.modules.reply.CustomReply;
import top.spco.modules.reply.KeywordsCustomReply;

/**
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public class ScoldedRule extends KeywordsCustomReply {
    public ScoldedRule() {
        super("傻逼","智障","脑残","sb");
    }

    @Override
    public String reply(String text) {
        String[] strings = {"你出生后是不是被扔上去3次，但只被接住2次？", "你怎么可以这么对我说话", "网友，你可别惹我哦",
                "對阿，被你發現了", "今天这天气很适合聊天的说", "这是不对的", "没什么话题聊了是吗", "别发这么无聊的信息行不", "无聊的时候看看天，因为你妈也在天上看你"};
        return CustomReply.randomString(strings);
    }
}