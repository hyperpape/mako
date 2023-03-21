package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class ReturnExpression extends Statement {

    public final Expression expression;

    ReturnExpression(Expression expression) {
        Objects.requireNonNull(expression, "cannot return a null expression");
        this.expression = expression;
    }

    public String toString() {
        return "Return " + expression;
    }
}
