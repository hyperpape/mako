package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class StaticFieldSet extends Statement {

    public final StaticFieldReference fieldReference;
    public final Expression expression;

    public StaticFieldSet(StaticFieldReference fieldReference, Expression expression) {
        this.fieldReference = Objects.requireNonNull(fieldReference, "fieldReference cannot be null");
        this.expression = Objects.requireNonNull(expression, "expression cannot be null");
    }

    @Override
    public String toString() {
        return fieldReference + "." + expression;
    }
}
