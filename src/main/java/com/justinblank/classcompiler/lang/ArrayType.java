package com.justinblank.classcompiler.lang;

public class ArrayType implements Type {

    public final Type elementType;

    private ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    public static ArrayType of(Type elementType) {
        return new ArrayType(elementType);
    }

    @Override
    public Type type() {
        return this;
    }

    public String typeString() {
        return "[" + elementType.typeString();
    }
}
