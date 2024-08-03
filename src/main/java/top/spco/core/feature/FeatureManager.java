package top.spco.core.feature;


import top.spco.core.Manager;

/**
 * 表示某个{@link Feature 功能}的管理器。
 *
 * @param <F> 功能的类型
 * @version 4.0.0
 * @since 4.0.0
 * @author SpCo
 */
public abstract class FeatureManager<K, F extends Feature> extends Manager<K, F> {

}
