package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class ArrayLength implements Expression {

    public final Expression expression;

    ArrayLength(Expression expression) {
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
    }
}
