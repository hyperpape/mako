package com.justinblank.classcompiler.lang;

import java.util.Objects;

public class Assignment extends Statement {

    public final String variable;
    public final Expression expression;

    public Assignment(String variable, Expression expression) {
        Objects.requireNonNull(variable, "variable cannot be null in an assignment");
        Objects.requireNonNull(expression, "expression cannot be null in an assignment");
        this.variable = variable;
        this.expression = expression;
    }

    public String toString() {
        return variable + " = " + expression;
    }
}
