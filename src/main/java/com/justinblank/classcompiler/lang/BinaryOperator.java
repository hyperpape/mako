package com.justinblank.classcompiler.lang;

import static org.objectweb.asm.Opcodes.*;

public enum BinaryOperator {

    AND,
    OR,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    PLUS,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MOD;



    public Operation op(Expression left, Expression right) {
        return Binary.of(this, left, right);
    }

    public static Operation plus(Expression left, Expression right) {
        return PLUS.op(left, right);
    }

    public static Operation eq(Expression left, Expression right) {
        return EQUALS.op(left, right);
    }

    public int asmOP(Type left, Type right) {
        switch (this) {
            case PLUS:
                return IADD;
            case SUBTRACT:
                return ISUB;
            case MULTIPLY:
                return IMUL;
            case DIVIDE:
                return IDIV;
            case EQUALS:
                return IF_ICMPNE;
            default:
                throw new UnsupportedOperationException("");
        }
    };
}