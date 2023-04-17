package com.justinblank.classcompiler.lang;

import java.util.Objects;

// TODO: Is this required?
public class TypedReturn extends Statement {

    public final Expression expression;
    public final Type type;

    public TypedReturn(Expression expression, Type type) {
        super();
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }
}
