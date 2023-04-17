package com.justinblank.classcompiler.lang;

import com.justinblank.util.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class VariableRead implements Expression {

    public final String variable;

    public VariableRead(String variable) {
        this.variable = Validate.requireNonEmpty(variable, "variable name cannot be blank");
    }

    public String toString() {
        return "" + variable;
    }
}
