package com.justinblank.classcompiler.lang;

public class ArrayType implements Type {

    public final Type elementType;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    @Override
    public Type type() {
        return this;
    }
}
