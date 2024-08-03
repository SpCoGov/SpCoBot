/*
 * Copyright 2024 SpCo
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

import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.core.feature.Feature;
import top.spco.core.feature.FeatureManager;
import top.spco.events.CommandEvents;
import top.spco.service.chat.Chat;
import top.spco.service.chat.Stage;
import top.spco.service.command.commands.HelpCommand;
import top.spco.service.command.exceptions.CommandRegistrationException;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link Command 命令} 是一种用户与机器人交互的方式。<p>
 * 与 {@link Chat 对话} 可能有以下不同：
 * <ul>
 * <li>{@link Chat 对话} 传递参数时可以有明确的引导。</li>
 * <li>{@link Chat 对话} 适合传递内容较大的参数。</li>
 * <li>{@link Chat 对话} 每次传递参数都可以对参数进行校验。</li>
 * <li>{@link Chat 对话} 支持传入的参数数量或类型取决于 {@link Stage 阶段} 的处理流程，而 {@link Command 命令} 支持传入不限量个参数（至多 {@link Integer#MAX_VALUE 2<sup>31</sup>-1} 个）。</li>
 * </ul>
 * 这个接口代表一个命令的定义，用于处理用户输入的命令。每个命令都包括 {@link #getLabels 标签} 、 {@link #getDescriptions 描述} 、 {@link #getScope 作用域} 、 {@link #needPermission 所需权限} 等信息，
 * 并提供 {@link #init 初始化} 和 {@link #onCommand 执行命令} 的方法。<p>
 * <b>不建议命令实现此接口，而是继承 {@link AbstractCommand} 类。</b><p>
 *
 * <h1>命令创建</h1>
 * 创建一个类，并继承 {@code AbstractCommand}
 * <pre>{@code
 * public class TestCommand extends AbstractCommand{
 *     @Override
 *     public String[] getLabels() {
 *         return new String[]{};
 *     }
 *
 *     @Override
 *     public String getDescriptions() {
 *         return null;
 *     }
 *
 *     @Override
 *     public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
 *
 *     }
 * }
 * }</pre>
 * 一个命令最少实现以上三个方法
 * <table border="1">
 *     <tr>
 *         <th>方法名</th>
 *         <th>描述</th>
 *     </tr>
 *     <tr>
 *         <td>{@code getLabels}</td>
 *         <td>定义命令的标签。数组的第一个元素为命令的主标签，其余为命令的别称。</td>
 *     </tr>
 *     <tr>
 *         <td>{@code getDescriptions}</td>
 *         <td>定义该命令的描述。</td>
 *     </tr>
 *     <tr>
 *         <td>{@code onCommand}</td>
 *         <td>定义命令的执行过程。</td>
 *     </tr>
 * </table>
 * <h2>用法</h2>
 * 命令的默认用法是一个不包含任何参数的且描述为 {@link #getDescriptions() 命令描述} 的用法。可以使用 {@link #getUsages()} 方法重写该命令的用法。<p>
 * <b>命令的用法不能直接或间接重复。</b>
 * <h2>作用域</h2>
 * 命令的作用域指命令的 {@link CommandScope 执行范围} 。每个命令在注册阶段会判断该命令所支持的作用域对应的所有已注册命令是否包含该命令的标签。
 * 如果包含，会抛出 {@link CommandRegistrationException} 。<p>
 * 可以使用 {@link #getScope()} 来定义命令的作用域。
 * <h2>权限</h2>
 * 可以使用 {@link #needPermission()} 来定义命令所需的权限。这里的权限只是 {@link BotUser 机器人用户} 的权限，并不指群管理员等。
 * 需要判断群管理员之类的权限，请在 {@link #onCommand} 中进行判断。<p>
 * 命令调用前 {@code CommandDispatcher} 会先使用 {@link #hasPermission(BotUser)} 方法判断该机器人用户是否拥有指定权限，如果该方法返回 {@code false} 则会直接通知用户缺少权限。
 * <h2>可见性</h2>
 * 使用 {@link #isVisible()} 命令可以设置该命令在命令帮助列表中是否可见。
 * <h2>初始化</h2>
 * 当一个命令注册完成后， {@code CommandDispatcher} 会调用该命令的 {@link #init() 初始化方法} 。
 * 该方法会在除 {@link #getScope()}、{@link #getLabels()} 的其他方法之前被执行。
 * <h1>命令注册</h1>
 * 对命令使用 {@link CommandMarker} 注解即可在注册阶段自动注册该命令。
 *
 * @author SpCo
 * @version 4.0.0
 * @see AbstractCommand
 * @since 0.1.0
 */
public abstract class Command extends Feature {
    /**
     * 命令的标签（或名称）。<p>
     * 如命令 {@code "/command arg"} 中，{@code "command"} 就是该命令的标签。<p>
     * 命令的标签是一个数组。数组的第一个元素是主标签，其余都是别称。<p>
     * 在使用 {@link HelpCommand} 时，会将别称的描述重定向到主标签。<p>
     * 如：
     * <pre>
     *     help - 显示帮助信息
     *     ? -> help
     * </pre>
     */
    public abstract String[] getLabels();

    public abstract List<Usage> getUsages();

    /**
     * 命令的描述。
     *
     * @return 命令的描述
     */
    public abstract String getDescriptions();

    /**
     * 命令的作用域。
     *
     * @return 命令的作用域
     * @see CommandScope
     */
    public abstract CommandScope getScope();

    /**
     * 使用命令所需的最低权限等级。<p>
     * 注意：这里的最低权限指的是 {@link BotUser 机器人用户} 的 {@link UserPermission 用户权限} ，而不是群聊中 {@link Member 群成员} 的 {@link MemberPermission 成员权限} 。
     *
     * @return 使用命令所需的最低权限等级
     * @see UserPermission
     */
    public abstract UserPermission needPermission();

    /**
     * 命令发送用户是否有足够的权限触发该命令。<p>
     * 默认情况下与 {@link #needPermission()} 有关。<p>
     * 注意：这里的最低权限指的是 {@link BotUser 机器人用户} 的 {@link UserPermission 用户权限} ，而不是群聊中 {@link Member 群成员} 的 {@link MemberPermission 成员权限} 。
     *
     * @param user 命令发送用户
     * @return 如果返回 {@code false} 则命令发送用户会被提示： {@code "您无权使用此命令."}
     */
    public abstract boolean hasPermission(BotUser user) throws SQLException;

    /**
     * 命令的操作。<p>
     * 会在命令被有效触发时被调用。
     *
     * @param bot       收到命令的机器人对象
     * @param from      收到命令的来源
     * @param sender    命令的发送者
     * @param user      命令的发送用户
     * @param message   原始消息
     * @param time      命令发送的时间
     * @param meta      命令的元数据
     * @param usageName 命令被触发的用法名
     * @throws CommandSyntaxException 用户调用命令发生语法错误时抛出
     * @see CommandEvents
     */
    public abstract void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException;

    /**
     * 在帮助列表是否可见。
     *
     * @return 在帮助列表是否可见
     */
    public abstract boolean isVisible();

    @Override
    public Supplier<FeatureManager<?, ? extends Feature>> manager() {
        return CommandDispatcher::getInstance;
    }

    @Override
    public boolean isAvailable(Interactive<?> where) {
        return true;
    }
}