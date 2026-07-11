package top.spco.permission;

import java.util.Objects;

public final class PermissionNode {
    private final String value;

    public PermissionNode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Permission node cannot be blank.");
        }
        this.value = value.toLowerCase();
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PermissionNode other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
