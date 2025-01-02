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
package top.spco.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

/**
 * {@code InteractiveList} 类是一个实现了 {@code Collection} 接口的交互式元素列表。<p>
 * 这个类包装了一个底层的 {@code Collection} 实现，提供了一些额外的方法来操作交互式元素。<p>
 * 以下是一个示例用法:
 * <pre>
 * InteractiveList<MyInteractiveElement> interactiveList = new InteractiveList<>();
 * interactiveList.add(myInteractiveElement);
 * MyInteractiveElement foundElement = interactiveList.get(123);
 * interactiveList.remove(123);
 * </pre>
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public class InteractiveList<C extends Interactive<?>> implements Collection<C> {
    private final Collection<C> delegate;

    /**
     * 使用给定的底层实现构造一个交互式元素列表。
     *
     * @param delegate 底层实现的 {@code Collection}
     */
    public InteractiveList(Collection<C> delegate) {
        this.delegate = delegate;
    }

    /**
     * 构造一个空的交互式元素列表，使用默认的底层实现 {@code ConcurrentLinkedDeque}。
     */
    public InteractiveList() {
        this(new ConcurrentLinkedDeque<>());
    }

    /**
     * 根据给定的ID获取交互式元素。
     *
     * @param id 要查找的元素的ID
     * @return 具有给定ID的交互式元素，如果不存在则返回 {@code null}
     */
    public C get(long id) {
        for (C contact : delegate) {
            if (contact.getId() == id) {
                return contact;
            }
        }
        return null;
    }

    /**
     * 根据给定的ID删除交互式元素。
     *
     * @param id 要删除的元素的ID
     * @return 如果成功删除则返回 {@code true}
     */
    public boolean remove(long id) {
        Predicate<C> predicate = contact -> contact.getId() == id;
        return delegate.removeIf(predicate);
    }

    /**
     * 检查列表中是否包含具有给定ID的交互式元素。
     *
     * @param id 要检查的元素的ID
     * @return 如果列表中包含具有给定ID的元素则返回 {@code true}
     */
    public boolean contains(long id) {
        return get(id) != null;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @NotNull
    @Override
    public Iterator<C> iterator() {
        return this.delegate.iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return this.delegate.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return this.delegate.toArray(a);
    }

    @Override
    public boolean add(C c) {
        return this.delegate.add(c);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends C> c) {
        return this.delegate.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }
}