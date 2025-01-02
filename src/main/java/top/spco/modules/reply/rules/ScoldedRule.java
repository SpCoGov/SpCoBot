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
import top.spco.modules.reply.KeywordsCustomReply;

/**
 * @author SpCo
 * @version 3.2.2
 * @since 3.2.0
 */
public class ScoldedRule extends KeywordsCustomReply {
    public ScoldedRule() {
        super("傻逼", "智障", "脑残", "sb", "操你妈");
    }

    @Override
    public String reply(String text) {
        if (text.contains("@") && !text.contains("@" + SpCoBot.getInstance().botId)) {
            return null;
        }
        if (text.contains("我是")) {
            return null;
        }
        String[] strings = {"你出生后是不是被扔上去3次，但只被接住2次？", "你怎么可以这么对我说话", "网友，你可别惹我哦",
                "對阿，被你發現了", "今天这天气很适合聊天的说", "这是不对的", "没什么话题聊了是吗", "别发这么无聊的信息行不",
                "无聊的时候看看天，因为你妈也在天上看你", "闪了，你这么猥琐，跟你聊天会降低人品的", "我想啐你，又怕玷污了我的唾沫。——莎士比亚",
                "别对着我叫，我小时候被狗吓过。", "你上辈子是条内裤吧，这么爱装比", "前男友有尿毒症吗 嘴巴这么毒？",
                "你一开口，我就知道结果，你就不能搞点新创意出来嘛", "小孙子你可真jb闲。\uD83D\uDC74也很闲，于是把你的短小家伙嘎了油爆。我把它的两面炸至金黄，再裹上面包糠，给隔壁小孩。瞧把这孩给吓的，一把把你小\uD83D\uDC14儿丢粪坑里了。不知道咋滴，你这\uD83D\uDC14儿在管道里瞎jb转，结果转到一撤硕里。老八来吃粑粑，本来吃挺香的，结果看见你那玩意，哇哇的呕啊。",
                "要不是我当年拒绝了你妈，你会长的比现在好看的多", "我即将为你母亲做移植手术 稍后你会看到母猪的子宫接二连三的进入你母亲子宫囤积能量"};
        return CustomReply.randomString(strings);
    }
}