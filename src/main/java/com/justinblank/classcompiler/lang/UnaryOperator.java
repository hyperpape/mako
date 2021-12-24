package com.justinblank.classcompiler.lang;

// TODO: this might clash with the standard library
public enum UnaryOperator {

    NOT;

    Operation op(Expression exp) {
        return Unary.of(this, exp);
    }

    public static Operation not(Expression expression) {
        return NOT.op(expression);
    }

    public int asmOP(Type type) {
        throw new UnsupportedOperationException();
    }

    public Type type(Type expressionType) {
        switch (this) {
            case NOT:
                return Builtin.BOOL;
            default:
                throw new UnsupportedOperationException("");
        }
    }
}
