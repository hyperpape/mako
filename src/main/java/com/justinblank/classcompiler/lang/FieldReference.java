package com.justinblank.classcompiler.lang;

public class FieldReference implements Expression {

    public final String fieldName;
    public final Expression expression;
    public final Type type;

    FieldReference(String fieldName, Type type, Expression expression) {
        this.fieldName = fieldName;
        this.type = type;
        this.expression = expression;
    }
}
