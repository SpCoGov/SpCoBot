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

import top.spco.modules.reply.CustomReply;
import top.spco.modules.reply.KeywordsCustomReply;

/**
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public class BotRule extends KeywordsCustomReply {

    public BotRule() {
        super("机器人","bot","BOT","Bot");
    }

    @Override
    public String reply(String text) {
        String[] strings = {"你才是机器人，你全家都是机器人！", "你不会真的相信这个世界上有机器人吧", "木有鸡又木有逼",
                "在", "奴才到","机器人是头猪","笨蛋","海绵无敌","我是猪！不是人","你才是机器人，你们全家都是机器人","机器人是世界上最瓜的瓜娃子",
                "小雪上门服务电话110","一群无知的人类","他是个傻逼叽叽喳喳","我不是机器人，我是SpCoBot。","机器人是傻逼","机器人","我主人是海绵",
                "破机器","他死了","二逼","猪他爹","机器人闭嘴","系统故障 死机中...","你大爷、、、","是个傻逼","我是傻逼 傻逼 废铁", "什么事呀，我正忙着呢","啥事说吧，SpCoBot听着呢"};
        return CustomReply.randomString(strings);
    }
}