package com.justinblank.classcompiler.lang;

public class NewArray extends Expression {

    public final Expression size;
    public final Type type;

    private NewArray(Expression size, Type type) {
        this.size = size;
        this.type = type;
    }

    public static NewArray newArray(Expression size, Type type) {
        return new NewArray(size, type);
    }
}
