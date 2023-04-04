package com.justinblank.classcompiler.lang;

public class FieldSet extends Statement {

    public final FieldReference fieldReference;
    public final Expression expression;

    public FieldSet(FieldReference fieldReference, Expression expression) {
        this.fieldReference = fieldReference;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return fieldReference.toString() + "." + expression;
    }
}
