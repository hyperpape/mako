package com.justinblank.classcompiler.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class StaticFieldReference implements Expression {

    public final String fieldName;
    public final Type receiver;
    public final Type type;

    public StaticFieldReference(String fieldName, Type receiver, Type type) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("FieldName cannot be blank");
        }
        this.fieldName = fieldName;
        Objects.requireNonNull(receiver, "Receiver cannot be null");
        this.receiver = type;
        Objects.requireNonNull(type, "Type cannot be null");
        this.type = type;
    }

}
