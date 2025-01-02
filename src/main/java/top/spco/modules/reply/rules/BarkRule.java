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

import top.spco.modules.reply.KeywordsCustomReply;

/**
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public class BarkRule extends KeywordsCustomReply {
    public BarkRule() {
        super("狗叫");
    }

    @Override
    public String reply(String text) {
        return "汪汪汪，主人我学了，我要吃骨头";
    }
}