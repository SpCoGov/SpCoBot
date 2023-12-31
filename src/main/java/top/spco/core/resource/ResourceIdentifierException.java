/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.core.resource;

import top.spco.util.StringEscapeUtils;

/**
 * 用于表示资源标识符操作中的异常情况的异常类
 *
 * @author Fabric
 * @version 0.1.0
 * @since 0.1.0
 */
public class ResourceIdentifierException extends RuntimeException {
    public ResourceIdentifierException(String message) {
        super(StringEscapeUtils.escapeJava(message));
    }

    public ResourceIdentifierException(String message, Throwable throwable) {
        super(StringEscapeUtils.escapeJava(message), throwable);
    }
}