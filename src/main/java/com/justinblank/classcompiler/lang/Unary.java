package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

import java.util.Objects;

public class Unary extends Operation {

    public final UnaryOperator operator;
    public final Expression expression;

    private Unary(UnaryOperator operator, Expression expression) {
        this.operator = Objects.requireNonNull(operator, "operator cannot be null");
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
    }

    public static Unary of(UnaryOperator operator, Expression expression) {
        return new Unary(operator, expression);
    }

    @Override
    int asmOP(Method method) {
        return operator.asmOP(method.typeOf(expression));
    }
}
