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

import top.spco.api.Image;
import top.spco.api.Interactive;
import top.spco.api.Member;
import top.spco.core.Wrapper;
import top.spco.api.message.service.MessageService;

import java.io.File;

/**
 * 表示一个通用的消息。直接实现此类的类是“可附加的”，我们称之为 "复合消息"，意味着它可以使用 {@link #append(Message)} 等命令在该消息的后面附加其他消息。
 * 但是，需要注意的是，{@code Message} 的子类（尤其是抽象类）通常代表消息的组成部分或元素，我们称之为 “元素消息”
 * 如 {@code Image} 。这些子类的实例（或其具体实现类）并非用于附加。相反，它们应当被用来表示消息内的独立元素。
 *
 * <h1>消息的通性</h1>
 * <ul>
 * <li>所有消息都可以通过 {@link Interactive#sendMessage(Message)} 或其他方法直接发送给{@link Interactive 可交互的对象}。</li>
 * <li>消息的内容一旦创建不能被修改，但是可以使用 {@link #wrap(Object)} 修改其包装的原始消息的引用</li>
 * </ul>
 *
 *
 * <h1>获取消息对象</h1>
 * 用户可以通过事件等方式获得机器人收到的消息，也可以自行创建消息对象。
 * 如通过 {@link MessageService#asMessage(String)} 来将字符串转换为一个消息对象、
 * 通过 {@link MessageService#at(long)} 获取一个 @{@link Member 某群员} 的消息对象。
 *
 * <h1>操作消息对象</h1>
 * 以下列举了一些对消息的操作，如附加、引用等等。
 *
 * <h2>在消息后添加消息</h2>
 * 对于复合消息可以直接使用 {@link #append(Message)} 等方法直接向其末尾添加消息。
 * <p>对于元素消息，需要先使用 {@link #toMessage()} 来将其转换为一个复合消息再进行后续操作。
 *
 * <pre>{@code
 * Interactive from = getFrom();
 * File file = new File("./image.png");
 * Image<?> image = SpCoBot.getInstance().getMessageService().toImage(file, from);
 * Message<?> message = image.toMessage();
 *
 * message.append("这是一张图片。").append(image);
 * from.sendMessage(message);
 * }</pre>
 *
 * <h2>引用一条消息</h2>
 * 和上面一样，元素消息不能直接引用一条消息，需先进行转换。
 * <p>复合消息可以使用 {@link #quoteReply(Message)} 来进行引用。注意：这里提交的消息不能是用户自己创建的消息。
 *
 * <pre>{@code
 * Interactive from = getFrom();
 * Message<?> sourceMessage = getMessage();
 * Message<?> message = SpCoBot.getInstance().getMessageService().asMessage("文本。");
 *
 * from.sendMessage(message.quoteReply(sourceMessage));
 * }</pre>
 *
 * <h2>撤回消息</h2>
 * 除了用户自行创建的消息，都可以使用 {@link MessageService#recall(Message)} 来撤回。
 *
 * <pre>{@code
 * Message<?> message = getMessage();
 *
 * SpCoBot.getInstance().getMessageService().recall(message);
 * }</pre>
 *
 * <h2>发送消息</h2>
 * 所有消息都可以通过 {@link Interactive#sendMessage(Message)} 发送给任意{@link Interactive 可交互的对象}。
 *
 * <pre>{@code
 * Interactive from = getFrom();
 * File file = new File("./image.png");
 * Message<?> text = SpCoBot.getInstance().getMessageService().asMessage("文本");
 * Image<?> image = SpCoBot.getInstance().getMessageService().toImage(file, from);
 *
 * from.sendMessage(text);
 * from.sendMessage(image);
 * }</pre>
 * <p>
 * 元素消息可能也会有自己独特的发送的方法。比如 {@link Interactive#sendImage(File)}。
 *
 * <pre>{@code
 * Interactive from = getFrom();
 * File file = new File("./image.png");
 *
 * from.sendImage(from);
 * }</pre>
 *
 * @author SpCo
 * @version 2.0.0
 * @see Image
 * @see MessageService
 * @see MessageSource
 * @since 0.1.0
 */
public abstract class Message<T> extends Wrapper<T> implements Codable {
    protected Message(T message) {
        super(message);
    }

    /**
     * 转为接近官方格式的字符串, 即 "内容". 如 At(member) + "test" 将转为 "@QQ test"
     *
     * @return 转化后的文本
     */
    public abstract String toMessageContext();

    /**
     * 引用一条消息
     *
     * @param toQuote 需要引用的消息
     */
    public abstract Message<T> quoteReply(Message<?> toQuote);

    /**
     * 在这条消息后添加 {@code Message} 对象
     *
     * @param another 需要添加的 {@code Message} 对象
     */
    public abstract Message<T> append(Message<?> another);

    /**
     * 在这条消息后添加文本
     *
     * @param another 需要添加的文本
     */
    public abstract Message<T> append(String another);

    /**
     * 将一条消息转换成普通的消息
     *
     * @return 转换的结果
     */
    public abstract Message<?> toMessage();
}