package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class ArrayType implements Type {

    public final Type elementType;

    private ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    public static ArrayType of(Type elementType) {
        Objects.requireNonNull(elementType, "Array cannot be of a null type");
        return new ArrayType(elementType);
    }

    @Override
    public Type type() {
        return this;
    }

    public String typeString() {
        return "[" + elementType.typeString();
    }

    public String toString() {
        return "[" + elementType.typeString();
    }
}
