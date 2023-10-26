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
package top.spco.mirai;

import net.mamoe.mirai.message.code.MiraiCode;
import top.spco.base.api.User;
import top.spco.base.api.message.Message;

/**
 * <p>
 * Created on 2023/10/26 0026 14:51
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class MiraiUser implements User {
    private final net.mamoe.mirai.contact.User user;

    public MiraiUser(net.mamoe.mirai.contact.User user) {
        this.user = user;
    }

    @Override
    public long getId() {
        return this.user.getId();
    }

    @Override
    public String getRemark() {
        return this.user.getRemark();
    }

    @Override
    public void sendMessage(String message) {
        user.sendMessage(message);
    }

    @Override
    public void sendMessage(Message message) {
        this.user.sendMessage(MiraiCode.deserializeMiraiCode(message.serialize()));
    }

    @Override
    public void nudge() {
        this.user.nudge();
    }
}