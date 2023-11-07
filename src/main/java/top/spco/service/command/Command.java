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
package top.spco.service.command;

import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;
import top.spco.events.CommandEvents;
import top.spco.service.chat.Chat;
import top.spco.service.chat.Stage;
import top.spco.service.command.commands.HelpCommand;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;

/**
 * {@link Command 命令}是一种用户与机器人交互的方式。与{@link Chat 对话}可能有以下不同：
 * <ul>
 * <li>{@link Chat 对话}传递参数时可以有明确的引导</li>
 * <li>{@link Chat 对话}适合传递内容较大的参数</li>
 * <li>{@link Chat 对话}每次传递参数都可以对参数进行校验</li>
 * <li>{@link Chat 对话}支持传入的参数数量或类型取决于{@link Stage 阶段}的处理流程，而{@link Command 命令}支持传入不限量个参数（至多{@link Integer#MAX_VALUE 2<sup>31</sup>-1}个）</li>
 * </ul>
 * 这个接口代表一个命令的定义，用于处理用户输入的命令。每个命令都包括{@link #getLabels 标签}、{@link #getDescriptions 描述}、{@link #getType 作用域}、{@link #needPermission 所需权限}等信息，
 * 并提供{@link #init 初始化}和{@link #onCommand 执行命令}的方法。<p>
 * <b>不建议命令实现此接口，而是继承 {@link BaseCommand}类</b>
 *
 * @author SpCo
 * @version 1.1
 * @see BaseCommand
 * @since 1.0
 */
public interface Command {
    /**
     * 命令的标签（或名称）<p>
     * 如命令 {@code "/command arg"} 中，{@code "command"} 就是该命令的标签。<p>
     * 命令的标签是一个数组。数组的第一个元素是主标签，其余都是别称。<p>
     * 在使用 {@link HelpCommand} 时，会将别称的描述重定向到主标签。<p>
     * 如：
     * <pre>
     *     help - 显示帮助信息
     *     ? -> help
     * </pre>
     */
    String[] getLabels();

    /**
     * 命令的描述
     */
    String getDescriptions();

    /**
     * 命令的作用域
     *
     * @see CommandType
     */
    CommandType getType();

    /**
     * 使用命令所需的最低权限等级
     *
     * @see UserPermission
     */
    UserPermission needPermission();

    /**
     * 命令发送用户是否有足够的权限触发该命令<p>
     * 默认情况下与 {@link #needPermission()} 有关。
     *
     * @return 如果返回 {@code false} 则命令发送用户会被提示：{@code "[告知] 您无权使用此命令."}
     */
    boolean hasPermission(BotUser user) throws SQLException;

    /**
     * 初始化命令<p>
     * 会在命令被成功注册时被调用。
     *
     * @see CommandSystem#registerCommands
     */
    void init();

    /**
     * 命令的操作<p>
     * 会在命令被有效触发时被调用。
     *
     * @param bot     收到命令的机器人对象
     * @param from    收到命令的来源
     * @param sender  命令的发送者
     * @param message 原始消息
     * @param time    命令发送的时间
     * @param command 命令的原始文本
     * @param label   触发命令的标签
     * @param args    触发命令提交的参数
     * @see CommandEvents
     */
    void onCommand(Bot bot, Interactive from, BotUser sender, Message message, int time, String command, String label, String[] args);

    /**
     * 在帮助列表是否可见
     */
    boolean isVisible();
}