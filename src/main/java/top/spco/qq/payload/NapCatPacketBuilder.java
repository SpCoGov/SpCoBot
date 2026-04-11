package top.spco.qq.payload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

/**
 * NapCat 请求封包构建器。
 *
 * <p>用于构建如下结构的请求包：</p>
 * <pre>{@code
 * {
 *   "action": "send_group_msg",
 *   "params": {
 *     "group_id": 123456,
 *     "message": "hello"
 *   },
 *   "echo": "custom-echo"
 * }
 * }</pre>
 *
 * <p>其中：</p>
 * <ul>
 *     <li>{@code action} 为 NapCat 终结点名称</li>
 *     <li>{@code params} 为请求参数集合</li>
 *     <li>{@code echo} 为可选字段；如果指定，响应包会带回相同的值</li>
 * </ul>
 */
public class NapCatPacketBuilder {
    private static final Gson GSON = new Gson();

    private String action;
    private final JsonObject params = new JsonObject();
    private JsonElement echo;

    /**
     * 创建一个空的 NapCat 封包构建器。
     */
    public NapCatPacketBuilder() {
    }

    /**
     * 创建一个带 action 的 NapCat 封包构建器。
     *
     * @param action NapCat 终结点名称
     */
    public NapCatPacketBuilder(String action) {
        this.action = action;
    }

    /**
     * 使用指定 action 创建构建器。
     *
     * @param action NapCat 终结点名称
     * @return 新的构建器实例
     */
    public static NapCatPacketBuilder create(String action) {
        return new NapCatPacketBuilder(action);
    }

    /**
     * 设置请求 action。
     *
     * @param action NapCat 终结点名称，例如 {@code send_group_msg}
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder action(String action) {
        this.action = action;
        return this;
    }

    /**
     * 添加字符串类型参数。
     *
     * @param name  参数名
     * @param value 参数值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder param(String name, String value) {
        params.addProperty(name, value);
        return this;
    }

    /**
     * 添加数字类型参数。
     *
     * @param name  参数名
     * @param value 参数值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder param(String name, Number value) {
        params.addProperty(name, value);
        return this;
    }

    /**
     * 添加布尔类型参数。
     *
     * @param name  参数名
     * @param value 参数值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder param(String name, Boolean value) {
        params.addProperty(name, value);
        return this;
    }

    /**
     * 添加字符类型参数。
     *
     * @param name  参数名
     * @param value 参数值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder param(String name, Character value) {
        params.addProperty(name, value);
        return this;
    }

    /**
     * 添加任意 Json 参数。
     *
     * @param name    参数名
     * @param element 参数值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder param(String name, JsonElement element) {
        params.add(name, element == null ? JsonNull.INSTANCE : element);
        return this;
    }

    /**
     * 通过普通对象添加参数。
     *
     * <p>该方法会通过 Gson 将对象转换为 {@link JsonElement} 后写入 {@code params}。</p>
     *
     * @param name  参数名
     * @param value 参数值对象
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder paramObject(String name, Object value) {
        params.add(name, value == null ? JsonNull.INSTANCE : GSON.toJsonTree(value));
        return this;
    }

    /**
     * 批量添加参数。
     *
     * @param values 参数映射
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder params(Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return this;
        }
        // 批量参数统一转换成 JsonElement，便于兼容字符串、数字、对象等多种类型。
        values.forEach(this::addUnknownParam);
        return this;
    }

    /**
     * 直接覆盖整个 params 对象。
     *
     * @param params 完整参数对象
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder params(JsonObject params) {
        this.params.entrySet().clear();
        if (params != null) {
            params.entrySet().forEach(entry -> this.params.add(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * 设置 echo 字符串。
     *
     * @param echo echo 值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder echo(String echo) {
        this.echo = echo == null ? JsonNull.INSTANCE : GSON.toJsonTree(echo);
        return this;
    }

    /**
     * 设置任意 Json 类型的 echo。
     *
     * @param echo echo 值
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder echo(JsonElement echo) {
        this.echo = echo == null ? JsonNull.INSTANCE : echo;
        return this;
    }

    /**
     * 清除已设置的 echo。
     *
     * @return 当前构建器实例
     */
    public NapCatPacketBuilder clearEcho() {
        this.echo = null;
        return this;
    }

    /**
     * 构建 JsonObject 形式的 NapCat 请求包。
     *
     * @return 构建完成的请求包
     * @throws IllegalStateException 当 action 为空时抛出
     */
    public JsonObject build() {
        if (action == null || action.isBlank()) {
            throw new IllegalStateException("action 不能为空");
        }

        JsonObject packet = new JsonObject();
        packet.addProperty("action", action);
        packet.add("params", params.deepCopy());
        // echo 是可选字段，只有调用方显式指定时才写入封包。
        if (echo != null && !echo.isJsonNull()) {
            packet.add("echo", echo.deepCopy());
        }
        return packet;
    }

    /**
     * 构建字符串形式的 NapCat 请求包。
     *
     * @return 序列化后的 JSON 文本
     */
    public String buildString() {
        return build().toString();
    }

    private void addUnknownParam(String name, Object value) {
        Objects.requireNonNull(name, "name");
        if (value instanceof JsonElement element) {
            param(name, element);
            return;
        }
        params.add(name, value == null ? JsonNull.INSTANCE : GSON.toJsonTree(value));
    }
}
