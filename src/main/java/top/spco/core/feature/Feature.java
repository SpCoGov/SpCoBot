package top.spco.core.feature;

import top.spco.api.Interactive;

import java.util.function.Supplier;

/**
 * 该类表示机器人的一个功能。
 *
 * @version 4.0.0
 * @since 4.0.0
 * @author SpCo
 */
public abstract class Feature {
    /**
     * 查询该功能在某个可交互的对象中是否可用。
     *
     * @param where 要查询的对象
     */
    public abstract boolean isAvailable(Interactive<?> where);

    /**
     * 功能的初始化。会在其注册完毕后执行。
     */
    public void init() {}

    public abstract Supplier<FeatureManager<?, ? extends Feature>> manager();
}
