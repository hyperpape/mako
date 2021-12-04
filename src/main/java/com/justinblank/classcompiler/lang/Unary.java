package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

public class Unary extends Operation {

    public final UnaryOperator operator;
    public final Expression expression;

    private Unary(UnaryOperator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public static Unary of(UnaryOperator operator, Expression expression) {
        return new Unary(operator, expression);
    }

    @Override
    int asmOP() {
        return operator.asmOP(Method.typeOf(expression));
    }
}
