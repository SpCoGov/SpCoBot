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
package top.spco.service.command.usage.parameters;

import top.spco.core.module.AbstractModule;
import top.spco.core.module.ModuleManager;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 表示一个 {@link AbstractModule 模块} 的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class ModuleParameter extends Parameter<AbstractModule> {
    public ModuleParameter(String name, boolean isOptional, AbstractModule defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public AbstractModule parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String moduleName = parser.readUnquotedString();
        AbstractModule module = ModuleManager.getInstance().get(moduleName);
        if (module != null) {
            return module;
        }
        parser.setCursor(start);
        throw BuiltInExceptions.createWithContext("无效的结构名“" + moduleName + "”", parser);
    }
}