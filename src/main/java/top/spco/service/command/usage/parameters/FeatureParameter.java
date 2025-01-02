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
package top.spco.service.command.usage.parameters;

import top.spco.core.feature.Feature;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 机器人功能的命令参数
 *
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public class FeatureParameter extends Parameter<Feature> {
    public FeatureParameter(String name, boolean isOptional, Feature defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public Feature parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String id = parser.readUnquotedString();
        if (!Feature.isFeatureIdAvailable(id)) {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("未注册的功能Id", parser);
        }
        return Feature.getFeatureById(id);

    }
}
