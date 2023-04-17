package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class ReturnExpression extends Statement {

    public final Expression expression;

    ReturnExpression(Expression expression) {
        this.expression = Objects.requireNonNull(expression, "cannot return a null expression");
    }

    public String toString() {
        return "return " + expression;
    }
}
