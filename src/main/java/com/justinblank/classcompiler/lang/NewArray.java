package com.justinblank.classcompiler.lang;

import java.util.Objects;

import static com.justinblank.classcompiler.lang.Literal.literal;

public class NewArray implements Expression {

    public final Expression size;
    public final Type type;

    private NewArray(Expression size, Type type) {
        this.size = Objects.requireNonNull(size, "array size cannot be null");
        this.type = Objects.requireNonNull(type, "array type cannot be null");
    }

    public static NewArray newArray(Expression size, Type type) {
        return new NewArray(size, type);
    }

    public static NewArray newArray(int size, Type type) {
        return new NewArray(literal(size), type);
    }

    public String toString() {
        return "new " + type.typeString() + "[" + size + "]";
    }
}
