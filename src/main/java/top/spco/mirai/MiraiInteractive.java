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
package top.spco.mirai;

import net.mamoe.mirai.contact.Contact;
import top.spco.api.Interactive;
import top.spco.api.message.Message;

import java.io.File;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiInteractive extends Interactive<Contact> {
    protected MiraiInteractive(Contact interactive) {
        super(interactive);
    }

    @Override
    public long getId() {
        return this.wrapped().getId();
    }

    @Override
    public void sendMessage(String message) {
        this.wrapped().sendMessage(message);
    }

    @Override
    public void sendMessage(Message<?> message) {
        this.wrapped().sendMessage(((MiraiMessage) message).wrapped());
    }

    @Override
    public void sendImage(File image) {
        Contact.uploadImage(this.wrapped(), image);
    }
}