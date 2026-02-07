package com.socialmedia.model;

import java.util.Objects;

public abstract class PlatformEntity {

    private final int id;

    protected PlatformEntity(int id) {
        this.id = id;
    }

    public final int getId() {
        return id;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        PlatformEntity that = (PlatformEntity) other;
        return id == that.id;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public abstract String toString();
}
