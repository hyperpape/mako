package com.justinblank.classcompiler.lang;

import static com.justinblank.classcompiler.lang.Literal.literal;

public class NewArray implements Expression {

    public final Expression size;
    public final Type type;

    private NewArray(Expression size, Type type) {
        this.size = size;
        this.type = type;
    }

    public static NewArray newArray(Expression size, Type type) {
        return new NewArray(size, type);
    }

    public static NewArray newArray(int size, Type type) {
        return new NewArray(literal(size), type);
    }
}
