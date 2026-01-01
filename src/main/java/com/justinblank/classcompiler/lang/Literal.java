package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class Literal implements Expression {

    public final Object value;

    private Literal(Object value) {
        this.value = Objects.requireNonNull(value, "literal value cannot be null");
    }

    public static Literal literal(Number n) {
        return new Literal(n);
    }

    public static Literal literal(boolean b) {
        return new Literal(b);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
