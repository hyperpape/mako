package com.justinblank.classcompiler.lang;

public class Unary extends Operation {

    private final UnaryOperator operator;
    private final Expression expression;

    private Unary(UnaryOperator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public static Unary of(UnaryOperator operator, Expression expression) {
        return new Unary(operator, expression);
    }
}
