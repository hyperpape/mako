package com.justinblank.classcompiler.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class VariableRead implements Expression {

    public final String variable;

    public VariableRead(String variable) {
        Objects.requireNonNull(variable, "Cannot read variable with null name");
        if (StringUtils.isBlank(variable)) {
            throw new IllegalArgumentException("Cannot read variable with name='" + variable + "'");
        }
        this.variable = variable;
    }

    public String toString() {
        return "" + variable;
    }
}
