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

import top.spco.base.api.Logger;

/**
 * <p>
 * Created on 2023/10/25 0025 19:32
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
record MiraiLogger(net.mamoe.mirai.utils.MiraiLogger miraiLogger) implements Logger {

    @Override
    public void debug(String message) {
        this.miraiLogger.debug(message);
    }

    @Override
    public void debug(Throwable e) {
        this.miraiLogger.debug(e);
    }

    @Override
    public void debug(String message, Throwable e) {
        this.miraiLogger.debug(message, e);
    }

    @Override
    public void info(String message) {
        this.miraiLogger.info(message);
    }

    @Override
    public void info(Throwable e) {
        this.miraiLogger.info(e);
    }

    @Override
    public void info(String message, Throwable e) {
        this.miraiLogger.info(message, e);
    }

    @Override
    public void warn(String message) {
        this.miraiLogger.warning(message);
    }

    @Override
    public void warn(Throwable e) {
        this.miraiLogger.warning(e);
    }

    @Override
    public void warn(String message, Throwable e) {
        this.miraiLogger.warning(message, e);
    }

    @Override
    public void error(String message) {
        this.miraiLogger.error(message);
    }

    @Override
    public void error(Throwable e) {
        this.miraiLogger.error(e);
    }

    @Override
    public void error(String message, Throwable e) {
        this.miraiLogger.error(message, e);
    }
}