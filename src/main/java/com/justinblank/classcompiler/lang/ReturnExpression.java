package com.justinblank.classcompiler.lang;

public class ReturnExpression extends Statement {

    public final Expression expression;

    ReturnExpression(Expression expression) {
        this.expression = expression;
    }
}
