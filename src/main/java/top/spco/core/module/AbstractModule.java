/*
 * Copyright 2025 SpCo
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
package top.spco.core.module;

import org.jetbrains.annotations.NotNull;
import top.spco.api.Interactive;
import top.spco.core.feature.Feature;
import top.spco.core.feature.FeatureManager;
import top.spco.util.StringUtil;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@code Module} 类是所有模块的抽象基类。
 * 它定义了模块的基本属性和行为，包括激活和停用的操作。
 * 子类应该实现特定的行为，如何在模块激活或停用时执行的动作。
 *
 * <p>模块通过名称进行识别，并可通过 {@link #toggle()} 方法切换其活动状态。</p>
 * <p>此类实现了 {@link Comparable} 接口，以便可以根据模块名称对模块进行排序。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * public class MyModule extends Module {
 *     public MyModule() {
 *         super("MyModule");
 *     }
 *
 *     @Override
 *     public void onActivate() {
 *         // 模块激活时的逻辑
 *         System.out.println(getName() + " is activated.");
 *     }
 *
 *     @Override
 *     public void onDeactivate() {
 *         // 模块停用时的逻辑
 *         System.out.println(getName() + " is deactivated.");
 *     }
 *
 *     @Override
 *     public void init() {
 *         // 模块初始化
 *         System.out.println(getName() + " is initialized.");
 *     }
 * }
 *
 * public class Main {
 *     public static void main(String[] args) {
 *         Module module = new MyModule();
 *         ModuleManager.getInstance().add(module);
 *         module.toggle(); // 激活模块
 *         module.toggle(); // 停用模块
 *     }
 * }
 * }</pre>
 *
 * @author SpCo
 * @version 4.0.0
 * @since 2.0.0
 */
public abstract class AbstractModule extends Feature implements Comparable<AbstractModule> {
    private final String name;

    /**
     * 构造一个新的模块。
     *
     * @param name 模块的名称。
     */
    public AbstractModule(String name) {
        this.name = name;
    }

    boolean active;

    /**
     * 返回模块当前的激活状态。
     *
     * @return 如果模块当前处于激活状态，则为 {@code true}；否则为 {@code false}。
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 当模块被激活时调用。子类应该覆盖此方法以实现特定的激活行为。
     */
    public abstract void onActivate();

    /**
     * 当模块被停用时调用。子类应该覆盖此方法以实现特定的停用行为。
     */
    public abstract void onDeactivate();

    /**
     * 切换模块的激活状态。
     * 如果模块当前是非激活状态，它将被激活，并调用 {@link #onActivate()}。
     * 如果模块当前是激活状态，它将被停用，并调用 {@link #onDeactivate()}。
     */
    public void toggle() throws SQLException {
        if (!active) {
            active = true;
            Feature.setDisabled(this, false);
            ModuleManager.getInstance().addActive(this);
            onActivate();
        } else {
            active = false;
            Feature.setDisabled(this, true);
            ModuleManager.getInstance().removeActive(this);
            onDeactivate();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractModule module = (AbstractModule) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull AbstractModule o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean isAvailable(Interactive<?> where) throws SQLException {
        if (!ModuleManager.getInstance().isActive(getClass())) {
            return false;
        }
        return ((ModuleManager) manager().get()).isFeatureAvailable(where, this.getClass(), this);
    }

    @Override
    public String getFeatureName() {
        return StringUtil.toSnakeCase(getName());
    }

    @Override
    public Supplier<FeatureManager<?, ? extends Feature>> manager() {
        return ModuleManager::getInstance;
    }
}