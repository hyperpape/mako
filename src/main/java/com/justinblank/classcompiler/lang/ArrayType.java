package com.justinblank.classcompiler.lang;

public class ArrayType implements Type {

    private final Type type;

    public ArrayType(Type type) {
        this.type = type;
    }

    @Override
    public Type type() {
        return this;
    }
}
