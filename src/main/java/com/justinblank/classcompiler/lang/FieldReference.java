package com.justinblank.classcompiler.lang;

import com.justinblank.util.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class FieldReference implements Expression {

    public final String fieldName;
    public final Expression expression;
    public final Type type;

    FieldReference(String fieldName, Type type, Expression expression) {
        this.fieldName = Validate.requireNonEmpty(fieldName, "fieldName cannot be blank");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.expression = Objects.requireNonNull(expression, "Expression cannot be null");
    }

    @Override
    public String toString() {
        return expression.toString() + "." + fieldName;
    }
}
