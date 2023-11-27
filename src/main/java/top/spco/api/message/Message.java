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
package top.spco.api.message;

/**
 * 消息
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Message extends Codable{
    /**
     * 转为接近官方格式的字符串, 即 "内容". 如 At(member) + "test" 将转为 "@QQ test"
     *
     * @return 转化后的文本
     */
    String toMessageContext();
}