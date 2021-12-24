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

    public static Operation sub(Expression left, Expression right) {
        return SUBTRACT.op(left, right);
    }

    public static Operation mul(Expression left, Expression right) {
        return MULTIPLY.op(left, right);
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
    }

    public Type type(Type left, Type right) {
        switch (this) {
            case PLUS:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
                return left;
            case EQUALS:
            case NOT_EQUALS:
            case GREATER_THAN:
            case LESS_THAN:
            case AND:
            case OR:
                return Builtin.BOOL;
            default:
                throw new IllegalStateException("");
        }
    }
}