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
package top.spco.core.event.base.toposort;

import java.util.ArrayList;
import java.util.List;

/**
 * 可排序节点的抽象类，用于在图中表示节点，并维护节点之间的顺序关系
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public abstract class SortableNode<N extends SortableNode<N>> {
    final List<N> subsequentNodes = new ArrayList<>();
    final List<N> previousNodes = new ArrayList<>();
    boolean visited = false;

    /**
     * @return Description of this node, used to print the cycle warning.
     */
    protected abstract String getDescription();

    public static <N extends SortableNode<N>> void link(N first, N second) {
        if (first == second) {
            throw new IllegalArgumentException("Cannot link a node to itself!");
        }

        first.subsequentNodes.add(second);
        second.previousNodes.add(first);
    }
}