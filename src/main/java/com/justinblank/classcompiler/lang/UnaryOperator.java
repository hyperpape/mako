package com.justinblank.classcompiler.lang;

import org.objectweb.asm.Opcodes;

// TODO: this might clash with the standard library
public enum UnaryOperator {

    NOT,
    BNEG;

    Operation op(Expression exp) {
        return Unary.of(this, exp);
    }

    public static Expression not(Expression expression) {
        // Canonicalize to avoid double-negation. This will slightly simplify method resolution later.
        if (expression instanceof Unary) {
            Unary un = (Unary) expression;
            if (un.operator == NOT) {
                return un.expression;
            }
        }
        return NOT.op(expression);
    }

    public int asmOP(Type type) {
        switch (this) {
            case NOT:
                return Opcodes.INEG;
            case BNEG:
                if (type == Builtin.L) {
                    return Opcodes.LNEG;
                }
                else {
                    return Opcodes.INEG;
                }
            default:
                throw new UnsupportedOperationException("Unhandled case " + this);
        }
    }

    public Type type(Type expressionType) {
        switch (this) {
            case NOT:
                return Builtin.BOOL;
            case BNEG:
                return expressionType;
            default:
                throw new UnsupportedOperationException("Unhandled case " + this);
        }
    }
}
