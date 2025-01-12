package top.spco.api;

/**
 * 代表一个频道。
 *
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public abstract class Channel<T> extends Interactive<T> {
    protected Channel(T channel) {
        super(channel);
    }

    /**
     * 获取该频道名称
     *
     * @return 群名称
     * @since 4.1.0
     */
    public abstract String getName();
}
