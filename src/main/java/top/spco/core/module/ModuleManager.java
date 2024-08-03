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
package top.spco.core.module;

import top.spco.core.feature.FeatureManager;
import top.spco.service.RegistrationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * {@code ModuleManager} 是一个单例类，用于管理 {@link AbstractModule} 的生命周期和状态。
 * 它提供了添加、移除、激活、禁用和检索模块的方法。
 *
 * <p>这个类使用单例模式，确保全局只有一个模块管理器实例。
 * 使用 {@link #getInstance()} 方法获取这个实例。</p>
 *
 * <p>示例用法：
 * <pre>{@code
 * ModuleManager manager = ModuleManager.getInstance();
 * MyModule myModule = new MyModule();
 * manager.add(myModule);
 * // ...
 * manager.disableAll();
 * }</pre>
 * </p>
 *
 * @author SpCo
 * @version 4.0.0
 * @since 2.0.0
 */
public class ModuleManager extends FeatureManager<Class<? extends AbstractModule>, AbstractModule> {
    private static ModuleManager instance;
    private final List<AbstractModule> active = new ArrayList<>();

    /**
     * 获取 {@code ModuleManager} 的单例实例。
     * 如果实例不存在，将创建一个新的实例。
     *
     * @return {@code ModuleManager} 的单例实例
     */
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    /**
     * 根据模块类获取模块实例。
     *
     * @param klass 模块类的 {@link Class} 对象
     * @param <T>   模块的类型
     * @return 指定类型的模块实例，如果不存在则返回 {@code null}
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractModule> T get(Class<T> klass) {
        return (T) getAllRegistered().get(klass);
    }

    /**
     * 根据模块名称获取模块实例。
     *
     * @param name 模块的名称
     * @return 与给定名称匹配的模块实例，如果没有找到则返回 {@code null}
     */
    public AbstractModule get(String name) {
        for (AbstractModule module : getAllRegistered().values()) {
            if (module.getName().equalsIgnoreCase(name)) return module;
        }
        return null;
    }

    /**
     * 检查指定类的模块是否处于激活状态。
     *
     * @param klass 模块类的 {@link Class} 对象
     * @return 如果模块处于激活状态，则返回 {@code true} ；否则返回 {@code false}
     */
    public boolean isActive(Class<? extends AbstractModule> klass) {
        AbstractModule module = get(klass);
        return module != null && module.isActive();
    }

    /**
     * 获取所有已加载的模块实例的集合。
     *
     * @return 包含所有模块实例的 {@link Collection}
     */
    public Collection<AbstractModule> getAll() {
        return getAllRegistered().values();
    }

    /**
     * 获取已加载模块的数量。
     *
     * @return 已注册模块的数量
     */
    public int getCount() {
        return getAllRegistered().values().size();
    }

    /**
     * 获取当前激活的模块列表。
     *
     * @return 包含所有激活模块的 {@link List}
     */
    public List<AbstractModule> getActive() {
        synchronized (active) {
            return active;
        }
    }

    void addActive(AbstractModule module) {
        synchronized (active) {
            if (!active.contains(module)) {
                active.add(module);
            }
        }
    }

    void removeActive(AbstractModule module) {
        synchronized (active) {
            active.remove(module);
        }
    }

    /**
     * 向模块管理器中添加一个模块。
     *
     * @param module 要添加的模块
     * @param active 注册后是否直接激活
     */
    public void register(AbstractModule module, boolean active) {
        register(module);
        if (active) {
            module.toggle();
        }
    }

    /**
     * 向模块管理器中添加一个模块。
     *
     * @param module 要添加的模块
     */
    public void register(AbstractModule module) {
        getAllRegistered().put(module.getClass(), module);
        module.init();
    }


    @Deprecated
    @Override
    public void register(Class<? extends AbstractModule> value, AbstractModule module) throws RegistrationException {
        register(module);
    }

    /**
     * 获取模块的迭代器。
     *
     * @return 模块的迭代器
     */
    public Iterator<AbstractModule> iterator() {
        return new ModuleIterator();
    }

    private static class ModuleIterator implements Iterator<AbstractModule> {
        private final Iterator<AbstractModule> iterator = ModuleManager.getInstance().getAll().iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public AbstractModule next() {
            return iterator.next();
        }
    }
}