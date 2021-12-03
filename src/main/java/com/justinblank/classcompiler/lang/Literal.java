package com.justinblank.classcompiler.lang;

public class Literal extends Expression {

    public final Object value;

    private Literal(Object value) {
        this.value = value;
    }

    public static Literal of(int i) {
        return new Literal(i);
    }
}
