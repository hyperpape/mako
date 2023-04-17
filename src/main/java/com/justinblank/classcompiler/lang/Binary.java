package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

import java.util.Objects;
import java.util.Optional;

public class Binary extends Operation {

    public final BinaryOperator operator;
    public final Expression left;
    public final Expression right;

    Binary(BinaryOperator operator, Expression left, Expression right) {
        this.operator = Objects.requireNonNull(operator, "operator cannot be null");
        this.left = Objects.requireNonNull(left, "left side of operator cannot be null");
        this.right = Objects.requireNonNull(right, "right side of operator cannot be null");
    }

    public static Binary of(BinaryOperator operator, Expression left, Expression right) {
        return new Binary(operator, left, right);
    }

    public int asmOP(Method method) {
        return operator.asmOP(method.typeOf(left), method.typeOf(right));
    }

    public Optional<Integer> comparisonOperation(Method method) {
        return operator.comparisonOperation(method.typeOf(left), method.typeOf(right));
    }
}
