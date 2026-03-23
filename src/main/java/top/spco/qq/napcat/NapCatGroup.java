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
package top.spco.qq.napcat;

import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.User;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;

import java.util.Set;

public class NapCatGroup extends Group {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Member getOwner() {
        return null;
    }

    @Override
    public boolean quit() {
        return false;
    }

    @Override
    public MemberPermission botPermission() {
        return null;
    }

    @Override
    public Member botAsMember() {
        return null;
    }

    @Override
    public Member getMember(String id) {
        return null;
    }

    @Override
    public Set<Member> getMembers() {
        return Set.of();
    }

    @Override
    public void sendMessage(MessageChain message) {

    }

    @Override
    public String getId() {
        return "";
    }
}