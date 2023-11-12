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
package top.spco.core.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Created on 2023/10/25 0025 16:46
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class ResourceIdentifier implements Comparable<ResourceIdentifier> {
    private final String namespace;
    private final String path;

    protected ResourceIdentifier(String namespace, String path, @Nullable ResourceIdentifier.Dummy ignoredDummy) {
        this.namespace = namespace;
        this.path = path;
    }

    public ResourceIdentifier(String namespace, String path) {
        this(assertValidNamespace(namespace, path), assertValidPath(namespace, path), null);
    }

    private static String assertValidNamespace(String namespace, String path) {
        if (!isValidNamespace(namespace)) {
            throw new ResourceIdentifierException("Non [a-z0-9_.-] character in namespace of identifier: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    private static String assertValidPath(String namespace, String path) {
        if (!isValidPath(path)) {
            throw new ResourceIdentifierException("Non [a-z0-9/._-] character in path of identifier: " + namespace + ":" + path);
        } else {
            return path;
        }
    }

    private static boolean isValidPath(String path) {
        for(int i = 0; i < path.length(); ++i) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean validPathChar(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '/' || character == '.';
    }
    
    private static boolean isValidNamespace(String namespace) {
        for(int i = 0; i < namespace.length(); ++i) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean validNamespaceChar(char character) {
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    @Override
    public int compareTo(@NotNull ResourceIdentifier id) {
        int i = this.path.compareTo(id.path);
        if (i == 0) {
            i = this.namespace.compareTo(id.namespace);
        }

        return i;
    }

    protected interface Dummy {
    }
}