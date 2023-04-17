package com.justinblank.classcompiler.lang;

import com.justinblank.util.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class StaticFieldReference implements Expression {

    public final String fieldName;
    public final Type receiver;
    public final Type type;

    public StaticFieldReference(String fieldName, Type receiver, Type type) {
        this.fieldName = Validate.requireNonEmpty(fieldName, "fieldName cannot be blank");
        this.receiver = Objects.requireNonNull(receiver, "Receiver cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }

    @Override
    public String toString() {
        return receiver.toString() + "." + fieldName;
    }

}
