package com.justinblank.classcompiler.lang;

// TODO: Is this required?
public class TypedReturn extends Statement {

    public final Expression expression;
    public final Type type;

    public TypedReturn(Expression expression, Type type) {
        super();
        this.expression = expression;
        this.type = type;
    }
}
