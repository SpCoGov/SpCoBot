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
package top.spco.util;

import org.jetbrains.annotations.NotNull;
import top.spco.api.Interactive;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Predicate;

/**
 * <p>
 * Created on 2023/10/27 0027 13:29
 * <p>
 *
 * @author SpCo
 * @version 1.2
 * @since 1.0
 */
public class InteractiveList<C extends Interactive> implements Collection<C> {

    private final Collection<C> delegate;

    public InteractiveList(Collection<C> delegate) {
        this.delegate = delegate;
    }

    public InteractiveList() {
        this(new ConcurrentLinkedDeque<>());
    }

    public C get(long id) {
        for (C contact : delegate) {
            if (contact.getId() == id) {
                return contact;
            }
        }
        return null;
    }

    public boolean remove(long id) {
        Predicate<C> predicate = contact -> contact.getId() == id;
        return delegate.removeIf(predicate);
    }

    public C getOrFail(long id) {
        C contact = get(id);
        if (contact == null) {
            throw new NoSuchElementException("Contact " + id + " not found.");
        }
        return contact;
    }

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
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
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