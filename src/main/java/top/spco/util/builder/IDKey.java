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
package top.spco.util.builder;

/**
 * Wrap an identity key (System.identityHashCode())
 * so that an object can only be equal() to itself.
 * <p>
 * This is necessary to disambiguate the occasional duplicate
 * identityHashCodes that can occur.
 */
final class IDKey {
    private final Object value;
    private final int id;

    /**
     * Constructor for IDKey
     * @param value The value
     */
    IDKey(final Object value) {
        // This is the Object hash code
        this.id = System.identityHashCode(value);
        // There have been some cases (LANG-459) that return the
        // same identity hash code for different objects.  So
        // the value is also added to disambiguate these cases.
        this.value = value;
    }

    /**
     * returns hash code - i.e. the system identity hash code.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * checks if instances are equal
     * @param other The other object to compare to
     * @return if the instances are for the same object
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof IDKey idKey)) {
            return false;
        }
        if (id != idKey.id) {
            return false;
        }
        // Note that identity equals is used.
        return value == idKey.value;
    }
}