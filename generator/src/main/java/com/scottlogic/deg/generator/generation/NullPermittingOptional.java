package com.scottlogic.deg.generator.generation;

//Optional.of() does not permit <null> as its value, but we can emit this value in the generator
class NullPermittingOptional<T> {
    private final T value;
    private final boolean hasValue;

    NullPermittingOptional(T value) {
        this.value = value;
        hasValue = true;
    }

    NullPermittingOptional() {
        value = null;
        hasValue = false;
    }

    public boolean isPresent() {
        return hasValue;
    }

    public T get() {
        if (!hasValue) {
            throw new IllegalStateException("No value is present");
        }

        return value;
    }

    @Override
    public String toString() {
        return hasValue
            ? String.format("Optional[%s]", value)
            : "Optional[empty]";
    }
}
