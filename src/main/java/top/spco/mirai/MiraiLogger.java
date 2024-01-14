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

import top.spco.api.Logger;
import top.spco.api.Wrapper;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiLogger extends Wrapper<net.mamoe.mirai.utils.MiraiLogger> implements Logger {
    protected MiraiLogger(net.mamoe.mirai.utils.MiraiLogger logger) {
        super(logger);
    }

    @Override
    public void debug(String message) {
        this.wrapped().debug(message);
    }

    @Override
    public void debug(Throwable e) {
        this.wrapped().debug(e);
    }

    @Override
    public void debug(String message, Throwable e) {
        this.wrapped().debug(message, e);
    }

    @Override
    public void info(String message) {
        this.wrapped().info(message);
    }

    @Override
    public void info(Throwable e) {
        this.wrapped().info(e);
    }

    @Override
    public void info(String message, Throwable e) {
        this.wrapped().info(message, e);
    }

    @Override
    public void warn(String message) {
        this.wrapped().warning(message);
    }

    @Override
    public void warn(Throwable e) {
        this.wrapped().warning(e);
    }

    @Override
    public void warn(String message, Throwable e) {
        this.wrapped().warning(message, e);
    }

    @Override
    public void error(String message) {
        this.wrapped().error(message);
    }

    @Override
    public void error(Throwable e) {
        this.wrapped().error(e);
    }

    @Override
    public void error(String message, Throwable e) {
        this.wrapped().error(message, e);
    }
}