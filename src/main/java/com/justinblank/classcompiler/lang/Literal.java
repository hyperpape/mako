package com.justinblank.classcompiler.lang;

public class Literal implements Expression {

    public final Object value;

    private Literal(Object value) {
        this.value = value;
    }

    public static Literal literal(Number n) {
        return new Literal(n);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
