package com.justinblank.classcompiler.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class FieldReference implements Expression {

    public final String fieldName;
    public final Expression expression;
    public final Type type;

    FieldReference(String fieldName, Type type, Expression expression) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("FieldName cannot be blank");
        }
        this.fieldName = fieldName;
        Objects.requireNonNull(type, "Type cannot be null");
        this.type = type;
        Objects.requireNonNull(expression, "Expression cannot be null");
        this.expression = expression;
    }
}
