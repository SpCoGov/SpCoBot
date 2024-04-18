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
import top.spco.modules.reply.ExactCustomReply;

/**
 * Created on 2024/04/18 23:36
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class CallFatherRule extends ExactCustomReply {
    public CallFatherRule() {
        super("叫爸爸", "叫我爸爸");
    }

    @Override
    public String reply(String text) {
        String[] strings = {"给我个叫的理由", "爸爸","叫你网友就可以了","我习惯直呼名字，网友","不叫","凭什么叫啊","为什么","好的，爸爸","","",""};
        return CustomReply.randomString(strings);
    }
}