package top.spco.core.feature;


import top.spco.service.RegistrationException;
import top.spco.service.command.exceptions.CommandRegistrationException;

import java.util.Map;

/**
 * {@code SimpleFeatureManager} 和 {@link FeatureManager} 一样，是一种功能管理器，用于管理特定类型的功能。
 * 它使用 {@link DummyFeature} 作为占位符，以标识已注册的功能。
 *
 * @param <F> 功能的类型
 * @version 4.0.0
 * @since 4.0.0
 * @author SpCo
 *
 * @see FeatureManager
 * @see DummyFeature
 */
public abstract class SimpleFeatureManager<F extends Feature> extends FeatureManager<F, DummyFeature> {
    protected static final DummyFeature PRESENT = DummyFeature.INSTANCE;

    @Deprecated
    @Override
    public void register(F feature, DummyFeature object) throws RegistrationException {
        register(feature);
    }

    public void register(F feature) throws RegistrationException {
        if (getAllRegistered().containsKey(feature)) {
            throw new CommandRegistrationException(feature + " is registered in the " + this.getClass().getName() + ".");
        }
        getAllRegistered().put(feature, PRESENT);
    }

    @Deprecated
    @Override
    public DummyFeature get(F key) {
        return PRESENT;
    }

    @Override
    public Map<F, DummyFeature> getAllRegistered() {
        return super.getAllRegistered();
    }
}
