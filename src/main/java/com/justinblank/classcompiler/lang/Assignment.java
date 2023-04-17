package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class Assignment extends Statement {

    public final String variable;
    public final Expression expression;

    public Assignment(String variable, Expression expression) {
        this.variable = Objects.requireNonNull(variable, "variable cannot be null in an assignment");
        this.expression = Objects.requireNonNull(expression, "expression cannot be null in an assignment");
    }

    public String toString() {
        return variable + " = " + expression;
    }
}
