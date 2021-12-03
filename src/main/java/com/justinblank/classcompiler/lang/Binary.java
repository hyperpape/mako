package com.justinblank.classcompiler.lang;

public class Binary extends Operation {

    private final BinaryOperator operator;
    private final Expression left;
    private final Expression right;

    public Binary(BinaryOperator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public static Binary of(BinaryOperator operator, Expression left, Expression right) {
        return new Binary(operator, left, right);
    }
}
