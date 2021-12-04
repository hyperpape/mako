package com.justinblank.classcompiler.lang;

public class Assignment extends Statement {

    public final String variable;
    public final Expression expression;

    public Assignment(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }
}
