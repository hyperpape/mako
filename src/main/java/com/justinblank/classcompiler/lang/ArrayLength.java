package com.justinblank.classcompiler.lang;

public class ArrayLength implements Expression {

    public final Expression expression;

    ArrayLength(Expression expression) {
        this.expression = expression;
    }
}
