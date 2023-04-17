package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class FieldSet extends Statement {

    public final FieldReference fieldReference;
    public final Expression expression;

    public FieldSet(FieldReference fieldReference, Expression expression) {
        this.fieldReference = Objects.requireNonNull(fieldReference, "fieldReference cannot be null");
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
    }

    @Override
    public String toString() {
        return fieldReference + "." + expression;
    }
}
