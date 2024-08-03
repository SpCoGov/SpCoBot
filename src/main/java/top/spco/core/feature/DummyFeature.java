package top.spco.core.feature;

import top.spco.api.Interactive;

import java.util.function.Supplier;

/**
 * {@code DummyFeature} 是一个占位符。
 * 它的 {@code isAvailable} 方法总是返回 {@code false}，并且其 {@code manager} 方法返回 {@code null}。
 * 这个类是单例模式的实现，唯一实例通过 {@link #INSTANCE} 静态字段获取。
 *
 * @version 4.0.0
 * @since 4.0.0
 * @author SpCo
 *
 * @see SimpleFeatureManager
 */
public class DummyFeature extends Feature {
    public static final DummyFeature INSTANCE = new DummyFeature();

    private DummyFeature() {
    }

    @Override
    public boolean isAvailable(Interactive<?> where) {
        return false;
    }

    @Override
    public Supplier<FeatureManager<?, ? extends Feature>> manager() {
        return null;
    }
}
