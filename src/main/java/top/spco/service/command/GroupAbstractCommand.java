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
package top.spco.service.command;

/**
 * 这是一个抽象类，继承自 {@link AbstractCommand}，专门用于处理群组内的命令。<p>
 * 它覆盖了 {@link #getScope()} 方法，将命令的作用域限定为仅限群组。
 *
 * @author SpCo
 * @version 1.2.0
 * @since 1.2.0
 */
public abstract class GroupAbstractCommand extends AbstractCommand {
    @Override
    public final CommandScope getScope() {
        return CommandScope.ONLY_GROUP;
    }
}